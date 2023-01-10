package dev.sublab.substrate

import dev.sublab.common.numerics.*
import dev.sublab.scale.ScaleCodec
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

internal class TestFetchingConstants {
    private val network = KusamaNetwork()
    private val client = network.makeClient()

    private val constants: List<RpcConstant<*>> = listOf(
        RpcConstant("babe", "epochDuration", UInt64::class, 600UL),
        RpcConstant("babe", "expectedBlockTime", UInt64::class, 6000UL),
        RpcConstant("balances", "existentialDeposit", UInt128::class, UInt128("333333333")),
        RpcConstant("crowdloan", "minContribution", UInt128::class, UInt128("999999999000")),
        RpcConstant("staking", "bondingDuration", UInt32::class, 28U),
        RpcConstant("staking", "maxNominations", UInt32::class, 24U),
        RpcConstant("staking", "sessionsPerEra", UInt32::class, 6U),
        RpcConstant("system", "blockHashCount", UInt32::class, 4096U),
        RpcConstant("system", "ss58Prefix", UInt16::class, 2U),
        RpcConstant("timestamp", "minimumPeriod", UInt64::class, 3000UL),
    )

    @Test
    fun testService() = runBlocking {
        val service = SubstrateConstantsService(ScaleCodec.default(), client.lookup)
        for (constant in constants) {
            testConstant(service, constant)
        }
    }

    @Test
    fun testClient() = runBlocking {
        for (constant in constants) {
            testConstant(client.constants, constant)
        }
    }

    private fun <T: Any> testConstant(service: SubstrateConstantsService, constant: RpcConstant<T>) = runBlocking {
        run {
            val fetchedConstant = service.fetch(constant.module, constant.constant, constant.type).first()
            assertEquals(constant.expectedValue, fetchedConstant)
        }

        run {
            val foundConstant = service.find(constant.module, constant.constant).first()
            assertNotNull(foundConstant)
            val fetchedConstant = service.fetch(foundConstant, constant.type)
            assertEquals(constant.expectedValue, fetchedConstant)
        }
    }
}