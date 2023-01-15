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