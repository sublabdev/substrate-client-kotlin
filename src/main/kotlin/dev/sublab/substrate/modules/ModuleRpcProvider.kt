package dev.sublab.substrate.modules

import dev.sublab.substrate.SubstrateClient
import dev.sublab.substrate.modules.state.StateRpc
import dev.sublab.substrate.modules.system.SystemRpc

interface InternalModuleRpcProvider: ModuleRpcProvider {
    fun workingWithClient(client: SubstrateClient)
}

interface ModuleRpcProvider {
    fun stateRpc(): StateRpc
    fun systemRpc(): SystemRpc
}