package dev.sublab.substrate.modules

import dev.sublab.common.asByteArrayConvertible
import dev.sublab.hex.hex
import dev.sublab.ss58.ss58
import dev.sublab.substrate.support.KusamaNetwork
import dev.sublab.substrate.support.allKeyPairFactories
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class TestSystemModule {
    private val network = KusamaNetwork()
    private val client = network.makeClient()
    private val module get() = client.modules.system

    @Test
    fun `runtime version`(): Unit = runBlocking {
        val runtimeVersion = module.getRuntimeVersion()
        assertNotNull(runtimeVersion)
    }

    @Test
    fun `no account for key pair`() = runBlocking {
        for (factory in allKeyPairFactories()) {
            val keyPair = factory.generate()
            val account = module.getAccountByKeyPair(keyPair)
            assertNull(account)
        }
    }

    @Test
    fun `no account for public key`() = runBlocking {
        for (factory in allKeyPairFactories()) {
            val keyPair = factory.generate()
            val account = module.getAccountByPublicKey(keyPair.publicKey)
            assertNull(account)
        }
    }

    @Test
    fun `no account for public key hex`() = runBlocking {
        for (factory in allKeyPairFactories()) {
            val keyPair = factory.generate()
            val account = module.getAccountByPublicKey(keyPair.publicKey.hex.encode(true))
            assertNull(account)
        }
    }

    private val accountIds get() = allKeyPairFactories().map { it.generate().publicKey.ss58.accountId() }

    @Test
    fun `no account for account id`() = runBlocking {
        for (accountId in accountIds) {
            val account = module.getAccountByAccountId(accountId)
            assertNull(account)
        }
    }

    @Test
    fun `no account for account id hex`() = runBlocking {
        for (accountId in accountIds) {
            val account = module.getAccountByAccountId(accountId.hex.encode(true))
            assertNull(account)
        }
    }

    @Test
    fun `no account for account id as byte array convertible`() = runBlocking {
        for (accountId in accountIds) {
            val account = module.getAccountByAccountId(accountId.asByteArrayConvertible())
            assertNull(account)
        }
    }
}