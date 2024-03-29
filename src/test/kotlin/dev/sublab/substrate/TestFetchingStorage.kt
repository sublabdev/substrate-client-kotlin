/**
 *
 * Copyright 2023 SUBSTRATE LABORATORY LLC <info@sublab.dev>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

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
    private val client = network.makeClient()

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
        // Unwrap to internal type for tests
        val lookup = (client.lookup as? SubstrateLookupService) ?: run {
            assert(false)
            return
        }

        val service = SubstrateStorageService(ScaleCodec.default(), lookup, client.modules.state)
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

    private fun <T: Any> testStorageItem(service: SubstrateStorage, item: RpcStorageItem<T>) = runBlocking {
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