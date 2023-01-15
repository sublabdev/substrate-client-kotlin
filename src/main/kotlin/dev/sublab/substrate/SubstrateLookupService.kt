/**
 *
 * Copyright 2023 SUBSTRATE LABORATORY LLC <info@sublab.dev>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package dev.sublab.substrate

import dev.sublab.substrate.metadata.RuntimeMetadata
import dev.sublab.substrate.metadata.lookup.RuntimeType
import dev.sublab.substrate.metadata.modules.RuntimeModule
import dev.sublab.substrate.metadata.modules.RuntimeModuleConstant
import dev.sublab.substrate.metadata.modules.storage.RuntimeModuleStorage
import dev.sublab.substrate.metadata.modules.storage.item.RuntimeModuleStorageItem
import kotlinx.coroutines.flow.*
import java.math.BigInteger

/**
 * An object holding information about a module
 */
private data class ModulePath(
    val moduleName: String,
    val childName: String
)

interface SubstrateLookup {
    fun findModule(name: String): Flow<RuntimeModule?>
    fun findConstant(moduleName: String, constantName: String): Flow<RuntimeModuleConstant?>
    fun findStorageItem(moduleName: String, itemName: String): Flow<FindStorageItemResult?>
    fun findRuntimeType(index: BigInteger): Flow<RuntimeType?>
}

/**
 * Substrate lookup service
 */
internal class SubstrateLookupService(
    private val runtimeMetadata: Flow<RuntimeMetadata>,
    private val namingPolicy: SubstrateClientNamingPolicy
): SubstrateLookup {
    private fun lookupAsFlow() = runtimeMetadata.map {
        SubstrateLookupServiceImplementation(it, namingPolicy)
    }

    /**
     * Finds a runtime module for a provided name using the existing runtime metadata
     */
    override fun findModule(name: String) = lookupAsFlow().map {
        it.findModule(name)
    }

    /**
     * Finds constant with the provided name either in a runtime module after finding the module first
     */
    override fun findConstant(moduleName: String, constantName: String) = lookupAsFlow().map {
        it.findConstant(moduleName, constantName)
    }

    /**
     * Finds a storage item previously fetching the module
     */
    override fun findStorageItem(moduleName: String, itemName: String) = lookupAsFlow().map {
        it.findStorageItem(moduleName, itemName)
    }

    /**
     * Finds a runtime lookup item for a provided index
     */
    override fun findRuntimeType(index: BigInteger) = lookupAsFlow().map {
        it.findRuntimeType(index)
    }
}

/**
 * v
 */
data class FindStorageItemResult(val item: RuntimeModuleStorageItem, val storage: RuntimeModuleStorage)

private class SubstrateLookupServiceImplementation(
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