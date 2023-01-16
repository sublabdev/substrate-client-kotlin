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

package dev.sublab.substrate.modules.system

import dev.sublab.common.ByteArrayConvertible
import dev.sublab.common.asByteArrayConvertible
import dev.sublab.encrypting.keys.KeyPair
import dev.sublab.hex.hex
import dev.sublab.ss58.AccountId
import dev.sublab.ss58.ss58
import dev.sublab.substrate.SubstrateConstants
import dev.sublab.substrate.SubstrateStorage
import dev.sublab.substrate.modules.system.constants.RuntimeVersion
import dev.sublab.substrate.modules.system.storage.Account
import kotlinx.coroutines.flow.first

interface SystemModule {
    suspend fun runtimeVersion(): RuntimeVersion?
    suspend fun accountByAccountId(accountId: ByteArrayConvertible): Account?
    suspend fun accountByAccountId(accountId: AccountId): Account?
    suspend fun accountByAccountId(accountIdHex: String): Account?
    suspend fun accountByPublicKey(publicKey: ByteArray): Account?
    suspend fun accountByPublicKey(publicKeyHex: String): Account?
    suspend fun accountByKeyPair(keyPair: KeyPair): Account?
}

class SystemModuleClient(
    private val constants: SubstrateConstants,
    private val storage: SubstrateStorage
): SystemModule {
    override suspend fun runtimeVersion() = constants
        .fetch("system", "version", RuntimeVersion::class).first()

    override suspend fun accountByAccountId(accountId: ByteArrayConvertible) = storage
        .fetch("system", "account", accountId, Account::class).first()

    override suspend fun accountByAccountId(accountId: AccountId)
        = accountByAccountId(accountId.asByteArrayConvertible())

    override suspend fun accountByAccountId(accountIdHex: String)
            = accountByAccountId(accountIdHex.hex.decode())

    override suspend fun accountByPublicKey(publicKey: ByteArray)
            = accountByAccountId(publicKey.ss58.accountId())

    override suspend fun accountByPublicKey(publicKeyHex: String)
            = accountByPublicKey(publicKeyHex.hex.decode())

    override suspend fun accountByKeyPair(keyPair: KeyPair)
            = accountByPublicKey(keyPair.publicKey)
}