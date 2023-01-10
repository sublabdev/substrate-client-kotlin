package dev.sublab.substrate.metadata

import dev.sublab.common.numerics.UInt8
import java.math.BigInteger

data class RuntimeExtrinsic(
    val type: BigInteger,
    val version: UInt8,
    val signedExtensions: List<SignedExtension>
) {

    data class SignedExtension(
        val identifier: String,
        val type: BigInteger,
        val additionalSigned: BigInteger
    )
}