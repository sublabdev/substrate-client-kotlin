package dev.sublab.substrate.rpcClient

import dev.sublab.substrate.rpcClient.modules.StateRpc

interface ModuleRpcProvider {
    fun stateRpc(): StateRpc
}