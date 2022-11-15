package dev.sublab.substrate.hashers

import dev.sublab.substrate.metadata.modules.storage.RuntimeModuleStorage

interface HashersProvider {
    fun getStorageHasher(storage: RuntimeModuleStorage): StorageHashing
}