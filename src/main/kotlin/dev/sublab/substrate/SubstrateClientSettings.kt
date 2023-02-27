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

import dev.sublab.substrate.storage.InMemoryObjectStorageFactory
import dev.sublab.substrate.storage.ObjectStorageFactory

/**
 * Substrate client settings
 */
data class SubstrateClientSettings(
    val rpcPath: String?,
    val rpcParams: Map<String, Any?>,
    val webSocketSecure: Boolean,
    val webSocketPath: String?,
    val webSocketParams: Map<String, Any?>,
    val webSocketPort: Int?,
    val runtimeMetadataUpdateTimeoutMs: Long,
    val namingPolicy: SubstrateClientNamingPolicy,
    val lookupPolicy: SubstrateClientLookupPolicy,
    val extrinsicsPolicy: SubstrateClientExtrinsicsPolicy,
    val objectStorageFactory: ObjectStorageFactory
) {
    companion object {
        /**
         * The default settings for substrate client
         */
        fun default() = SubstrateClientSettings(
            rpcPath = null,
            rpcParams = mapOf(),
            webSocketPath = null,
            webSocketParams = mapOf(),
            webSocketSecure = false,
            webSocketPort = null,
            runtimeMetadataUpdateTimeoutMs = 3600 * 1000,
            namingPolicy = SubstrateClientNamingPolicy.CASE_INSENSITIVE,
            lookupPolicy = SubstrateClientLookupPolicy.safe(),
            extrinsicsPolicy = SubstrateClientExtrinsicsPolicy.safe(),
            objectStorageFactory = InMemoryObjectStorageFactory()
        )
    }
}