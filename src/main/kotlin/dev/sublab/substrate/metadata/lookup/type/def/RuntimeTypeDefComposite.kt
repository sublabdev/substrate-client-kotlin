package dev.sublab.substrate.metadata.lookup.type.def

/**
 * Composite runtime type
 */
data class RuntimeTypeDefComposite(
    val fields: List<RuntimeTypeDefField>
)