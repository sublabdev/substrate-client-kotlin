package dev.sublab.substrate.scale

import kotlin.reflect.KType

internal class DynamicAdapterGivenInvalidType(type: KType): Throwable()
internal class TypeIsNotDynamicException(type: KType): Throwable()
internal class TypeIsNotFoundInRuntimeMetadataException(type: KType): Throwable()
internal class UnsupportedDynamicTypeException(type: KType): Throwable()
internal class NoByteArrayConstructorException(type: KType): Throwable()