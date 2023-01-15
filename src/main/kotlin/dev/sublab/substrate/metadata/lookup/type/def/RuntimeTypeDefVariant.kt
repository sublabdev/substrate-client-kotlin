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