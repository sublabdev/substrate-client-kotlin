package dev.sublab.substrate.storage

import kotlinx.coroutines.flow.MutableStateFlow

class InMemoryObjectStorageFactory: ObjectStorageFactory {
    override fun <T> make() = MutableStateFlow<T?>(null)
}