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

package dev.sublab.substrate.hashers

import dev.sublab.substrate.metadata.modules.storage.RuntimeModuleStorage

/**
 * An interface for providing a storage hasher
 */
interface HashersProvider {
    /**
     * Provides a storage hasher for a specified storage
     * @param storage the module storage which needs to be hashed
     * @return A storage hasher
     */
    fun getStorageHasher(storage: RuntimeModuleStorage): StorageHashing
}