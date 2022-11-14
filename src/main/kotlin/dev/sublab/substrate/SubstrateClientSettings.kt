package dev.sublab.substrate

import dev.sublab.substrate.storage.InMemoryObjectStorageFactory
import dev.sublab.substrate.storage.ObjectStorageFactory

data class SubstrateClientSettings(
    val webSocketPath: String?,
    val webSocketPort: Int?,
    val runtimeMetadataUpdateTimeoutMs: Long,
    val namingPolicy: SubstrateClientNamingPolicy,
    val objectStorageFactory: ObjectStorageFactory
) {
    companion object {
        internal fun default() = SubstrateClientSettings(
            webSocketPath = null,
            webSocketPort = null,
            runtimeMetadataUpdateTimeoutMs = 3600 * 1000,
            namingPolicy = SubstrateClientNamingPolicy.CASE_INSENSITIVE,
            objectStorageFactory = InMemoryObjectStorageFactory()
        )
    }
}