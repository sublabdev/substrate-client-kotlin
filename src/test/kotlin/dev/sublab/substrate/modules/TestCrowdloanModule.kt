package dev.sublab.substrate.modules

import dev.sublab.encrypting.keys.KeyPair
import dev.sublab.sr25519.sr25519
import dev.sublab.substrate.modules.crowdloan.calls.AddMemo
import dev.sublab.substrate.modules.crowdloan.calls.AddMemoCall
import dev.sublab.substrate.support.KusamaNetwork
import extra.kotlin.util.UUID
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import java.math.BigInteger
import kotlin.test.Test
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
internal class TestCrowdloanModule {
    private val network = KusamaNetwork()
    private val client = network.makeClient()

    @Test
    fun `add memo`() = runTest {
        val addMemo = AddMemoCall(AddMemo(0u, UUID.uuid4().toString().toByteArray()))
        val keyPair = KeyPair.sr25519().generate()

        val extrinsic = client.extrinsics.makeSigned(call = addMemo, keyPair = keyPair)
        assertNotNull(extrinsic)

        val fee = client.modules.payment.getQueryFeeDetails(extrinsic)
        assertNotNull(fee)

        assert(fee.baseFee.value > BigInteger.ZERO)
    }
}