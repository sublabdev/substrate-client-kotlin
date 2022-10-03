package dev.sublab.substrate.webSocketClient

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import java.util.*

private typealias Subscriber = (String) -> Unit

class WebSocketClient(
    host: String,
    path: String? = null,
    port: Int? = null
) {

    private val clientScope = CoroutineScope(Job())

    private val client = HttpClient {
        install(WebSockets)
    }

    private val subscribers = mutableListOf<Subscriber>()
    private val output: Queue<String> = LinkedList()

    init {
        clientScope.launch {
            client.webSocket(host = host, path = path, port = port) {
                try {
                    val receive = launch {
                        receive(this@webSocket)
                    }
                    val send = launch {
                        send(this@webSocket)
                    }

                    receive.join()
                    send.cancelAndJoin()
                } catch (exception: Exception) {
                }
            }
        }
    }

    private suspend fun receive(client: ClientWebSocketSession) {
        try {
            for (message in client.incoming) {
                message as? Frame.Text ?: continue
                val text = message.readText()

                for (subscriber in subscribers) {
                    subscriber(text)
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
            }
        }
    }

    fun send(message: String) {
        output.add(message)
    }

    fun subscribe(onReceive: Subscriber) {
        subscribers.add(onReceive)
    }
}