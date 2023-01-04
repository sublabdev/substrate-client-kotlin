package dev.sublab.substrate.metadata.lookup

import java.math.BigInteger

data class RuntimeLookup(
    private val items: List<RuntimeLookupItem>
) {

    private var itemsByIndices: Map<BigInteger, RuntimeType> = items.associate { Pair(it.id, it.type) }
    fun findItemByIndex(index: BigInteger): RuntimeType? = itemsByIndices[index]
    fun findItemByIndex(index: Int): RuntimeType? = itemsByIndices[index.toBigInteger()]
}