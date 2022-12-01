package dev.sublab.substrate

import dev.sublab.hex.hex
import dev.sublab.scale.ScaleCodec
import dev.sublab.substrate.metadata.RuntimeMetadata
import dev.sublab.substrate.rpcClient.RpcClient
import dev.sublab.substrate.support.Constants
import dev.sublab.substrate.support.allNetworks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class TestRuntimeMetadata {

    private val codec = ScaleCodec.default()

    @Test
    internal fun testLocalParsing() = runBlocking {
        for (network in allNetworks()) {
            val file = File(Constants.resourcesPath + network.localRuntimeMetadataSnapshot.path)
            if (!file.exists()) {
                assert(false)
                continue
            }

            val metadataEncoded = file.readText().hex.decode()
            try {
                val metadataDecoded = codec.fromScale(metadataEncoded, RuntimeMetadata::class)

                println("metadata from ${network.rpcUrl} magic number: ${metadataDecoded.magicNumber}, version: ${metadataDecoded.version}")
                assertEquals(metadataDecoded.version, 14u)
                assertEquals(metadataDecoded.magicNumber, network.localRuntimeMetadataSnapshot.magicNumber)
            } catch (e: Exception) {
                println("Exception: $e")
                throw e
            }
        }
    }

    @Test
    internal fun testRemoteParsing() = runBlocking {
        for (network in allNetworks()) {
            val response = withContext(Dispatchers.IO) {
                val client = RpcClient(network.rpcUrl)
                client.sendRequest<Unit, String> {
                    method = "state_getMetadata"
                    responseType = String::class
                }
            }

            val metadataEncoded = response.hex.decode()

            try {
                val metadataDecoded = codec.fromScale(metadataEncoded, RuntimeMetadata::class)

                println("metadata from ${network.rpcUrl} magic number: ${metadataDecoded.magicNumber}, version: ${metadataDecoded.version}")
                assertEquals(metadataDecoded.version, 14u)
            } catch (e: Exception) {
                println("Exception: $e")
                throw e
            }
        }
    }
}