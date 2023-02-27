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

import dev.sublab.substrate.ScaleCodecProvider
import dev.sublab.substrate.SubstrateClient
import dev.sublab.substrate.hashers.HashersProvider
import dev.sublab.substrate.modules.chain.ChainModule
import dev.sublab.substrate.modules.chain.ChainModuleClient
import dev.sublab.substrate.modules.payment.PaymentModule
import dev.sublab.substrate.modules.payment.PaymentModuleClient
import dev.sublab.substrate.modules.state.StateModule
import dev.sublab.substrate.modules.state.StateModuleClient
import dev.sublab.substrate.modules.system.SystemModule
import dev.sublab.substrate.modules.system.SystemModuleClient
import dev.sublab.substrate.rpcClient.Rpc

/**
 * Default module rpc provider
 */
class DefaultModuleProvider(
    private val codecProvider: ScaleCodecProvider,
    private val rpc: Rpc,
    private val hashersProvider: HashersProvider
): InternalModuleProvider {
    lateinit var client: SubstrateClient

    override val chain: ChainModule get() = ChainModuleClient(rpc)
    override val state: StateModule get() = StateModuleClient(codecProvider.hex, rpc, hashersProvider)
    override val system: SystemModule get() = SystemModuleClient(client.constants, client.storage)
    override val payment: PaymentModule get() = PaymentModuleClient(rpc)

    // Supply dependencies
    /**
     * Sets substrate client to be used
     */
    override fun workingWithClient(client: SubstrateClient) {
        this.client = client
    }
}