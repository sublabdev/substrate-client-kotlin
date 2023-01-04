package dev.sublab.substrate.extrinsics

import dev.sublab.hashing.hashers.blake2b_256
import dev.sublab.hashing.hashing
import dev.sublab.scale.ScaleCodec
import dev.sublab.ss58.AccountId
import dev.sublab.substrate.metadata.RuntimeMetadata
import dev.sublab.substrate.scale.Balance
import dev.sublab.substrate.scale.Index

internal typealias SignatureBlock = (ByteArray) -> ByteArray

internal class SignedPayload<T: Any>(
    private val runtimeMetadata: RuntimeMetadata,
    private val codec: ScaleCodec<ByteArray>,
    private val payload: UnsignedPayload<T>,
    private val accountId: AccountId,
    private val nonce: Index,
    private val tip: Balance,
    private val signatureBlock: SignatureBlock
): Payload {
    override val moduleName: String get() = payload.moduleName
    override val callName: String get() = payload.callName

    private fun extra() = ExtrinsicExtra(nonce = nonce.value, tip = tip)
    private fun additional(): ExtrinsicAdditional /*= ExtrinsicAdditional(
        specVersion = runtimeMetadata.version,
        txVersion = runtimeMetadata.
    )*/ { null!! }

    private fun signingPayload() = codec.transaction()
        .append(payload)
        .append(extra(), ExtrinsicExtra::class)
        .append(additional(), ExtrinsicAdditional::class)
        .commit()

    private fun sign() = signingPayload().run {
        if (size > 256) signatureBlock(hashing.blake2b_256())
        else signatureBlock(this)
    }

    private fun makeExtrinsic() = Extrinsic(
        address = accountId,
        signature = sign(),
        extra = extra(),
        payload = payload.toByteArray()
    )

    override fun toByteArray() = codec.toScale(makeExtrinsic(), Extrinsic::class)
}