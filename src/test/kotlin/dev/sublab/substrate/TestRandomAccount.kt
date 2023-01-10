package dev.sublab.substrate

import dev.sublab.common.ByteArrayConvertible
import dev.sublab.common.asByteArrayConvertible
import dev.sublab.ecdsa.Kind
import dev.sublab.ecdsa.ecdsa
import dev.sublab.ed25519.ed25519
import dev.sublab.encrypting.keys.KeyPair
import dev.sublab.sr25519.sr25519
import dev.sublab.ss58.ss58
import dev.sublab.substrate.modules.system.storage.Account
import dev.sublab.substrate.scale.Balance
import dev.sublab.substrate.support.KusamaNetwork
import dev.sublab.substrate.support.extrinsics.AddMemo
import dev.sublab.substrate.support.extrinsics.AddMemoCall
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class TestRandomAccount {
    private val network = KusamaNetwork()
    private val client = network.makeClient()

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

            val account = client.storage
                .fetch("system", "account", accountId, Account::class)
                .first()

            assertNull(account)
            testFailingExtrinsic(keyPair, accountId)
        }
    }

    private suspend fun testFailingExtrinsic(keyPair: KeyPair, accountId: ByteArrayConvertible) {
        val addMemoInstruction = AddMemo(0u, "hi".toByteArray())
        val extrinsic = client.extrinsics.makeSigned(AddMemoCall(addMemoInstruction), Balance(0.toBigInteger()), keyPair)
        assertNotNull(extrinsic)
        val signedByteArray = extrinsic.toByteArray()
    }
}