package dev.sublab.substrate.modules

import dev.sublab.substrate.ScaleCodecProvider
import dev.sublab.substrate.SubstrateClient
import dev.sublab.substrate.SubstrateConstantsService
import dev.sublab.substrate.hashers.HashersProvider
import dev.sublab.substrate.modules.chain.ChainRpc
import dev.sublab.substrate.modules.chain.ChainRpcClient
import dev.sublab.substrate.modules.payment.PaymentRpc
import dev.sublab.substrate.modules.payment.PaymentRpcClient
import dev.sublab.substrate.modules.state.StateRpcClient
import dev.sublab.substrate.modules.system.SystemRpc
import dev.sublab.substrate.modules.system.SystemRpcClient
import dev.sublab.substrate.rpcClient.RpcClient

/**
 * Default module rpc provider
 */
class DefaultModuleRpcProvider(
    private val codecProvider: ScaleCodecProvider,
    private val rpcClient: RpcClient,
    private val hashersProvider: HashersProvider
): InternalModuleRpcProvider {
    lateinit var client: SubstrateClient

    override fun chainRpc() = ChainRpcClient(rpcClient)
    override fun stateRpc() = StateRpcClient(codecProvider.hex, rpcClient, hashersProvider)
    override fun systemRpc() = SystemRpcClient(client.constants, client.storage)
    override fun paymentRpc() = PaymentRpcClient(codecProvider.hex, rpcClient)

    // Supply dependencies
    /**
     * Sets substrate client to be used
     */
    override fun workingWithClient(client: SubstrateClient) {
        this.client = client
    }
}