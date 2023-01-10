package dev.sublab.substrate

import dev.sublab.substrate.rpcClient.RpcRequest
import dev.sublab.substrate.support.KusamaNetwork
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class TestRpcClient {
    private val network = KusamaNetwork()
    private val client = network.makeRpcClient()

    @Test
    fun testRpcError(): Unit = runBlocking {
        val request = RpcRequest(id = 1, method = "non_existing_method")
        val response = client.send(request)

        assertNotNull(response.error)
    }

    @Test
    fun testRpcRequest() = runBlocking {
        val requestId = (Long.MIN_VALUE..Long.MAX_VALUE).random()
        val request = RpcRequest(id = requestId, method = "state_getMetadata")

        val response = client.send(request)

        val result = response.result ?: throw Throwable(response.error?.toString())

        assertEquals(requestId, response.id)
        val primitive = result.jsonPrimitive
        assertTrue(primitive.isString)
        val stringWithBinary = primitive.content
        assertTrue(stringWithBinary.isNotEmpty())
        assertTrue(stringWithBinary.startsWith("0x"))
    }

    @Test
    fun testSendRequest() = runBlocking {
        val response = client.sendRequest<Unit, String> {
            method = "state_getMetadata"
            responseType = String::class
        }

        assertNotNull(response)
        assertTrue(response.isNotEmpty())
        assertTrue(response.startsWith("0x"))
    }
}