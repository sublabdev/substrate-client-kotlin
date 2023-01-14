package dev.sublab.substrate

import dev.sublab.scale.ScaleCodec
import dev.sublab.substrate.metadata.modules.RuntimeModuleConstant
import kotlinx.coroutines.flow.*
import kotlin.reflect.KClass

/**
 * Substrate constants service. Handles fetching runtime module constant
 */
class SubstrateConstantsService(
    private val codec: ScaleCodec<ByteArray>,
    private val lookup: SubstrateLookupService
) {

    /**
     * Finds a runtime module constant by the constant's name in a specified module
     * @param moduleName module's name in which the constant should be looked for
     * @param constantName constant name by which the constant should be found
     * @return a cruntime module constant
     */
    fun find(moduleName: String, constantName: String) = lookup.findConstant(moduleName, constantName)
    fun <T: Any> fetch(moduleName: String, constantName: String, type: KClass<T>) = find(moduleName, constantName)
        .map {
            it?.let { fetch(it, type) }
        }

    /**
     * Decodes the value bytes of a runtime module constant into a specified type
     */
    fun <T: Any> fetch(constant: RuntimeModuleConstant, type: KClass<T>)
        = codec.fromScale(constant.value, type)
}