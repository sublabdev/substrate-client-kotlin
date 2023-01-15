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

package dev.sublab.substrate.modules.chain

import dev.sublab.common.numerics.toByteArray
import dev.sublab.hex.hex
import dev.sublab.substrate.rpcClient.RpcClient

/**
 * An interface for chain RPC client
 */
interface ChainRpc {
    /**
     * Gets block hash using the provided number as a parameter for [RPC] request
     * @param number number used as a parameter for an [RPC] request to get a block hash
     * @return Returns a block hash
     */
    suspend fun getBlockHash(number: Int): String?
}

/**
 * Handles chain block hash fetching
 */
class ChainRpcClient(
    private val rpcClient: RpcClient
    ): ChainRpc {
    override suspend fun getBlockHash(number: Int) = rpcClient.sendRequest<String, String> {
        method = "chain_getBlockHash"
        responseType = String::class
        params = listOf(number.toUInt().toByteArray().hex.encode())
        paramsType = String::class
    }
}