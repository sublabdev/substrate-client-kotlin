package dev.sublab.substrate.metadata.lookup.type.def

import dev.sublab.scale.dataTypes.UInt32
import java.math.BigInteger

data class RuntimeTypeDefArray(
    val length: UInt32,
    val type: BigInteger
)