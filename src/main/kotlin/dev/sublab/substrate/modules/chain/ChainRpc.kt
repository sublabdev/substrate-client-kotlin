package dev.sublab.substrate.modules.chain

import dev.sublab.common.numerics.toByteArray
import dev.sublab.hex.hex
import dev.sublab.substrate.rpcClient.RpcClient

interface ChainRpc {
    suspend fun getBlockHash(number: Int): String?
}

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