package dev.sublab.substrate.metadata.lookup.type.def

import java.math.BigInteger

data class RuntimeTypeDefField(
    val name: String?,
    val type: BigInteger,
    val typeName: String?,
    val docs: List<String>
)