package dev.sublab.substrate

import dev.sublab.substrate.metadata.RuntimeMetadata
import dev.sublab.substrate.metadata.modules.RuntimeModule
import dev.sublab.substrate.metadata.modules.RuntimeModuleConstant
import dev.sublab.substrate.metadata.modules.storage.RuntimeModuleStorage
import dev.sublab.substrate.metadata.modules.storage.item.RuntimeModuleStorageItem
import kotlinx.coroutines.flow.*

private data class ModulePath(
    val moduleName: String,
    val childName: String
)

class SubstrateLookupService(
    private val runtimeMetadata: Flow<RuntimeMetadata>,
    private val namingPolicy: SubstrateClientNamingPolicy,
) {

    private fun equals(lhs: String, rhs: String) = when (namingPolicy) {
        SubstrateClientNamingPolicy.NONE -> lhs == rhs
        SubstrateClientNamingPolicy.CASE_INSENSITIVE -> lhs.lowercase() == rhs.lowercase()
    }

    private var modulesCache: MutableMap<String, RuntimeModule> = mutableMapOf()
    private var constantsCache: MutableMap<ModulePath, RuntimeModuleConstant> = mutableMapOf()
    private var storageItemsCache: MutableMap<ModulePath, RuntimeModuleStorageItem> = mutableMapOf()

    private fun findModule(metadata: RuntimeMetadata, name: String): RuntimeModule? {
        val module = modulesCache[name]
            ?: metadata.modules.firstOrNull { equals(it.name, name) }
            ?: return null

        modulesCache[name] = module
        return module
    }

    fun findModule(name: String) = runtimeMetadata.map { metadata ->
        findModule(metadata, name)
    }

    private fun findConstant(module: RuntimeModule, name: String): RuntimeModuleConstant? {
        val constantPath = ModulePath(module.name, name)
        val constant = constantsCache[constantPath]
            ?: module.constants.firstOrNull { equals(it.name, name) }
            ?: return null

        constantsCache[constantPath] = constant
        return constant
    }

    fun findConstant(moduleName: String, constantName: String) = findModule(moduleName).map {
        it?.let {
            findConstant(it, constantName)
        }
    }

    data class FindStorageItemResult(val item: RuntimeModuleStorageItem, val storage: RuntimeModuleStorage)

    private fun findStorageItem(module: RuntimeModule, name: String): FindStorageItemResult? {
        val storage = module.storage ?: return null

        val constantPath = ModulePath(module.name, name)
        val item = storageItemsCache[constantPath]
            ?: module.storage.items.firstOrNull { equals(it.name, name) }
            ?: return null

        storageItemsCache[constantPath] = item

        return FindStorageItemResult(item, storage)
    }

    fun findStorageItem(moduleName: String, itemName: String) = findModule(moduleName).map {
        it?.let {
            findStorageItem(it, itemName)
        }
    }
}