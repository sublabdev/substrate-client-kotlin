package dev.sublab.substrate

import dev.sublab.substrate.metadata.RuntimeMetadata
import dev.sublab.substrate.metadata.modules.RuntimeModule
import dev.sublab.substrate.metadata.modules.RuntimeModuleConstant
import dev.sublab.substrate.metadata.modules.storage.RuntimeModuleStorage
import dev.sublab.substrate.metadata.modules.storage.item.RuntimeModuleStorageItem
import kotlinx.coroutines.flow.*
import java.math.BigInteger

private data class ModulePath(
    val moduleName: String,
    val childName: String
)

class SubstrateLookupService(
    private val runtimeMetadata: Flow<RuntimeMetadata>,
    private val namingPolicy: SubstrateClientNamingPolicy
) {
    private fun lookupAsFlow() = runtimeMetadata.map {
        SubstrateLookup(it, namingPolicy)
    }

    fun findModule(name: String) = lookupAsFlow().map {
        it.findModule(name)
    }

    fun findConstant(moduleName: String, constantName: String) = lookupAsFlow().map {
        it.findConstant(moduleName, constantName)
    }

    fun findStorageItem(moduleName: String, itemName: String) = lookupAsFlow().map {
        it.findStorageItem(moduleName, itemName)
    }

    fun findRuntimeType(index: BigInteger) = lookupAsFlow().map {
        it.findRuntimeType(index)
    }
}

data class FindStorageItemResult(val item: RuntimeModuleStorageItem, val storage: RuntimeModuleStorage)

private class SubstrateLookup(
    private val runtimeMetadata: RuntimeMetadata,
    private val namingPolicy: SubstrateClientNamingPolicy
) {

    private val modulesCache: MutableMap<String, RuntimeModule> = mutableMapOf()
    private val constantsCache: MutableMap<ModulePath, RuntimeModuleConstant> = mutableMapOf()
    private val storageItemsCache: MutableMap<ModulePath, RuntimeModuleStorageItem> = mutableMapOf()

    fun findModule(name: String): RuntimeModule? {
        val module = modulesCache[name]
            ?: runtimeMetadata.modules.firstOrNull { namingPolicy.equals(it.name, name) }
            ?: return null

        modulesCache[name] = module
        return module
    }

    private fun findConstant(module: RuntimeModule, name: String): RuntimeModuleConstant? {
        val constantPath = ModulePath(module.name, name)
        val constant = constantsCache[constantPath]
            ?: module.constants.firstOrNull { namingPolicy.equals(it.name, name) }
            ?: return null

        constantsCache[constantPath] = constant
        return constant
    }

    fun findConstant(moduleName: String, constantName: String) = findModule(moduleName)?.let {
        findConstant(it, constantName)
    }

    private fun findStorageItem(module: RuntimeModule, name: String): FindStorageItemResult? {
        val storage = module.storage ?: return null

        val constantPath = ModulePath(module.name, name)
        val item = storageItemsCache[constantPath]
            ?: module.storage.items.firstOrNull { namingPolicy.equals(it.name, name) }
            ?: return null

        storageItemsCache[constantPath] = item

        return FindStorageItemResult(item, storage)
    }

    fun findStorageItem(moduleName: String, itemName: String) = findModule(moduleName)?.let {
        findStorageItem(it, itemName)
    }

    fun findRuntimeType(index: BigInteger) = runtimeMetadata.lookup.findItemByIndex(index)
}