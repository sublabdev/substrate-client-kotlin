package dev.sublab.substrate.extrinsics

import dev.sublab.common.numerics.UInt8
import dev.sublab.ss58.AccountId

data class Extrinsic(
    val signed: Boolean = true,
    val version: UInt8 = 4u,
    val address: AccountId,
    val signature: ByteArray,
    val extra: ExtrinsicExtra,
    val payload: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Extrinsic

        if (signed != other.signed) return false
        if (version != other.version) return false
        if (!address.contentEquals(other.address)) return false
        if (!signature.contentEquals(other.signature)) return false
        if (extra != other.extra) return false
        if (!payload.contentEquals(other.payload)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = signed.hashCode()
        result = 31 * result + version.hashCode()
        result = 31 * result + address.contentHashCode()
        result = 31 * result + signature.contentHashCode()
        result = 31 * result + extra.hashCode()
        result = 31 * result + payload.contentHashCode()
        return result
    }
}