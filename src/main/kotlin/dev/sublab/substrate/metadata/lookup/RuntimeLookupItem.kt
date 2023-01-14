package dev.sublab.substrate.metadata.lookup

import java.math.BigInteger

/**
 * Runtime lookup item. Consists of an id and runtime type.
 */
data class RuntimeLookupItem(
    val id: BigInteger,
    val type: RuntimeType
)