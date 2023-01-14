package dev.sublab.substrate.modules.chain

import dev.sublab.common.numerics.toByteArray
import dev.sublab.hex.hex
import dev.sublab.substrate.rpcClient.RpcClient

/**
 * An interface for chain RPC client
 */
interface ChainRpc {
    /**
     * Gets block hash using the provided number as a parameter for `RPC` request
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