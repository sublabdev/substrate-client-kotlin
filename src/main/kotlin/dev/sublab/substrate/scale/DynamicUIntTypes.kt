package dev.sublab.substrate.scale

import dev.sublab.common.FromByteArray
import java.math.BigInteger

/**
 * Index type, like nonce
 */
@DynamicType(lookupIndex = 4)
class Index(byteArray: ByteArray): FromByteArray(byteArray) {
    constructor(value: BigInteger) : this(value.toByteArray().reversedArray())

    val value = BigInteger(byteArray.reversedArray())
    // Required to convert to and from actual type
    override fun toByteArray(): ByteArray = value.toByteArray().reversedArray()
}

/**
 * Balance representation, used in transfers, etc
 */
@DynamicType(lookupIndex = 6)
class Balance(byteArray: ByteArray): FromByteArray(byteArray) {
    constructor(value: BigInteger) : this(value.toByteArray().reversedArray())

    val value = BigInteger(byteArray.reversedArray())
    // Required to convert to and from actual type
    override fun toByteArray(): ByteArray = value.toByteArray().reversedArray()
}