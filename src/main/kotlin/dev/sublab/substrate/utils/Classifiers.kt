package dev.sublab.substrate.utils

import kotlinx.serialization.KSerializer
import kotlin.reflect.KClass
import kotlin.reflect.full.createType

class InvalidSerializerType: Throwable()

@Suppress("unchecked_cast")
fun <T: Any> serializerOrNull(type: KClass<T>?) = type?.let {
    kotlinx.serialization.serializer(type.createType()) as? KSerializer<T>
}

fun <T: Any> serializer(type: KClass<T>?) = serializerOrNull(type) ?: throw InvalidSerializerType()

