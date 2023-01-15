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

package dev.sublab.substrate.metadata.lookup.type

import dev.sublab.scale.annotations.EnumCase
import dev.sublab.scale.annotations.EnumClass
import dev.sublab.substrate.metadata.lookup.type.def.*

/**
 * Runtime type definition
 */
@Suppress("unused")
@EnumClass
sealed class RuntimeTypeDef {
    @EnumCase(0) data class Composite(val composite: RuntimeTypeDefComposite): RuntimeTypeDef()
    @EnumCase(1) data class Variant(val variant: RuntimeTypeDefVariant): RuntimeTypeDef()
    @EnumCase(2) data class Sequence(val sequence: RuntimeTypeDefSequence): RuntimeTypeDef()
    @EnumCase(3) data class Array(val array: RuntimeTypeDefArray): RuntimeTypeDef()
    @EnumCase(4) data class Tuple(val tuple: RuntimeTypeDefTuple): RuntimeTypeDef()
    @EnumCase(5) data class Primitive(val primitive: RuntimeTypeDefPrimitive): RuntimeTypeDef()
    @EnumCase(6) data class Compact(val compact: RuntimeTypeDefCompact): RuntimeTypeDef()
    @EnumCase(7) data class BitSequence(val bitSequence: RuntimeTypeDefBitSequence): RuntimeTypeDef()
}