package dev.sublab.substrate.rpcClient.modules

import dev.sublab.scale.ScaleCodec
import dev.sublab.scale.helpers.decodeHex
import dev.sublab.substrate.metadata.RuntimeMetadata
import dev.sublab.substrate.rpcClient.RpcClient

interface StateRpc {
    suspend fun getRuntimeMetadata(): RuntimeMetadata
}

class StateRpcClient(
    private val codec: ScaleCodec<ByteArray>,
    private val rpcClient: RpcClient
): StateRpc {
    override suspend fun getRuntimeMetadata() = rpcClient.sendRequest<Unit, String> {
        method = "state_getMetadata"
        responseType = String::class
    }.let {
        codec.fromScale(it.decodeHex(), RuntimeMetadata::class)
    }
}