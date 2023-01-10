package dev.sublab.substrate.extrinsics

import dev.sublab.common.numerics.UInt8
import dev.sublab.scale.ScaleCodec
import dev.sublab.scale.ScaleCodecTransaction
import dev.sublab.substrate.metadata.lookup.type.def.RuntimeTypeDefVariant
import dev.sublab.substrate.metadata.modules.RuntimeModule
import kotlin.reflect.KClass

internal class UnsignedPayload<T: Any>(
    private val codec: ScaleCodec<ByteArray>,
    internal val module: RuntimeModule,
    internal val callVariant: RuntimeTypeDefVariant.Variant,
    internal val callValue: T,
    internal val callValueClass: KClass<T>
): Payload {

    override val moduleName: String get() = module.name
    override val callName: String get() = callVariant.name

    override fun toByteArray() = codec.transaction()
        .append(this)
        .commit()
}

internal fun <T: Any, Data: Any> ScaleCodecTransaction<Data>.append(
    unsignedPayload: UnsignedPayload<T>
): ScaleCodecTransaction<Data> = apply { println("[payload] add module index") }
    .append(unsignedPayload.module.indexUInt8, UInt8::class)
    .apply { println("[payload] add call index") }
    .append(unsignedPayload.callVariant.indexUInt8, UInt8::class)
    .apply { println("[payload] add call value") }
    .append(unsignedPayload.callValue, unsignedPayload.callValueClass)