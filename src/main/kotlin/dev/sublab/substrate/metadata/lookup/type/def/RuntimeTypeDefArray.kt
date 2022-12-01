package dev.sublab.substrate.metadata.lookup.type.def

import dev.sublab.common.numerics.UInt32
import java.math.BigInteger

data class RuntimeTypeDefArray(
    val length: UInt32,
    val type: BigInteger
)