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
import dev.sublab.substrate.modules.chain.ChainModuleClient
import dev.sublab.substrate.modules.payment.PaymentModuleClient
import dev.sublab.substrate.modules.state.StateModuleClient
import dev.sublab.substrate.modules.system.SystemModuleClient
import dev.sublab.substrate.rpcClient.RpcClient

/**
 * Default module rpc provider
 */
class DefaultModuleProvider(
    private val codecProvider: ScaleCodecProvider,
    private val rpcClient: RpcClient,
    private val hashersProvider: HashersProvider
): InternalModuleProvider {
    lateinit var client: SubstrateClient

    override fun chain() = ChainModuleClient(rpcClient)
    override fun state() = StateModuleClient(codecProvider.hex, rpcClient, hashersProvider)
    override fun system() = SystemModuleClient(client.constants, client.storage)
    override fun payment() = PaymentModuleClient(codecProvider.hex, rpcClient)

    // Supply dependencies
    /**
     * Sets substrate client to be used
     */
    override fun workingWithClient(client: SubstrateClient) {
        this.client = client
    }
}