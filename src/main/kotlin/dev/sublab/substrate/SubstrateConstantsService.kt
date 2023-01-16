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

package dev.sublab.substrate

import dev.sublab.scale.ScaleCodec
import dev.sublab.substrate.metadata.modules.RuntimeModuleConstant
import kotlinx.coroutines.flow.*
import kotlin.reflect.KClass

interface SubstrateConstants {
    fun find(moduleName: String, constantName: String): Flow<RuntimeModuleConstant?>
    fun <T: Any> fetch(moduleName: String, constantName: String, type: KClass<T>): Flow<T?>
    fun <T: Any> fetch(constant: RuntimeModuleConstant, type: KClass<T>): T?
}

/**
 * Substrate constants service. Handles fetching runtime module constant
 */
internal class SubstrateConstantsService(
    private val codec: ScaleCodec<ByteArray>,
    private val lookup: SubstrateLookup
): SubstrateConstants {

    /**
     * Finds a runtime module constant by the constant's name in a specified module
     * @param moduleName module's name in which the constant should be looked for
     * @param constantName constant name by which the constant should be found
     * @return a cruntime module constant
     */
    override fun find(moduleName: String, constantName: String) = lookup.findConstant(moduleName, constantName)
    override fun <T: Any> fetch(moduleName: String, constantName: String, type: KClass<T>) = find(moduleName, constantName)
        .map {
            it?.let { fetch(it, type) }
        }

    /**
     * Decodes the value bytes of a runtime module constant into a specified type
     */
    override fun <T: Any> fetch(constant: RuntimeModuleConstant, type: KClass<T>)
        = codec.fromScale(constant.value, type)
}