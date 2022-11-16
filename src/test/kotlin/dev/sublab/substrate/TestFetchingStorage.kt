package dev.sublab.substrate

import dev.sublab.hashing.decodeHex
import dev.sublab.hashing.utils.ByteArrayConvertible
import dev.sublab.hashing.utils.asByteArrayConvertible
import dev.sublab.substrate.modules.system.storage.Account
import dev.sublab.substrate.support.Constants
import dev.sublab.substrate.support.KusamaNetwork
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import java.math.BigInteger
import java.time.Duration
import java.time.Instant
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

private data class RpcStorageItem<T: Any>(
    val module: String,
    val item: String,
    val type: KClass<T>,
    val keys: List<ByteArrayConvertible> = listOf(),
    val validation: ((T) -> Boolean)? = null
)

class TestFetchingStorage {
    private val network = KusamaNetwork()
    private val client = SubstrateClient(url = network.rpcUrl)

    private val items: List<RpcStorageItem<*>> = listOf(
        RpcStorageItem("timestamp", "now", ULong::class) {
            // Difference should be within one minute, let's assume some big lag
            Duration.between(Instant.now(), Instant.ofEpochMilli(it.toLong())).seconds < Constants.testsTimeout
        },
        RpcStorageItem("system", "account", Account::class, keys = listOf(
            "0xd857fcac7bd9bb03551d70b9743895a98b74b06e54bdc34f1b27ab240356857d".decodeHex().asByteArrayConvertible()
        )) { account ->
            // Random Kusama validator account, as long as it participates in validation, all field should be > 0
            account.data.feeFrozen.value > BigInteger.ZERO &&
            account.data.reserved.value > BigInteger.ZERO &&
            account.data.miscFrozen.value > BigInteger.ZERO &&
            account.data.feeFrozen.value > BigInteger.ZERO
        }
    )

    @Test
    internal fun testService() {
        val service = SubstrateStorageService(client.codec, client.lookupService, client.modules.stateRpc())
        for (item in items) {
            testStorageItem(service, item)
        }
    }

    @Test
    internal fun testClient() {
        for (item in items) {
            testStorageItem(client.storageService, item)
        }
    }

    private fun <T: Any> testStorageItem(service: SubstrateStorageService, item: RpcStorageItem<T>) = runBlocking {
        service.fetch(item.module, item.item, item.keys, item.type).take(1).collect {
            assertNotNull(it)
            item.validation?.let { isValid ->
                assertTrue(isValid(it))
            }
        }

        service.find(item.module, item.item).take(1).collect {
            assertNotNull(it)
            val value = service.fetch(it.item, item.keys, it.storage, item.type)
            item.validation?.let { isValid ->
                assertTrue(isValid(value))
            }
        }
    }
}