package dev.sublab.substrate.metadata

import dev.sublab.common.numerics.*
import dev.sublab.substrate.metadata.lookup.RuntimeLookup
import dev.sublab.substrate.metadata.modules.RuntimeModule

/**
 * Runtime Metadata
 */
data class RuntimeMetadata(
    val magicNumber: UInt32,
    private val versionUInt8: UInt8,
    val lookup: RuntimeLookup,
    val modules: List<RuntimeModule>,
    val extrinsic: RuntimeExtrinsic
) {

    val version: UInt32 get() = versionUInt8.toUInt()
}