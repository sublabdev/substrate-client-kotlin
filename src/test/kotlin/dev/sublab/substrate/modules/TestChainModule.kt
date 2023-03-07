package dev.sublab.substrate.modules

import dev.sublab.substrate.support.Network
import dev.sublab.substrate.support.allNetworks
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class TestChainModule {
    @Test
    fun `test genesis hashes for all networks`() {
        for (network in allNetworks()) {
            checkGenesisHash(network)
        }
    }

    private fun checkGenesisHash(network: Network) = runBlocking {
        val client = network.makeClient()
        val genesisHash = client.modules.chain.getBlockHash(0)
        assertNotNull(genesisHash)
        assertEquals(network.genesisHash, genesisHash)
    }
}