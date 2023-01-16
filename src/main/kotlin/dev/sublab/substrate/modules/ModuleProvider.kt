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

package dev.sublab.substrate.modules

import dev.sublab.substrate.SubstrateClient
import dev.sublab.substrate.modules.chain.ChainModule
import dev.sublab.substrate.modules.payment.PaymentModule
import dev.sublab.substrate.modules.state.StateModule
import dev.sublab.substrate.modules.system.SystemModule

interface InternalModuleProvider: ModuleProvider {
    fun workingWithClient(client: SubstrateClient)
}

/**
 * An interface for getting RPCs
 */
interface ModuleProvider {
    /**
     * Provides an interface for getting chain `RPC` client
     */
    val chain: ChainModule

    /**
    * Provides an interface for getting payment `RPC` client
    */
    val payment: PaymentModule

    /**
     * Provides an interface for getting `RuntimeMetadata` and fetching `StorageItems`
     */
    val state: StateModule

    /**
     * Provides an interface for getting `RuntimeVersion`
     */
    val system: SystemModule
}