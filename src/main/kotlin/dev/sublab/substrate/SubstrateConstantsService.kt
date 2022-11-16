package dev.sublab.substrate

import dev.sublab.scale.ScaleCodec
import dev.sublab.substrate.metadata.modules.RuntimeModuleConstant
import kotlinx.coroutines.flow.*
import kotlin.reflect.KClass

class SubstrateConstantsService(
    private val codec: ScaleCodec<ByteArray>,
    private val lookup: SubstrateLookupService
) {

    fun find(moduleName: String, constantName: String) = lookup.findConstant(moduleName, constantName)
    fun <T: Any> fetch(moduleName: String, constantName: String, type: KClass<T>) = find(moduleName, constantName)
        .map {
            it?.let { fetch(it, type) }
        }

    fun <T: Any> fetch(constant: RuntimeModuleConstant, type: KClass<T>)
        = codec.fromScale(constant.value, type)
}