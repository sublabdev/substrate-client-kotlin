package dev.sublab.substrate

import dev.sublab.common.asByteArrayConvertible
import dev.sublab.ecdsa.Kind
import dev.sublab.ecdsa.ecdsa
import dev.sublab.ed25519.ed25519
import dev.sublab.encrypting.keys.KeyPair
import dev.sublab.sr25519.sr25519
import dev.sublab.ss58.ss58
import dev.sublab.substrate.modules.system.storage.Account
import dev.sublab.substrate.support.KusamaNetwork
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertNull

internal class TestRandomAccount {
    private val network = KusamaNetwork()
    private val client = SubstrateClient(url = network.rpcUrl)

    private val factories = listOf(
        KeyPair.Factory.ecdsa(Kind.SUBSTRATE),
        KeyPair.Factory.ed25519,
        KeyPair.Factory.sr25519()
    )

    @Test
    fun `no records about account in blockchain`() = runBlocking {
        for (factory in factories) {
            val keyPair = factory.generate()
            val accountId = keyPair.publicKey.ss58.accountId().asByteArrayConvertible()

            val account = client.storageService
                .fetch("system", "account", accountId, Account::class)
                .first()

            assertNull(account)
        }
    }
}