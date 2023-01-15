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

package dev.sublab.substrate.modules.system.constants

import dev.sublab.common.numerics.UInt8
import dev.sublab.scale.annotations.FixedArray
import dev.sublab.substrate.scale.Index

/**
 *  Runtime version
 */
data class RuntimeVersion(
    val specName: String,
    val implName: String,
    val authoringVersion: Index,
    val specVersion: Index,
    val implVersion: Index,
    val apis: List<RuntimeVersionApi>,
    val txVersion: Index,
    val stateVersion: UInt8
)

//@FixedArray(size = 8) class RuntimeVersionApiId<Byte>: ArrayList<Byte>()

data class RuntimeVersionApi(
    @FixedArray(size = 8) val id: List<Byte>,
    val index: Index
)