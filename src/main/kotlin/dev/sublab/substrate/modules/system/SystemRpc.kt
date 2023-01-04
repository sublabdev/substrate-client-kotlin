package dev.sublab.substrate.modules.system

import dev.sublab.substrate.SubstrateConstantsService
import dev.sublab.substrate.modules.system.constants.RuntimeVersion
import kotlinx.coroutines.flow.Flow

interface SystemRpc {
    fun runtimeVersion(): Flow<RuntimeVersion?>
}

class SystemRpcClient(
    private val constants: SubstrateConstantsService
): SystemRpc {
    override fun runtimeVersion() = constants.fetch("system", "version", RuntimeVersion::class)
}