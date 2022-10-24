package dev.sublab.substrate.metadata.modules.storage.item

import dev.sublab.scale.helpers.decodeHex
import dev.sublab.substrate.metadata.modules.storage.item.type.RuntimeModuleStorageItemType

data class RuntimeModuleStorageItem(
    val name: String,
    val modifier: RuntimeModuleStorageItemModifier,
    val type: RuntimeModuleStorageItemType,
    private val fallbackHex: String,
    val docs: List<String>
) {

    val fallback get() = fallbackHex.decodeHex()
}