package dev.sublab.substrate.metadata.modules

import dev.sublab.substrate.metadata.modules.storage.RuntimeModuleStorage
import java.math.BigInteger

data class RuntimeModule(
    val name: String,
    val storage: RuntimeModuleStorage?,
    private val callIndex: BigInteger?,
    private val eventsIndex: BigInteger?,
    val constants: List<RuntimeModuleConstant>,
    private val errorsIndex: BigInteger?,
    private val indexUByte: UByte
) {

    val index get() = indexUByte.toUInt()
}