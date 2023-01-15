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

package dev.sublab.substrate.scale

import dev.sublab.common.FromByteArray
import dev.sublab.scale.ByteArrayReader
import dev.sublab.scale.ScaleCodecAdapter
import dev.sublab.scale.adapters.InvalidTypeException
import kotlinx.coroutines.runBlocking
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.primaryConstructor

/**
 * An adapter that decodes data dynamically. A subclass of `ScaleCodecAdapter`
 */
internal class DynamicAdapter<T>(
    private val provider: DynamicAdapterProvider
): ScaleCodecAdapter<T>() {
    /**
     * Decodes ByteArray dynamically to a specified generic type `T`
     */
    override fun read(reader: ByteArrayReader, type: KType, annotations: List<Annotation>): T = runBlocking {
        val adapter = provider.findAdapter<T>(type)
        return@runBlocking adapter.toByteArray
            // for conversions like Index <> UInt32(64, whatever)
            ?.let { toByteArray ->
                val dynamicByteArray = toByteArray(adapter.scaleAdapter.read(reader, type))
                val kClass = type.classifier as? KClass<*> ?: throw InvalidTypeException(type)
                val constructor = kClass.primaryConstructor ?: throw NoByteArrayConstructorException(type)

//                // <debug>
//                val offset = reader.offset
//                reader.offset = 0
////                println("data: ${reader.readToEnd().hex.encode(true)}")
//                println("DynamicAdapter read: ${dynamicByteArray.hex.encode(true)}, trying constructor $constructor, result: ${(constructor.call(dynamicByteArray) as? Index)?.value}")
//                reader.offset = offset
//                // </debug>

                return@let constructor.call(dynamicByteArray) as T
            }
        // this might throw cast exception, from fun should be present for custom conversions
            ?: adapter.scaleAdapter.read(reader, type)
    }

    /**
     * Encodes provided value to `Data`
     */
    override fun write(obj: T, type: KType, annotations: List<Annotation>) = runBlocking {
        if (obj !is FromByteArray) throw DynamicAdapterGivenInvalidType(type)

        val adapter = provider.findAdapter<T>(type)
        adapter.fromByteArray
            // for conversions like Index <> UInt32(64, whatever)
            ?.let { fromByteArray ->
                adapter.scaleAdapter.write(fromByteArray(obj.toByteArray()), type)
            }
            // this might throw cast exception, from fun should be present for custom conversions
            ?: adapter.scaleAdapter.write(obj, type)
    }
}