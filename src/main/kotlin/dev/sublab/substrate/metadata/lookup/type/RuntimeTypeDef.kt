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