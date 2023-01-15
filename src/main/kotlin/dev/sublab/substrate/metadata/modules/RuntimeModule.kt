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

package dev.sublab.substrate.metadata.modules

import dev.sublab.common.numerics.*
import dev.sublab.substrate.metadata.modules.storage.RuntimeModuleStorage
import java.math.BigInteger

/**
 * A runtime module object
 */
data class RuntimeModule(
    val name: String,
    val storage: RuntimeModuleStorage?,
    val callIndex: BigInteger?,
    val eventsIndex: BigInteger?,
    val constants: List<RuntimeModuleConstant>,
    val errorsIndex: BigInteger?,
    val indexUInt8: UInt8
) {

    val index: UInt32 get() = indexUInt8.toUInt()
}