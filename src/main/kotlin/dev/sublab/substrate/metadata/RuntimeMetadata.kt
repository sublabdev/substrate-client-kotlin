package dev.sublab.substrate.metadata

import dev.sublab.substrate.metadata.lookup.RuntimeLookup
import dev.sublab.substrate.metadata.modules.RuntimeModule

data class RuntimeMetadata(
    val magicNumber: UInt,
    private val versionUByte: UByte,
    val lookup: RuntimeLookup,
    val modules: List<RuntimeModule>,
    val extrinsic: RuntimeExtrinsic
) {

    val version get() = versionUByte.toUInt()
}