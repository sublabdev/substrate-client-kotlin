package dev.sublab.substrate.metadata.modules.storage.item.type

import dev.sublab.substrate.metadata.modules.storage.RuntimeModuleStorageHasher
import java.math.BigInteger

data class RuntimeModuleStorageItemTypeMap(
    val hashers: List<RuntimeModuleStorageHasher>,
    val key: BigInteger,
    val type: BigInteger
)