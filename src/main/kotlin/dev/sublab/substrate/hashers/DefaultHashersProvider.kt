package dev.sublab.substrate.hashers

import dev.sublab.substrate.metadata.modules.storage.RuntimeModuleStorage

/**
 * Default hashers provider
 */
class DefaultHashersProvider: HashersProvider {
    override fun getStorageHasher(storage: RuntimeModuleStorage) = StorageHasher(storage)
}