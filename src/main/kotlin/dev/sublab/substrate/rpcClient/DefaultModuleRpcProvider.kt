package dev.sublab.substrate.rpcClient

import dev.sublab.scale.ScaleCodec
import dev.sublab.substrate.rpcClient.modules.StateRpcClient

class DefaultModuleRpcProvider(
    private val codec: ScaleCodec<ByteArray>,
    private val rpcClient: RpcClient
): ModuleRpcProvider {
    override fun stateRpc() = StateRpcClient(codec, rpcClient)
}