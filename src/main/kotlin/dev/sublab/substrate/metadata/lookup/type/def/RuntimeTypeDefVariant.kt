package dev.sublab.substrate.metadata.lookup.type.def

data class RuntimeTypeDefVariant(
    val variants: List<Variant>
) {

    data class Variant(
        val name: String,
        val fields: List<RuntimeTypeDefField>,
        val index: UByte,
        val docs: List<String>
    )
}