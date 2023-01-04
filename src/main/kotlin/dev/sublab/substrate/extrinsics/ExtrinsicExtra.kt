package dev.sublab.substrate.extrinsics

import dev.sublab.substrate.scale.Balance
import java.math.BigInteger

data class ExtrinsicExtra(
    val era: Era = Era.Immortal(),
    val nonce: BigInteger,
    val tip: Balance
)