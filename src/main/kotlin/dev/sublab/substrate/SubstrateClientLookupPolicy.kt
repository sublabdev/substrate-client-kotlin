package dev.sublab.substrate

import dev.sublab.substrate.metadata.RuntimeMetadata

/**
 * Policy with set of rules for Lookup service
 */
data class SubstrateClientLookupPolicy(
    val cachePolicy: CachePolicy
) {
    /**
     * Policy to determine what to do with Lookup cache when [RuntimeMetadata] updates
     */
    enum class CachePolicy {
        RESET_ON_METADATA_UPDATE,
        KEEP_FOREVER
    }

    companion object {
        /**
         * Unsafe Lookup policy with cache being kept forever
         */
        fun unsafe() = SubstrateClientLookupPolicy(
            cachePolicy = CachePolicy.KEEP_FOREVER
        )

        /**
         * Safe Lookup policy with cache being reset on every [RuntimeMetadata] update
         */
        fun safe() = SubstrateClientLookupPolicy(
            cachePolicy = CachePolicy.RESET_ON_METADATA_UPDATE
        )
    }
}