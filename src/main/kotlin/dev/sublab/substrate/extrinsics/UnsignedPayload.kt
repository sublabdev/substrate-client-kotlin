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

package dev.sublab.substrate.extrinsics

import dev.sublab.common.numerics.UInt8
import dev.sublab.scale.ScaleCodec
import dev.sublab.scale.ScaleCodecTransaction
import dev.sublab.substrate.metadata.lookup.type.def.RuntimeTypeDefVariant
import dev.sublab.substrate.metadata.modules.RuntimeModule
import kotlin.reflect.KClass

/**
 * An unsigned payload. Subclass of Payload
 */
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
): ScaleCodecTransaction<Data> = /*apply { println("[payload] add module index") }
    .*/append(unsignedPayload.module.indexUInt8, UInt8::class)
//    .apply { println("[payload] add call index") }
    .append(unsignedPayload.callVariant.indexUInt8, UInt8::class)
//    .apply { println("[payload] add call value") }
    .append(unsignedPayload.callValue, unsignedPayload.callValueClass)