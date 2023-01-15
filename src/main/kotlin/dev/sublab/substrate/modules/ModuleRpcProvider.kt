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
import dev.sublab.substrate.modules.chain.ChainRpc
import dev.sublab.substrate.modules.payment.PaymentRpc
import dev.sublab.substrate.modules.state.StateRpc
import dev.sublab.substrate.modules.system.SystemRpc

interface InternalModuleRpcProvider: ModuleRpcProvider {
    fun workingWithClient(client: SubstrateClient)
}

/**
 * An interface for getting RPCs
 */
interface ModuleRpcProvider {
    /**
     * Provides an interface for getting chain `RPC` client
     */
    fun chainRpc(): ChainRpc

    /**
    * Provides an interface for getting payment `RPC` client
    */
    fun paymentRpc(): PaymentRpc

    /**
     * Provides an interface for getting `RuntimeMetadata` and fetching `StorageItems`
     */
    fun stateRpc(): StateRpc

    /**
     * Provides an interface for getting `RuntimeVersion`
     */
    fun systemRpc(): SystemRpc
}