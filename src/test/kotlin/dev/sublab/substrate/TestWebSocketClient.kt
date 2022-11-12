package dev.sublab.substrate

import dev.sublab.substrate.support.Constants
import dev.sublab.substrate.webSocketClient.WebSocketClient
import dev.sublab.substrate.webSocketClient.WebSocketClientSubscriptionPolicy
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestWebSocketClient {

    private fun echoClient(policy: WebSocketClientSubscriptionPolicy = WebSocketClientSubscriptionPolicy.NONE)
        = WebSocketClient(host = Constants.webSocketUrl, port = Constants.webSocketPort, policy = policy)

    @Test
    internal fun testOne() {
        val lock = CountDownLatch(1)
        val testMessage = UUID.randomUUID().toString()

        val echoClient = echoClient()
        echoClient.subscribe {
            assertEquals(testMessage, it)
            lock.countDown()
        }
        echoClient.send(testMessage)

        assertTrue(lock.await(Constants.singleTestTimeout, TimeUnit.SECONDS))
    }

    @Test
    internal fun testNone() {
        val lock = CountDownLatch(1)
        val testMessage = UUID.randomUUID().toString()

        val echoClient = echoClient()
        echoClient.send(testMessage)

        // Let message be sent and received back
        lock.await(Constants.singleTestTimeout, TimeUnit.SECONDS)

        echoClient.subscribe {
            // Shouldn't receive any messages
            assertEquals(testMessage, it)
            assert(false)
            lock.countDown()
        }

        lock.await(Constants.singleTestTimeout, TimeUnit.SECONDS)
    }

    @Test
    internal fun testFirst() {
        val lock = CountDownLatch(Constants.testsCount)
        val testMessage = UUID.randomUUID().toString()

        val echoClient = echoClient(policy = WebSocketClientSubscriptionPolicy.FIRST_SUBSCRIBER)
        echoClient.send(testMessage)

        // Let message be sent and received back
        lock.await(Constants.singleTestTimeout, TimeUnit.SECONDS)

        for (i in 0 until Constants.testsCount) {
            echoClient.subscribe {
                // Should receive message only on first subscription
                assertEquals(testMessage, it)
                assertEquals(i, 0)
                lock.countDown()
            }
        }

        lock.await(Constants.singleTestTimeout, TimeUnit.SECONDS)
    }

    @Test
    internal fun testAll() {
        val lock = CountDownLatch(Constants.testsCount)
        val testMessage = UUID.randomUUID().toString()

        val echoClient = echoClient(policy = WebSocketClientSubscriptionPolicy.ALL_SUBSCRIBERS)
        echoClient.send(testMessage)

        // Let message be sent and received back
        lock.await(Constants.singleTestTimeout, TimeUnit.SECONDS)

        for (i in 0 until Constants.testsCount) {
            echoClient.subscribe {
                // Should receive message every subscription
                assertEquals(testMessage, it)
                lock.countDown()
            }
        }

        assertTrue(lock.await(Constants.singleTestTimeout, TimeUnit.SECONDS))
    }

    @Test
    internal fun test() {
        val lock = CountDownLatch(Constants.testsCount)
        val echoClient = echoClient(policy = WebSocketClientSubscriptionPolicy.FIRST_SUBSCRIBER)

        val testMessages = HashSet<String>()
        for (i in 0 until Constants.testsCount) {
            val testMessage = UUID.randomUUID().toString()
            echoClient.send(testMessage)
            testMessages.add(testMessage)
        }

        // Let messages be sent and received back
        lock.await(Constants.singleTestTimeout, TimeUnit.SECONDS)
        
        echoClient.subscribe {
            // Should receive all messages
            assert(testMessages.contains(it))
            lock.countDown()
        }

        echoClient.subscribe {
            // Shouldn't receive anything
            assert(false)
        }

        assertTrue(lock.await(Constants.singleTestTimeout, TimeUnit.SECONDS))
    }
}