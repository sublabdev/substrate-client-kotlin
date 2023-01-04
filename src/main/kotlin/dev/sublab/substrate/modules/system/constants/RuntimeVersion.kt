package dev.sublab.substrate.modules.system.constants

import dev.sublab.common.numerics.UInt8
import dev.sublab.scale.annotations.FixedArray
import dev.sublab.substrate.scale.Index

data class RuntimeVersion(
    val specName: String,
    val implName: String,
    val authoringVersion: Index,
    val specVersion: Index,
    val implVersion: Index,
    val apis: List<RuntimeVersionApi>,
    val txVersion: Index,
    val stateVersion: UInt8
)

//@FixedArray(size = 8) class RuntimeVersionApiId<Byte>: ArrayList<Byte>()

data class RuntimeVersionApi(
    @FixedArray(size = 8) val id: List<Byte>,
    val index: Index
)