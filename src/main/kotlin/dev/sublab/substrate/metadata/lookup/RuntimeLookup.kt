package dev.sublab.substrate.metadata.lookup

import java.math.BigInteger

/**
 * Runtime lookup. Holds an array of lookup items
 */
data class RuntimeLookup(
    private val items: List<RuntimeLookupItem>
) {

    private var itemsByIndices: Map<BigInteger, RuntimeType> = items.associate { Pair(it.id, it.type) }

    /**
     * Finds a lookup item by an index of type BigInteger
     * @param index Index (of type BigInteger) to find a lookup item
     * @return A lookup item for a specific index
     */
    fun findItemByIndex(index: BigInteger): RuntimeType? = itemsByIndices[index]

    /**
     * Finds a lookup item by an index of type Int
     * @param index Index (of type BigInteger) to find a lookup item
     * @return A lookup item for a specific index
     */
    fun findItemByIndex(index: Int): RuntimeType? = itemsByIndices[index.toBigInteger()]
}