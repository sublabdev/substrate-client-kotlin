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

interface SystemRpc {
    suspend fun runtimeVersion(): RuntimeVersion?
    suspend fun accountByAccountId(accountId: ByteArrayConvertible): Account?
    suspend fun accountByAccountId(accountId: AccountId): Account?
    suspend fun accountByAccountId(accountIdHex: String): Account?
    suspend fun accountByPublicKey(publicKey: ByteArray): Account?
    suspend fun accountByPublicKey(publicKeyHex: String): Account?
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