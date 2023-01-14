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
            objectStorageFactory = InMemoryObjectStorageFactory()
        )
    }
}