package dev.sublab.substrate.metadata.lookup.type.def

import java.math.BigInteger

/**
 * Tuple runtime type
 */
data class RuntimeTypeDefTuple(
    val types: List<BigInteger>
)