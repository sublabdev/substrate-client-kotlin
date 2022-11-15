package dev.sublab.substrate.modules.system.storage

import dev.sublab.scale.dataTypes.UInt128

data class Account(
    val nonce: UInt,
    val consumers: UInt,
    val providers: UInt,
    val sufficients: UInt,
    val data: Data
) {
    data class Data(
        val free: UInt128,
        val reserved: UInt128,
        val miscFrozen: UInt128,
        val feeFrozen: UInt128
    )
}