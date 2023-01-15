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
    )// + generatedAddMemoCases()

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

        if (!case.unsignedHex.hex.decode().contentEquals(unsigned.toByteArray())) {
            println("Expected extrinsic encoded hex to be: ${case.unsignedHex}, received: ${unsigned.toByteArray().hex.encode(true)}")
        }
        assertContentEquals(case.unsignedHex.hex.decode(), unsigned.toByteArray())

        // Signed
        val keyPair = KeyPair.Factory.sr25519().generate()

        val signed = client.extrinsics.makeSigned(
            moduleName = case.moduleName,
            callName = case.callName,
            callValue = case.callValue,
            callValueType = case.callValueType,
            tip = Balance(BigInteger("0")),
            accountId = keyPair.publicKey.ss58.accountId(),
            nonce = Index(BigInteger("0")),
            signatureEngine = keyPair.getSignatureEngine(keyPair.privateKey)
        )

        println("network: ${network}, signed: ${signed.toByteArray().hex.encode(true)}")

        val queryFeeDetails = client.modules.paymentRpc().getQueryFeeDetails(signed)
        assertNotNull(queryFeeDetails)
        assert(queryFeeDetails.baseFee.value > BigInteger.ZERO)
    }
}