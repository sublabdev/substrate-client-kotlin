package dev.sublab.substrate.metadata.modules.storage.item

import dev.sublab.substrate.metadata.modules.storage.item.type.RuntimeModuleStorageItemType

data class RuntimeModuleStorageItem(
    val name: String,
    val modifier: RuntimeModuleStorageItemModifier,
    val type: RuntimeModuleStorageItemType,
    val fallbackHex: List<Byte>,
    val docs: List<String>
)