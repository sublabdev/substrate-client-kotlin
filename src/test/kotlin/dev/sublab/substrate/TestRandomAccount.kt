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
        KeyPair.Factory.ecdsa(Kind.ETHEREUM),
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
        }
    }
}