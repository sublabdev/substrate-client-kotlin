package dev.sublab.substrate.extrinsics

import dev.sublab.common.numerics.UInt64
import dev.sublab.scale.annotations.EnumCase
import dev.sublab.scale.annotations.EnumClass

/**
 * An extrinsic era
 */
@EnumClass
sealed class Era {
    @EnumCase(index = 0)
    class Immortal : Era() {
        override fun equals(other: Any?) = other is Immortal
        override fun hashCode() = System.identityHashCode(this)
    }

    @EnumCase(index = 1)
    data class Mortal(val period: UInt64, val phase: UInt64) : Era()
}