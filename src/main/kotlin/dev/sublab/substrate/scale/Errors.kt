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

import kotlin.reflect.KType

internal class DynamicAdapterGivenInvalidType(type: KType): Throwable()
internal class TypeIsNotDynamicException(type: KType): Throwable()
internal class TypeIsNotFoundInRuntimeMetadataException(type: KType): Throwable()
internal class UnsupportedDynamicTypeException(type: KType): Throwable()
internal class NoByteArrayConstructorException(type: KType): Throwable()