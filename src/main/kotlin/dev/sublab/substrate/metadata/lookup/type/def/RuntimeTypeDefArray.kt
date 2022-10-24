package dev.sublab.substrate.metadata.lookup.type.def

import java.math.BigInteger

data class RuntimeTypeDefArray(
    val length: UInt,
    val type: BigInteger
)