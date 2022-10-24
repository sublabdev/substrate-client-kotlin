package dev.sublab.substrate.metadata.modules

import dev.sublab.scale.helpers.decodeHex
import java.math.BigInteger

data class RuntimeModuleConstant(
    val name: String,
    val type: BigInteger,
    private val valueHex: String,
    val docs: List<String>
) {

    val value get() = valueHex.decodeHex()
}