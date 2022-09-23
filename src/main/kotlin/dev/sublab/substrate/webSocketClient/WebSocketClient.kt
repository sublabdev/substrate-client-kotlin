package dev.sublab.substrate.webSocketClient

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

private typealias Subscriber = (String) -> Unit
private val clientScope = CoroutineScope(Dispatchers.IO)

class WebSocketClient(
    url: String,
    port: Int? = null
) {

    private val receiveScope = CoroutineScope(Job())
    private val sendScope = CoroutineScope(Job())

    private val client = HttpClient {
        install(WebSockets)
    }

    private val subscribers = mutableListOf<Subscriber>()
    private val output: Queue<String> = LinkedList()

    init {
        clientScope.launch {
            client.webSocket(host = url, port = port) {
                receiveScope.launch {
                    receive(this@webSocket)
                }

                sendScope.launch {
                    send(this@webSocket)
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
            client.send(Frame.Text(message))
        }
    }

    fun send(message: String) {
        output.add(message)
    }

    fun subscribe(onReceive: Subscriber) {
        subscribers.add(onReceive)
    }
}