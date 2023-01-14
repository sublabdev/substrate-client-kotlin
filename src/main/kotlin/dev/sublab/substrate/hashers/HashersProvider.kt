package dev.sublab.substrate.hashers

import dev.sublab.substrate.metadata.modules.storage.RuntimeModuleStorage

/**
 * An interface for providing a storage hasher
 */
interface HashersProvider {
    /**
     * Provides a storage hasher for a specified storage
     * @param storage the module storage which needs to be hashed
     * @return A storage hasher
     */
    fun getStorageHasher(storage: RuntimeModuleStorage): StorageHashing
}