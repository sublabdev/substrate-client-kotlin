package dev.sublab.substrate.metadata.modules

import java.math.BigInteger

data class RuntimeModuleConstant(
    val name: String,
    val type: BigInteger,
    val value: List<Byte>,
    val docs: List<String>
)