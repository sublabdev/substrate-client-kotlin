package dev.sublab.substrate

import dev.sublab.substrate.webSocketClient.WebSocketClient
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestWebSocketClient {

    private val echoClient get() = WebSocketClient(url = "localhost", port = 8080)

    @Test
    fun testEchoClient() {
        val testMessage = "testing"
        val lock = CountDownLatch(1)

        val echoClient = echoClient
        echoClient.subscribe {
            assertEquals(it, testMessage)
            lock.countDown()
        }

        echoClient.send(testMessage)

        assertTrue(lock.await(5, TimeUnit.SECONDS))
    }
}