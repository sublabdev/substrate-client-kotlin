package dev.sublab.substrate

import dev.sublab.common.numerics.UInt32
import dev.sublab.encrypting.keys.KeyPair
import dev.sublab.encrypting.mnemonic.DefaultMnemonic
import dev.sublab.hex.hex
import dev.sublab.scale.ScaleCodec
import dev.sublab.sr25519.sr25519
import dev.sublab.ss58.ss58
import dev.sublab.substrate.scale.Balance
import dev.sublab.substrate.scale.Index
import dev.sublab.substrate.support.Constants
import dev.sublab.substrate.support.KusamaNetwork
import dev.sublab.substrate.support.extrinsics.AddMemo
import extra.kotlin.util.UUID
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.math.BigInteger
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
    private val client = network.makeClient()

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
        // Unsigned
        val unsigned = client.extrinsics.makeUnsigned(
            moduleName = case.moduleName,
            callName = case.callName,
            callValue = case.callValue,
            callValueType = case.callValueType
        )

        println("case: $case")
        println("unsigned: ${unsigned.toByteArray().hex.encode(true)}")

        if (!case.unsignedHex.hex.decode().contentEquals(unsigned.toByteArray())) {
            println("Expected extrinsic encoded hex to be: ${case.unsignedHex}, received: ${unsigned.toByteArray().hex.encode(true)}")
        }
        assertContentEquals(case.unsignedHex.hex.decode(), unsigned.toByteArray())

        // Signed
//        val keyPair = KeyPair.Factory.sr25519().generate()
        // EM6Q1K1e5W4EaUD4gXCQd71ZoyyVreNSrQXy2PtKcfh71i1
        val seed = "recycle duty silver hunt option tonight task month crew twice churn level"
        val keyPair = KeyPair.Factory.sr25519().generate(seed)
        println("keypair seed: ${DefaultMnemonic.fromPhrase(seed).toSeed().hex.encode(true)}")
        println("keypair private key: ${keyPair.privateKey.hex.encode()}")
        println("keypair public key: ${keyPair.publicKey.hex.encode()}")

        val signed = client.extrinsics.makeSigned(
            moduleName = case.moduleName,
            callName = case.callName,
            callValue = case.callValue,
            callValueType = case.callValueType,
            tip = Balance(BigInteger("0")),
            keyPair.publicKey.ss58.accountId(),
            keyPair.getSignatureEngine(keyPair.privateKey)
        )

        val signedByteArray = signed.toByteArray()
        println("account account id: ${keyPair.publicKey.ss58.accountId().hex.encode(true)}")
        println("account address: ${keyPair.publicKey.ss58.address(network.addressType)}")
        println("signed: ${signedByteArray.hex.encode(true)}")
    }
}