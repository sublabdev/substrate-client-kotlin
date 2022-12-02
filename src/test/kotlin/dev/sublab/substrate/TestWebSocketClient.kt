package dev.sublab.substrate

import dev.sublab.substrate.support.Constants
import dev.sublab.substrate.webSocketClient.WebSocketClient
import dev.sublab.substrate.webSocketClient.WebSocketClientSubscriptionPolicy
import extra.kotlin.util.UUID
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TestWebSocketClient {
    private fun echoClient(policy: WebSocketClientSubscriptionPolicy = WebSocketClientSubscriptionPolicy.NONE)
        = WebSocketClient(host = Constants.webSocketUrl, port = Constants.webSocketPort, policy = policy)

    @Test
    internal fun testOne() = runBlocking {
        val testMessage = UUID.uuid4().toString()

        val echoClient = echoClient()

        echoClient.send(testMessage)

        val response = withTimeout(Constants.singleTestTimeout) {
            echoClient.subscribe().first()
        }

        assertEquals(testMessage, response)
    }

    @Test
    internal fun testNone() = runBlocking {
        val testMessage = UUID.uuid4().toString()

        val echoClient = echoClient()

        echoClient.send(testMessage)

        // Let message be echoed before we subscribe
        delay(Constants.singleTestTimeout)

        val response = withTimeoutOrNull(Constants.singleTestTimeout) {
            echoClient.subscribe().first()
        }

        assertNull(response)
    }

    @Test
    internal fun testFirst(): Unit = runBlocking {
        val testMessage = UUID.uuid4().toString()

        val echoClient = echoClient(policy = WebSocketClientSubscriptionPolicy.FIRST_SUBSCRIBER)
        echoClient.send(testMessage)

        // Let message be echoed before we subscribe
        delay(Constants.singleTestTimeout)

        val firstResponse = withTimeout(Constants.singleTestTimeout) {
            echoClient.subscribe().first()
        }

        assertEquals(testMessage, firstResponse)

        val unexpectedResponses = withTimeoutOrNull(Constants.singleTestTimeout) {
            val subscriptions = (1 until Constants.testsCount).map {
                echoClient.subscribe()
            }

            combine(subscriptions) { it }.first()
        }

        unexpectedResponses?.let { responses ->
            // this is unexpected behavior, but still let's do some asserts
            assertEquals(responses.size,  Constants.testsCount - 1)
            responses.forEach {
                assertEquals(testMessage, it)
            }
        }

        assertNull(unexpectedResponses)
    }

    @Test
    internal fun testAll() = runBlocking {
        val testMessage = UUID.uuid4().toString()

        val echoClient = echoClient(policy = WebSocketClientSubscriptionPolicy.ALL_SUBSCRIBERS)
        echoClient.send(testMessage)

        // Let message be echoed before we subscribe
        delay(Constants.singleTestTimeout)

        val responses = withTimeout(Constants.singleTestTimeout) {
            val subscriptions = (0 until Constants.testsCount).map {
                echoClient.subscribe()
            }

            combine(subscriptions) { it }.first()
        }

        assertEquals(Constants.testsCount, responses.size)
        responses.forEach {
            assertEquals(testMessage, it)
        }
    }

    @Test
    internal fun `verify first subscriber gets message, rest don't`() = runBlocking{
        val echoClient = echoClient(policy = WebSocketClientSubscriptionPolicy.FIRST_SUBSCRIBER)

        val testMessages = HashSet<String>()
        for (i in 0 until Constants.testsCount) {
            val testMessage = UUID.uuid4().toString()
            echoClient.send(testMessage)
            testMessages.add(testMessage)
        }

        // Let message be echoed before we subscribe
        delay(Constants.singleTestTimeout)

        // Enter with timeout, so if we don't take all "1000" messages, timeout is thrown
        withTimeout(Constants.singleTestTimeout) {
            // Take first "1000" messages to unblock that scope and leave timeout with success
            echoClient.subscribe().take(testMessages.size).collect {
                // Should receive all messages
                assert(testMessages.contains(it))
                testMessages.remove(it)
            }
        }

        // Double check if all messages were received
        assertEquals(true, testMessages.isEmpty())

        val unexpectedResponse = withTimeoutOrNull(Constants.singleTestTimeout) {
            echoClient.subscribe().first()
        }

        // Shouldn't receive anything
        assertNull(unexpectedResponse)
    }
}