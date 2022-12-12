package dev.sublab.substrate.modules.system.storage

import dev.sublab.common.numerics.UInt128
import dev.sublab.common.numerics.UInt32

data class Account(
    val nonce: UInt32,
    val consumers: UInt32,
    val providers: UInt32,
    val sufficients: UInt32,
    val data: Data
) {
    data class Data(
        val free: UInt128,
        val reserved: UInt128,
        val miscFrozen: UInt128,
        val feeFrozen: UInt128
    )
}