package dev.sublab.substrate.metadata.lookup.type

import java.math.BigInteger

data class RuntimeTypeParam(
    val name: String,
    val type: BigInteger?
)