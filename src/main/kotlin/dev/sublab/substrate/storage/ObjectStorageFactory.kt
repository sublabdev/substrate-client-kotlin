package dev.sublab.substrate.storage

import kotlinx.coroutines.flow.MutableStateFlow

interface ObjectStorageFactory {
    fun <T> make(): MutableStateFlow<T?>
}