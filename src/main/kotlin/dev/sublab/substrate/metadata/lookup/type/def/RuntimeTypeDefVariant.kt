package dev.sublab.substrate.metadata.lookup.type.def

import dev.sublab.common.numerics.UInt8

/**
 * Variant runtime type
 */
data class RuntimeTypeDefVariant(
    val variants: List<Variant>
) {

    data class Variant(
        val name: String,
        val fields: List<RuntimeTypeDefField>,
        internal val indexUInt8: UInt8,
        val docs: List<String>
    ) {

        val index get() = indexUInt8.toUInt()
    }
}