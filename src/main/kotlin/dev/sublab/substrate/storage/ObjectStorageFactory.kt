package dev.sublab.substrate.storage

import kotlinx.coroutines.flow.MutableStateFlow

/**
 * An interface of getting an object storage factory
 */
interface ObjectStorageFactory {
    /**
     * Makes a mutable state flow of a generic type T
     */
    fun <T> make(): MutableStateFlow<T?>
}