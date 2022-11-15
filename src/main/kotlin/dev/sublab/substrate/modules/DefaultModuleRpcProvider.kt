package dev.sublab.substrate.modules

import dev.sublab.scale.ScaleCodec
import dev.sublab.substrate.hashers.HashersProvider
import dev.sublab.substrate.modules.state.StateRpcClient
import dev.sublab.substrate.rpcClient.RpcClient

class DefaultModuleRpcProvider(
    private val codec: ScaleCodec<ByteArray>,
    private val rpcClient: RpcClient,
    private val hashersProvider: HashersProvider
): ModuleRpcProvider {
    override fun stateRpc() = StateRpcClient(codec, rpcClient, hashersProvider)
}