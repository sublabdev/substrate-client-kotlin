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
import dev.sublab.substrate.SubstrateConstantsService
import dev.sublab.substrate.SubstrateStorageService
import dev.sublab.substrate.modules.system.constants.RuntimeVersion
import dev.sublab.substrate.modules.system.storage.Account
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * An interface for system RPC
 */
interface SystemRpc {
    /**
     * Returns an nullable runtime version
     */
    suspend fun runtimeVersion(): RuntimeVersion?

    /**
     * Returns [Account] by its id as [ByteArrayConvertible]
     * @param accountId An account id as [ByteArrayConvertible], used to find an account
     * @return A nullable [Account] based on its id
     */
    suspend fun accountByAccountId(accountId: ByteArrayConvertible): Account?

    /**
     * Returns [Account] by its id
     * @param accountId An account id, used to find an account
     * @return A nullable [Account] based on its id
     */
    suspend fun accountByAccountId(accountId: AccountId): Account?

    /**
     * Returns [Account] by its id hex
     * @param accountIdHex An account id hex, used to find an account
     * @return A nullable [Account] based on its id hex
     */
    suspend fun accountByAccountId(accountIdHex: String): Account?

    /**
     * Returns [Account] by public key
     * @param publicKey a public key, used to find an account
     * @return A nullable [Account] based on a public key
     */
    suspend fun accountByPublicKey(publicKey: ByteArray): Account?

    /**
     * Returns [Account] by public key hex
     * @param publicKeyHex a public key hex, used to find an account
     * @return A nullable [Account] based on a public key hex
     */
    suspend fun accountByPublicKey(publicKeyHex: String): Account?

    /**
     * Returns [Account] by key pair of public and private keys
     * @param keyPair a key pair of ublic and private keys, used to find an account
     * @return A nullable [Account] based on a key pair
     */
    suspend fun accountByKeyPair(keyPair: KeyPair): Account?
}

class SystemRpcClient(
    private val constants: SubstrateConstantsService,
    private val storage: SubstrateStorageService
): SystemRpc {
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