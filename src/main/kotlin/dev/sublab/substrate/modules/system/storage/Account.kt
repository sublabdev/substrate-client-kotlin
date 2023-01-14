package dev.sublab.substrate.modules.system.storage

import dev.sublab.substrate.scale.Balance
import dev.sublab.substrate.scale.Index

/**
 * Account information
 */
data class Account(
    val nonce: Index,
    val consumers: Index,
    val providers: Index,
    val sufficients: Index,
    val data: Data
) {
    data class Data(
        val free: Balance,
        val reserved: Balance,
        val miscFrozen: Balance,
        val feeFrozen: Balance
    )
}