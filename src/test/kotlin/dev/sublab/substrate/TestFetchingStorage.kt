package dev.sublab.substrate

import dev.sublab.common.ByteArrayConvertible
import dev.sublab.common.asByteArrayConvertible
import dev.sublab.common.numerics.UInt64
import dev.sublab.hex.hex
import dev.sublab.scale.ScaleCodec
import dev.sublab.substrate.modules.system.storage.Account
import dev.sublab.substrate.support.Constants
import dev.sublab.substrate.support.KusamaNetwork
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.math.BigInteger
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

private data class RpcStorageItem<T: Any>(
    val module: String,
    val item: String,
    val type: KClass<T>,
    val keys: List<ByteArrayConvertible> = listOf(),
    val validation: ((T?) -> Boolean)? = null
)

class TestFetchingStorage {
    private val network = KusamaNetwork()
    private val client = SubstrateClient(url = network.rpcUrl)

    private val items: List<RpcStorageItem<*>> = listOf(
        RpcStorageItem("timestamp", "now", UInt64::class) {
            // Difference should be within one minute, let's assume some big lag
            if (it == null) return@RpcStorageItem false

            (Clock.System.now() - Instant.fromEpochMilliseconds(it.toLong())).inWholeSeconds < Constants.testsTimeout.inWholeSeconds
        },
        RpcStorageItem("system", "account", Account::class, keys = listOf(
            "0xd857fcac7bd9bb03551d70b9743895a98b74b06e54bdc34f1b27ab240356857d".hex.decode().asByteArrayConvertible()
        )) { account ->
            // Random Kusama validator account, as long as it participates in validation, all field should be > 0
            if (account == null) return@RpcStorageItem false

            account.data.free.value > BigInteger.ZERO &&
            account.data.reserved.value > BigInteger.ZERO &&
            account.data.miscFrozen.value > BigInteger.ZERO &&
            account.data.feeFrozen.value > BigInteger.ZERO
        }
    )

    @Test
    internal fun testService() {
        val service = SubstrateStorageService(ScaleCodec.default(), client.lookup, client.modules.stateRpc())
        for (item in items) {
            testStorageItem(service, item)
        }
    }

    @Test
    internal fun testClient() {
        for (item in items) {
            testStorageItem(client.storage, item)
        }
    }

    private fun <T: Any> testStorageItem(service: SubstrateStorageService, item: RpcStorageItem<T>) = runBlocking {
        run {
            val storageItem = service.fetch(item.module, item.item, item.keys, item.type).first()
            item.validation?.let { isValid ->
                assertTrue(isValid(storageItem))
            }
        }

        run {
            val storageItem = service.find(item.module, item.item).first()
            assertNotNull(storageItem)
            val value = service.fetch(storageItem.item, item.keys, storageItem.storage, item.type)
            item.validation?.let { isValid ->
                assertTrue(isValid(value))
            }
        }
    }
}