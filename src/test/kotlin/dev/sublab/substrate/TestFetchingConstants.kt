package dev.sublab.substrate

import dev.sublab.substrate.support.KusamaNetwork
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

private data class RpcConstant<T: Any>(
    val module: String,
    val constant: String,
    val type: KClass<T>,
    val expectedValue: T
)

class TestFetchingConstants {
    private val network = KusamaNetwork()
    private val client = SubstrateClient(url = network.rpcUrl)

    private val constants: List<RpcConstant<*>> = listOf(
        RpcConstant("babe", "epochDuration", ULong::class, 600UL),
        RpcConstant("babe", "expectedBlockTime", ULong::class, 6000UL),
        RpcConstant("balances", "existentialDeposit", ULong::class, 33333333UL),
        RpcConstant("crowdloan", "minContribution", ULong::class, 99999999000UL),
        RpcConstant("staking", "bondingDuration", UInt::class, 28U),
        RpcConstant("staking", "maxNominations", UInt::class, 24U),
        RpcConstant("staking", "sessionsPerEra", UInt::class, 6U),
        RpcConstant("system", "blockHashCount", UInt::class, 4096U),
        RpcConstant("system", "ss58Prefix", UShort::class, 2U),
        RpcConstant("timestamp", "minimumPeriod", ULong::class, 3000UL),
    )

    @Test
    internal fun testService() = runBlocking {
        val service = SubstrateConstantsService(client.codec, client.lookupService)
        for (constant in constants) {
            testConstant(service, constant)
        }
    }

    @Test
    internal fun testClient() = runBlocking {
        for (constant in constants) {
            testConstant(client.constantsService, constant)
        }
    }

    private fun <T: Any> testConstant(service: SubstrateConstantsService, constant: RpcConstant<T>) = runBlocking {
        service.fetch(constant.module, constant.constant, constant.type).take(1).collect {
            assertEquals(constant.expectedValue, it)
        }

        val runtimeConstant = service.find(constant.module, constant.constant)
        runtimeConstant.take(1).collect {
            assertNotNull(it)
            val value = service.fetch(it, constant.type)
            assertEquals(constant.expectedValue, value)
        }
    }
}