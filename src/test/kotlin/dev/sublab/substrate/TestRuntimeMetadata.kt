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

import dev.sublab.scale.ScaleCodec
import dev.sublab.substrate.metadata.RuntimeMetadata
import dev.sublab.substrate.support.Constants
import dev.sublab.substrate.support.allNetworks
import kotlinx.coroutines.runBlocking
import okio.FileSystem
import okio.Path.Companion.toPath
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

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

                println("metadata from ${network.url} magic number: ${metadataDecoded.magicNumber}, version: ${metadataDecoded.version}")
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
            val client = network.makeRpcClient()
            val response = client.sendRequest<Unit, String> {
                method = "state_getMetadata"
                responseType = String::class
            }

            assertNotNull(response)

            try {
                val metadataDecoded = codec.fromScale(response, RuntimeMetadata::class)

                println("metadata from ${network.url} magic number: ${metadataDecoded.magicNumber}, version: ${metadataDecoded.version}")
                assertEquals(metadataDecoded.version, 14u)
            } catch (e: Exception) {
                println("Exception: $e")
                throw e
            }
        }
    }

    @Test
    fun testRuntimeVersion() = runBlocking {
        for (network in allNetworks()) {
            val client = network.makeClient()
            val runtimeVersion = client.modules.systemRpc().runtimeVersion()
            assertNotNull(runtimeVersion)
        }
    }

    @Test
    fun testGenesisHash() = runBlocking {
        for (network in allNetworks()) {
            val client = network.makeClient()
            val genesisHash = client.modules.chainRpc().getBlockHash(0)
            assertNotNull(genesisHash)
            assertEquals(network.genesisHash, genesisHash)
        }
    }
}