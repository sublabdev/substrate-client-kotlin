package dev.sublab.substrate.metadata

import dev.sublab.common.numerics.UInt8
import java.math.BigInteger

/**
 * Runtime extrinsic. Contains its type, version and an array of signed extensions
 */
data class RuntimeExtrinsic(
    val type: BigInteger,
    val version: UInt8,
    val signedExtensions: List<SignedExtension>
) {

    /**
     * Signed extrinsic. Contains its identifier, type and an additional signed
     */
    data class SignedExtension(
        val identifier: String,
        val type: BigInteger,
        val additionalSigned: BigInteger
    )
}