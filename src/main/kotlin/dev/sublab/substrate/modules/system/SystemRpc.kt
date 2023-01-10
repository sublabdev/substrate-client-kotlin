package dev.sublab.substrate.modules.system

import dev.sublab.substrate.SubstrateConstantsService
import dev.sublab.substrate.SubstrateStorageService
import dev.sublab.substrate.modules.system.constants.RuntimeVersion
import dev.sublab.substrate.modules.system.storage.Account
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

interface SystemRpc {
    suspend fun runtimeVersion(): RuntimeVersion?
    suspend fun account(): Account?
}

class SystemRpcClient(
    private val constants: SubstrateConstantsService,
    private val storage: SubstrateStorageService
): SystemRpc {
    override suspend fun runtimeVersion() = constants
        .fetch("system", "version", RuntimeVersion::class).first()

    override suspend fun account() = storage
        .fetch("system", "account", Account::class).first()
}