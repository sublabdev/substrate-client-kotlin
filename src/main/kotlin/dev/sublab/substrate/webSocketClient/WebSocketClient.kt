/**
 *
 * Copyright 2023 SUBSTRATE LABORATORY LLC <info@sublab.dev>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package dev.sublab.substrate.webSocketClient

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Web socket client subscription policy. Can be: "NONE"; "FIRST_SUBSCRIBER"; and "ALL_SUBSCRIBERS"
 */
enum class WebSocketClientSubscriptionPolicy {
    NONE,
    FIRST_SUBSCRIBER,
    ALL_SUBSCRIBERS
}

interface WebSocket {
    suspend fun send(message: String)
    fun subscribe(): Flow<String>
    fun subscribeToErrors(): Flow<Throwable>
}

/**
 * Web socket client
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal class WebSocketClient(
    secure: Boolean = false,
    host: String,
    path: String? = null,
    params: Map<String, Any?> = mapOf(),
    port: Int? = null,
    private val policy: WebSocketClientSubscriptionPolicy = WebSocketClientSubscriptionPolicy.NONE
): WebSocket {

    private val clientScope = CoroutineScope(Job())

    private val client = HttpClient {
        install(WebSockets)
    }

    private val input = MutableSharedFlow<String>(replay = Int.MAX_VALUE, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val error = Channel<Throwable>()
    private val output = Channel<String>()

    init {
        clientScope.launch {
            var fullPath = path
            if (fullPath != null && params.isNotEmpty()) {
                fullPath += "?${
                    params.map { "${it.key}=${it.value}" }.joinToString("&")
                }"
            }

            val setupBlock: suspend DefaultClientWebSocketSession.() -> Unit = {
                val session = this
                try {
                    val receive = launch { receive(session) }
                    val send = launch { send(session) }

                    receive.join()
                    send.cancelAndJoin()
                } catch (e: Exception) {
                    error.send(e)
                }
            }

            if (secure) {
                client.wss(host = host, path = fullPath, port = port, block = setupBlock)
            } else {
                client.webSocket(host = host, path = fullPath, port = port, block = setupBlock)
            }
        }
    }

    private var hadSubscriptions = false

    private suspend fun receive(client: ClientWebSocketSession) {
        try {
            for (message in client.incoming) {
                message as? Frame.Text ?: continue
                val text = message.readText()

                var resetReplayCache = policy == WebSocketClientSubscriptionPolicy.NONE
                if (!resetReplayCache) {
                    resetReplayCache = policy == WebSocketClientSubscriptionPolicy.FIRST_SUBSCRIBER && hadSubscriptions
                }

                input.emit(text)
                if (resetReplayCache) {
                    input.resetReplayCache()
                }
            }
        } catch (e: Exception) {
            error.send(e)
        }
    }

    private suspend fun send(client: ClientWebSocketSession) = output.consumeEach { message ->
        try {
            client.send(message)
        } catch (e: Exception) {
            error.send(e)
        }
    }

    override suspend fun send(message: String) = output.send(message)

    /**
     * Subscribes for updates upon recieving messages
     */
    override fun subscribe(): Flow<String> = input.apply {
        when (policy) {
            WebSocketClientSubscriptionPolicy.FIRST_SUBSCRIBER -> {
                if (!hadSubscriptions) {
                    // If it's first subscription, just toggle this
                    // So when message is received, cache is reset
                    hadSubscriptions = true
                } else {
                    // If there was a subscriber already, simply reset before returning
                    // So no one else receives it
                    input.resetReplayCache()
                }
            }
            // Always reset to prevent getting messages
            WebSocketClientSubscriptionPolicy.NONE -> input.resetReplayCache()
            // In that case never reset cache
            WebSocketClientSubscriptionPolicy.ALL_SUBSCRIBERS -> {}
        }
    }

    override fun subscribeToErrors() = error.receiveAsFlow()
}