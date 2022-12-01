package dev.sublab.substrate.metadata.modules.storage.item

import dev.sublab.common.numerics.Int8
import dev.sublab.substrate.metadata.modules.storage.item.type.RuntimeModuleStorageItemType

data class RuntimeModuleStorageItem(
    val name: String,
    val modifier: RuntimeModuleStorageItemModifier,
    val type: RuntimeModuleStorageItemType,
    private val fallbackListOfInt8: List<Int8>,
    val docs: List<String>
) {

    val fallback get() = fallbackListOfInt8.toByteArray()
}