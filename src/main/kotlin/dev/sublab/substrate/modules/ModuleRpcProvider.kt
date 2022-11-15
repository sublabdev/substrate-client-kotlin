package dev.sublab.substrate.modules

import dev.sublab.substrate.modules.state.StateRpc

interface ModuleRpcProvider {
    fun stateRpc(): StateRpc
}