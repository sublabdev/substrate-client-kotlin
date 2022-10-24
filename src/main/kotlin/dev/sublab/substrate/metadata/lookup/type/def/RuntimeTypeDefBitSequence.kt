package dev.sublab.substrate.metadata.lookup.type.def

import java.math.BigInteger

data class RuntimeTypeDefBitSequence(
    val store: BigInteger,
    val order: BigInteger
)