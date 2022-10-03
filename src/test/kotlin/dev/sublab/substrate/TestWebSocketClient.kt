package dev.sublab.substrate

import dev.sublab.substrate.support.Constants
import dev.sublab.substrate.webSocketClient.WebSocketClient
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestWebSocketClient {

    private val echoClient get() = WebSocketClient(host = Constants.webSocketUrl, port = Constants.webSocketPort)

    @Test
    fun testEchoClient() {
        val lock = CountDownLatch(Constants.webSocketTestsCount)

        for (i in 0 until Constants.webSocketTestsCount) {
            val testMessage = UUID.randomUUID().toString()
            val echoClient = echoClient
            echoClient.subscribe {
                assertEquals(it, testMessage)
                lock.countDown()
            }
            echoClient.send(testMessage)
        }

        assertTrue(lock.await(10, TimeUnit.SECONDS))
    }
}