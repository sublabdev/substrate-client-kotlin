package dev.sublab.substrate.metadata.modules

import dev.sublab.scale.dataTypes.Int8
import java.math.BigInteger

data class RuntimeModuleConstant(
    val name: String,
    val type: BigInteger,
    private val valueListOfInt8: List<Int8>,
    val docs: List<String>
) {

    val value get() = valueListOfInt8.toByteArray()
}