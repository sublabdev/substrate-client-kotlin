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

enum class WebSocketClientSubscriptionPolicy {
    NONE,
    FIRST_SUBSCRIBER,
    ALL_SUBSCRIBERS
}

@OptIn(ExperimentalCoroutinesApi::class)
class WebSocketClient(
    secure: Boolean = false,
    host: String,
    path: String? = null,
    params: Map<String, Any?> = mapOf(),
    port: Int? = null,
    private val policy: WebSocketClientSubscriptionPolicy = WebSocketClientSubscriptionPolicy.NONE
) {

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

    suspend fun send(message: String) = output.send(message)

    fun subscribe(): Flow<String> = input.apply {
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

    fun subscribeToErrors() = error.receiveAsFlow()
}