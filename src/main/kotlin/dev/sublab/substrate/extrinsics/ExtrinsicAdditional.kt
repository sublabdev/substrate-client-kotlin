package dev.sublab.substrate.extrinsics

import dev.sublab.common.numerics.UInt32

data class ExtrinsicAdditional(
    val specVersion: UInt32,
    val txVersion: UInt32,
    val genesisHash: ByteArray,
    val mortalityCheckpoint: ByteArray,
    val marker: Any
)