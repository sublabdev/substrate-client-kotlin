/**
 *
 * Copyright 2023 SUBSTRATE LABORATORY LLC <info@sublab.dev>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package dev.sublab.substrate.metadata.lookup

import java.math.BigInteger

/**
 * Runtime lookup. Holds an array of lookup items
 * @property items a list of [RuntimeLookupItem]
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