package dev.sublab.substrate

import dev.sublab.scale.ScaleCodec
import dev.sublab.substrate.metadata.RuntimeMetadata
import dev.sublab.substrate.metadata.modules.RuntimeModule
import dev.sublab.substrate.metadata.modules.RuntimeModuleConstant
import kotlinx.coroutines.flow.*
import kotlin.reflect.KClass

private data class ConstantPath(
    val moduleName: String,
    val constantName: String
)

class SubstrateConstantsService(
    private val codec: ScaleCodec<ByteArray>,
    private val runtimeMetadata: Flow<RuntimeMetadata>,
    private val namingPolicy: SubstrateClientNamingPolicy,
) {

    private var modulesCache: MutableMap<String, RuntimeModule> = mutableMapOf()
    private var constantsCache: MutableMap<ConstantPath, RuntimeModuleConstant> = mutableMapOf()

    private fun equals(lhs: String, rhs: String) = when (namingPolicy) {
        SubstrateClientNamingPolicy.NONE -> lhs == rhs
        SubstrateClientNamingPolicy.CASE_INSENSITIVE -> lhs.lowercase() == rhs.lowercase()
    }

    fun find(moduleName: String, constantName: String): Flow<RuntimeModuleConstant?> = runtimeMetadata.map { metadata ->
        val module = modulesCache[moduleName]
            ?: metadata.modules.firstOrNull { equals(it.name, moduleName) }
            ?: return@map null

        modulesCache[moduleName] = module

        val constantPath = ConstantPath(moduleName, constantName)
        val constant = constantsCache[constantPath]
            ?: module.constants.firstOrNull { equals(it.name, constantName) }
            ?: return@map null

        constantsCache[constantPath] = constant

        constant
    }

    fun <T: Any> fetch(moduleName: String, constantName: String, type: KClass<T>) = find(moduleName, constantName)
        .map {
            it?.let { fetch(it, type) }
        }

    fun <T: Any> fetch(constant: RuntimeModuleConstant, type: KClass<T>)
        = codec.fromScale(constant.value.toByteArray(), type)
}