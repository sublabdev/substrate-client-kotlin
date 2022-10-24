package dev.sublab.substrate.metadata.lookup

import dev.sublab.substrate.metadata.lookup.type.RuntimeTypeDef
import dev.sublab.substrate.metadata.lookup.type.RuntimeTypeParam

data class RuntimeType(
    val path: List<String>,
    val params: List<RuntimeTypeParam>,
    val def: RuntimeTypeDef,
    val docs: List<String>
)

