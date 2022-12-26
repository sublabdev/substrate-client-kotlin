package dev.sublab.substrate

import dev.sublab.scale.ScaleCodec
import dev.sublab.substrate.metadata.RuntimeMetadata
import dev.sublab.substrate.rpcClient.RpcClient
import dev.sublab.substrate.support.Constants
import dev.sublab.substrate.support.allNetworks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path.Companion.toPath
import kotlin.test.Test
import kotlin.test.assertEquals

internal class TestRuntimeMetadata {
    private val codec = ScaleCodec.hex()

    @Test
    fun testLocalParsing() = runBlocking {
        val fs = FileSystem.SYSTEM
        for (network in allNetworks()) {
            val path = (Constants.resourcesPath + network.localRuntimeMetadataSnapshot.path).toPath()
            if (!fs.exists(path)) {
                assert(false)
                continue
            }

            val metadataEncoded = fs.read(path) { readUtf8() }
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
    fun testRemoteParsing() = runBlocking {
        for (network in allNetworks()) {
            val response = withContext(Dispatchers.IO) {
                val client = RpcClient(network.rpcUrl)
                client.sendRequest<Unit, String> {
                    method = "state_getMetadata"
                    responseType = String::class
                }
            }

            try {
                val metadataDecoded = codec.fromScale(response, RuntimeMetadata::class)

                println("metadata from ${network.rpcUrl} magic number: ${metadataDecoded.magicNumber}, version: ${metadataDecoded.version}")
                assertEquals(metadataDecoded.version, 14u)
            } catch (e: Exception) {
                println("Exception: $e")
                throw e
            }
        }
    }
}