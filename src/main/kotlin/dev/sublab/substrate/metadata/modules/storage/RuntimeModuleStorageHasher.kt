package dev.sublab.substrate.metadata.modules.storage

import dev.sublab.scale.annotations.EnumClass

@Suppress("unused")
@EnumClass
enum class RuntimeModuleStorageHasher {
    Blake128,
    Blake256,
    Blake128Concat,
    Twox128,
    Twox256,
    Twox64Concat,
    Identity
}