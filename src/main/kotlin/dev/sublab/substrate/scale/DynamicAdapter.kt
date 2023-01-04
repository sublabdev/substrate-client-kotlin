package dev.sublab.substrate.scale

import dev.sublab.common.FromByteArray
import dev.sublab.hex.hex
import dev.sublab.scale.ByteArrayReader
import dev.sublab.scale.ScaleCodecAdapter
import dev.sublab.scale.adapters.InvalidTypeException
import kotlinx.coroutines.runBlocking
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.typeOf

internal class DynamicAdapterGivenInvalidType(type: KType): Throwable()

internal class DynamicAdapter<T>(
    private val provider: DynamicAdapterProvider
): ScaleCodecAdapter<T>() {
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