package dev.sublab.substrate.modules

import dev.sublab.substrate.ScaleCodecProvider
import dev.sublab.substrate.hashers.HashersProvider
import dev.sublab.substrate.modules.state.StateRpcClient
import dev.sublab.substrate.rpcClient.RpcClient

class DefaultModuleRpcProvider(
    private val codecProvider: ScaleCodecProvider,
    private val rpcClient: RpcClient,
    private val hashersProvider: HashersProvider
): ModuleRpcProvider {
    override fun stateRpc() = StateRpcClient(codecProvider.hex, rpcClient, hashersProvider)
}