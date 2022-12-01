package dev.sublab.substrate.webSocketClient

import extra.kotlin.collection.ArrayListQueue
import extra.kotlin.collection.Queue
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*

private typealias Subscriber = (String) -> Unit
private typealias ErrorSubscriber = (Throwable) -> Unit

enum class WebSocketClientSubscriptionPolicy {
    NONE,
    FIRST_SUBSCRIBER,
    ALL_SUBSCRIBERS
}

class WebSocketClient(
    host: String,
    path: String? = null,
    port: Int? = null,
    private val policy: WebSocketClientSubscriptionPolicy = WebSocketClientSubscriptionPolicy.NONE
) {

    private val clientScope = CoroutineScope(Job())

    private val client = HttpClient {
        install(WebSockets)
    }

    private val subscribers = mutableListOf<Subscriber>()
    private val errorSubscribers = mutableListOf<ErrorSubscriber>()

    private val input: Queue<String> = ArrayListQueue()
    private val output: Queue<String> = ArrayListQueue()

    init {
        clientScope.launch {
            client.webSocket(host = host, path = path, port = port) {
                try {
                    val receive = launch { receive(this@webSocket) }
                    val send = launch { send(this@webSocket) }

                    receive.join()
                    send.cancelAndJoin()
                } catch (e: Exception) {
                    errorSubscribers.forEach { it(e) }
                }
            }
        }
    }

    private suspend fun receive(client: ClientWebSocketSession) {
        try {
            for (message in client.incoming) {
                message as? Frame.Text ?: continue
                val text = message.readText()

                if (subscribers.isEmpty()) {
                    if (policy != WebSocketClientSubscriptionPolicy.NONE) {
                        input.add(text)
                    }
                } else {
                    for (subscriber in subscribers) {
                        subscriber(text)
                    }
                }
            }
        } catch (e: Exception) {
            println(e)
        }
    }

    private suspend fun send(client: ClientWebSocketSession) {
        while (true) {
            val message = output.poll() ?: continue
            try {
                client.send(message)
            } catch (e: Exception) {
                errorSubscribers.forEach { it(e) }
            }
        }
    }

    fun send(message: String) {
        output.add(message)
    }

    fun subscribe(onError: ErrorSubscriber? = null, onReceive: Subscriber? = null) {
        addSubscriber(onReceive)
        addErrorSubscriber(onError)
    }

    private fun addSubscriber(subscriber: Subscriber?) {
        val subscriber = subscriber ?: return
        if (input.isNotEmpty()) {
            fun sendMessages() {
                for (message in input) {
                    subscriber(message)
                }
            }

            when (policy) {
                WebSocketClientSubscriptionPolicy.ALL_SUBSCRIBERS -> sendMessages()
                WebSocketClientSubscriptionPolicy.FIRST_SUBSCRIBER -> {
                    if (subscribers.isEmpty()) {
                        sendMessages()
                        input.clear()
                    }
                }
                else -> {}
            }
        }

        subscribers.add(subscriber)
    }

    private fun addErrorSubscriber(errorSubscriber: ErrorSubscriber?) {
        val subscriber = errorSubscriber ?: return
        errorSubscribers.add(subscriber)
    }
}