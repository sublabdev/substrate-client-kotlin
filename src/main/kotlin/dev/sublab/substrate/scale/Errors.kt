package dev.sublab.substrate.scale

import kotlin.reflect.KType

class TypeIsNotDynamicException(type: KType): Throwable()
class TypeIsNotFoundInRuntimeMetadataException(type: KType): Throwable()
class UnsupportedDynamicTypeException(type: KType): Throwable()
class NoByteArrayConstructorException(type: KType): Throwable()