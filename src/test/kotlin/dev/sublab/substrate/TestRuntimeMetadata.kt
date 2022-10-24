package dev.sublab.substrate

import dev.sublab.scale.ScaleCodec
import dev.sublab.scale.helpers.decodeHex
import dev.sublab.substrate.metadata.RuntimeMetadata
import dev.sublab.substrate.rpcClient.RpcClient
import dev.sublab.substrate.support.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertEquals

class TestRuntimeMetadata {

    private val client = RpcClient(Constants.kusamaUrl)
    private val codec = ScaleCodec.default()

    @Test
    fun testRuntimeMetadataParsing() = runBlocking {
        val response = withContext(Dispatchers.IO) {
            client.sendRequest<Unit, String> {
                method = "state_getMetadata"
                responseType = String::class
            }
        }

        val metadataEncoded = response.decodeHex()

        try {
            val metadataDecoded = codec.fromScale(metadataEncoded, RuntimeMetadata::class)

            println("metadata version: ${metadataDecoded.version}")
            assertEquals(metadataDecoded.version, 14u)
        } catch (e: Exception) {
            println("Exception: $e")
            throw e
        }
    }
}