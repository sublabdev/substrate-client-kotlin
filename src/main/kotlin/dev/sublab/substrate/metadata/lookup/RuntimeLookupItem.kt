package dev.sublab.substrate.metadata.lookup

import java.math.BigInteger

data class RuntimeLookupItem(
    val id: BigInteger,
    val type: RuntimeType
)