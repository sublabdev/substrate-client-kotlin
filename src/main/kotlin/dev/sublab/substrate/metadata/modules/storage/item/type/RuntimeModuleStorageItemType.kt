package dev.sublab.substrate.metadata.modules.storage.item.type

import dev.sublab.scale.annotations.EnumCase
import dev.sublab.scale.annotations.EnumClass

@EnumClass
sealed class RuntimeModuleStorageItemType {
    @EnumCase(0) data class Plain(val value: RuntimeModuleStorageItemTypePlain): RuntimeModuleStorageItemType()
    @EnumCase(1) data class Map(val value: RuntimeModuleStorageItemTypeMap): RuntimeModuleStorageItemType()
}