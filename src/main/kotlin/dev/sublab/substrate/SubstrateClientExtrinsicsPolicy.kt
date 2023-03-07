package dev.sublab.substrate

import dev.sublab.substrate.extrinsics.NonceNotKnownException

/**
 * Policy with set of rules for Extrinsics service
 */
data class SubstrateClientExtrinsicsPolicy(
    val nonceResolving: NonceResolvePolicy
) {
    /**
     * Policy to determine what to do if nonce is unknown during signing extrinsic for an account which keeps zero balance
     */
    enum class NonceResolvePolicy {
        THROW_ERROR_IF_UNKNOWN,
        SET_TO_ZERO
    };

    companion object {
        /**
         * Unsafe Extrinsics policy with nonce being set to 0 if account is not found
         */
        fun unsafe() = SubstrateClientExtrinsicsPolicy(
            nonceResolving = NonceResolvePolicy.SET_TO_ZERO
        )

        /**
         * Safe Extrinsics policy with throwing [NonceNotKnownException] when couldn't resolve nonce
         *
         * Considered safe as this wouldn't create extrinsic with failing nonce for existing account with real nonce
         * if resolving just failed
         */
        fun safe() = SubstrateClientExtrinsicsPolicy(
            nonceResolving = NonceResolvePolicy.THROW_ERROR_IF_UNKNOWN
        )
    }
}