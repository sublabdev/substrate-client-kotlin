package dev.sublab.substrate.modules

import dev.sublab.substrate.SubstrateClient
import dev.sublab.substrate.modules.chain.ChainRpc
import dev.sublab.substrate.modules.payment.PaymentRpc
import dev.sublab.substrate.modules.state.StateRpc
import dev.sublab.substrate.modules.system.SystemRpc

interface InternalModuleRpcProvider: ModuleRpcProvider {
    fun workingWithClient(client: SubstrateClient)
}

/**
 * An interface for getting RPCs
 */
interface ModuleRpcProvider {
    /**
     * Provides an interface for getting chain `RPC` client
     */
    fun chainRpc(): ChainRpc

    /**
    * Provides an interface for getting payment `RPC` client
    */
    fun paymentRpc(): PaymentRpc

    /**
     * Provides an interface for getting `RuntimeMetadata` and fetching `StorageItems`
     */
    fun stateRpc(): StateRpc

    /**
     * Provides an interface for getting `RuntimeVersion`
     */
    fun systemRpc(): SystemRpc
}