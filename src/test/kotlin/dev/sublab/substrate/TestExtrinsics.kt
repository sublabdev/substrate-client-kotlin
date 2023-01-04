package dev.sublab.substrate

import dev.sublab.common.numerics.UInt32
import dev.sublab.hex.hex
import dev.sublab.scale.ScaleCodec
import dev.sublab.substrate.support.Constants
import dev.sublab.substrate.support.KusamaNetwork
import dev.sublab.substrate.support.extrinsics.AddMemo
import extra.kotlin.util.UUID
import kotlinx.coroutines.runBlocking
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertNotNull

private data class ExtrinsicTestCase<T: Any>(
    val moduleName: String,
    val callName: String,
    val callValue: T,
    val callValueType: KClass<T>,
    val unsignedHex: String
)

internal class TestExtrinsics {
    private val network = KusamaNetwork()
    private val client = SubstrateClient(url = network.rpcUrl)

    private fun generatedAddMemoCases() = (0 until Constants.testsCount).map {
        val randomIndex = (UInt32.MIN_VALUE..UInt32.MAX_VALUE).random()
        val scaleEncodedRandomIndex = ScaleCodec.default().toScale(randomIndex, UInt32::class)

        val randomString = UUID.uuid4().toString()
        val scaleEncodedRandomString = ScaleCodec.default().toScale(randomString, String::class)

        val addMemoPrefix = "0x4906".hex.decode()

        val finalHex = (addMemoPrefix + scaleEncodedRandomIndex + scaleEncodedRandomString).hex.encode()

        ExtrinsicTestCase(
            moduleName = "crowdloan",
            callName = "add_memo",
            callValue = AddMemo(randomIndex, randomString.toByteArray()),
            callValueType = AddMemo::class,
            unsignedHex = finalHex
        )
    }

    private val testCases = listOf<ExtrinsicTestCase<*>>(
        ExtrinsicTestCase(
            moduleName = "crowdloan",
            callName = "add_memo",
            callValue = AddMemo(0u, "hi".toByteArray()),
            callValueType = AddMemo::class,
            unsignedHex = "0x490600000000086869"
        )
    ) + generatedAddMemoCases()

    @Test
    fun test() = runBlocking {
        for (case in testCases) {
            testCase(case)
        }
    }

    private suspend fun <T: Any> testCase(case: ExtrinsicTestCase<T>) {
        val unsigned = client.extrinsics.makeUnsigned(
            case.moduleName,
            case.callName,
            case.callValue,
            case.callValueType
        )

        assertNotNull(unsigned)
        if (!case.unsignedHex.hex.decode().contentEquals(unsigned.toByteArray())) {
            println("Expected extrinsic encoded hex to be: ${case.unsignedHex}, received: ${unsigned.toByteArray().hex.encode(true)}")
        }
        assertContentEquals(case.unsignedHex.hex.decode(), unsigned.toByteArray())
    }
}