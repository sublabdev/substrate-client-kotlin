package dev.sublab.substrate.metadata.modules.storage

import dev.sublab.substrate.metadata.modules.storage.item.RuntimeModuleStorageItem

data class RuntimeModuleStorage(
    val prefix: String,
    val items: List<RuntimeModuleStorageItem>
)