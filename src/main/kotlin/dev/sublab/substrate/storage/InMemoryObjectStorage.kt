package dev.sublab.substrate.storage

import kotlinx.coroutines.flow.MutableStateFlow

/**
 * In memory object storage facotry
 */
class InMemoryObjectStorageFactory: ObjectStorageFactory {
    override fun <T> make() = MutableStateFlow<T?>(null)
}