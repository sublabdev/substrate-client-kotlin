package dev.sublab.substrate.metadata.modules

import dev.sublab.common.numerics.*
import dev.sublab.substrate.metadata.modules.storage.RuntimeModuleStorage
import java.math.BigInteger

data class RuntimeModule(
    val name: String,
    val storage: RuntimeModuleStorage?,
    val callIndex: BigInteger?,
    val eventsIndex: BigInteger?,
    val constants: List<RuntimeModuleConstant>,
    val errorsIndex: BigInteger?,
    val indexUInt8: UInt8
) {

    val index: UInt32 get() = indexUInt8.toUInt()
}