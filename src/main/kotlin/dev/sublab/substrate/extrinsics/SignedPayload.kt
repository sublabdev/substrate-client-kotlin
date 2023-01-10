package dev.sublab.substrate.extrinsics

import dev.sublab.common.numerics.UInt8
import dev.sublab.encrypting.signing.SignatureEngine
import dev.sublab.hashing.hashers.blake2b_256
import dev.sublab.hashing.hashing
import dev.sublab.hex.hex
import dev.sublab.scale.ScaleCodec
import dev.sublab.scale.ScaleCodecTransaction
import dev.sublab.scale.types.ScaleEncodedByteArray
import dev.sublab.scale.types.asScaleEncoded
import dev.sublab.ss58.AccountId
import dev.sublab.substrate.metadata.RuntimeMetadata
import dev.sublab.substrate.metadata.lookup.type.RuntimeTypeDef
import dev.sublab.substrate.modules.system.constants.RuntimeVersion
import dev.sublab.substrate.scale.Balance
import dev.sublab.substrate.scale.Index
import java.math.BigInteger

internal typealias SignatureBlock = (ByteArray) -> ByteArray

internal class SignedPayload<T: Any>(
    internal val runtimeMetadata: RuntimeMetadata,
    private val codec: ScaleCodec<ByteArray>,
    private val payload: UnsignedPayload<T>,
    internal val runtimeVersion: RuntimeVersion,
    internal val genesisHash: String,
    internal val era: Era = Era.Immortal(),
    internal val blockHash: String = genesisHash,
    internal val accountId: AccountId,
    internal val nonce: Index,
    internal val tip: Balance,
    internal val signatureEngine: SignatureEngine
): Payload {
    override val moduleName: String get() = payload.moduleName
    override val callName: String get() = payload.callName

    private fun signingPayload() = codec.transaction()
        .apply { println("[extrinsic-signing] add payload") }
        .append(payload)
        .apply { println("[extrinsic-signing] add extra") }
//        .append(extra(), ExtrinsicExtra::class)
        .appendExtra(this)
        .apply { println("[extrinsic-signing] add additional") }
        .appendAdditional(this)
        .commit()

    internal fun sign() = signingPayload().run {
        println("signing payload: ${this.hex.encode(true)}, signature: ${
            if (size > 256) signatureEngine.sign(hashing.blake2b_256())
            else signatureEngine.sign(this)
                .hex.encode(true)}")
        if (size > 256) signatureEngine.sign(hashing.blake2b_256())
        else signatureEngine.sign(this)
    }

//    private fun makeExtrinsic() = Extrinsic(
//        version = (0b10000000.toUInt() + runtimeMetadata.extrinsic.version.toUInt()).toUByte(),
//        address = accountId,
//        signature = sign(),
//        extra = extra(),
//        additional = codec.transaction().appendAdditional(this).commit().asScaleEncoded(),
//        payload = payload.toByteArray()
//    ).apply {
//        println("made extrinsic: $this")
//    }
//
//    override fun toByteArray() = codec.toScale(makeExtrinsic(), Extrinsic::class)

    private fun makeExtrinsic() = codec.transaction()
        .apply { println("[extrinsic]") }
        // "is signed" + transaction protocol version
        .apply { println("[extrinsic] add prefix") }
        .append((0b10000000.toUInt() + runtimeMetadata.extrinsic.version.toUInt()).toUByte(), UInt8::class)
        // from address for signature
        .apply { println("[extrinsic] add account id") }
//        .append(accountId, ByteArray::class)
        .appendAccountId(this)
        .apply { println("[extrinsic] add signature") }
        .appendSignature(this)
//        .append(sign().also { println("[signature] signature: ${it.hex.encode(true)}") }.asScaleEncoded(), ScaleEncodedByteArray::class)
        .apply { println("[extrinsic] add extra") }
//        .append(extra(), ExtrinsicExtra::class)
        .appendExtra(this)
//        .appendAdditional(this)
        .apply { println("[extrinsic] add payload") }
        .append(payload.toByteArray().asScaleEncoded(), ScaleEncodedByteArray::class) // Ignore size, inject directly
        .commit()

    override fun toByteArray() = codec.toScale(makeExtrinsic(), ByteArray::class)
}

private fun RuntimeMetadata.findSignatureTypeDef(name: String): RuntimeTypeDef {
    val extrinsicTypeIndex = extrinsic.type
    val extrinsicType = lookup.findItemByIndex(extrinsicTypeIndex)
        ?: throw ExtrinsicBuildFailedDueToLookupFailureException()

    val lookupTypeIndex = extrinsicType.params.firstOrNull { it.name == name }?.type
        ?: throw ExtrinsicBuildFailedDueToLookupFailureException()
    return lookup.findItemByIndex(lookupTypeIndex)?.def
        ?: throw ExtrinsicBuildFailedDueToLookupFailureException()
}

private fun <T: Any, Data: Any> ScaleCodecTransaction<Data>.appendAccountId(signedPayload: SignedPayload<T>) = apply {
    val addressTypeDef = signedPayload.runtimeMetadata.findSignatureTypeDef("Address")
    val addressVariants = (addressTypeDef as? RuntimeTypeDef.Variant)?.variant?.variants
        ?: throw ExtrinsicBuildFailedDueToLookupFailureException()

    val addressIdTypeIndex = addressVariants.firstOrNull { it.name == "Id" }?.indexUInt8
        ?: throw ExtrinsicBuildFailedDueToLookupFailureException()

    println("[account-id] index: $addressIdTypeIndex")
    append(addressIdTypeIndex, UInt8::class)
    // Ignore size, inject directly
    append(signedPayload.accountId.asScaleEncoded(), ScaleEncodedByteArray::class)
}

private fun <T: Any, Data: Any> ScaleCodecTransaction<Data>.appendSignature(signedPayload: SignedPayload<T>) = apply {
    val signaturesTypeDef = signedPayload.runtimeMetadata.findSignatureTypeDef("Signature")

    val signatureVariants = (signaturesTypeDef as? RuntimeTypeDef.Variant)?.variant?.variants
        ?: throw ExtrinsicBuildFailedDueToLookupFailureException()

    val signatureTypeIndex = signatureVariants.firstOrNull {
        it.name.lowercase() == signedPayload.signatureEngine.name.lowercase()
    }?.indexUInt8 ?: throw ExtrinsicBuildFailedDueToLookupFailureException()

    println("[signature] index: $signatureTypeIndex]")
    append(signatureTypeIndex, UInt8::class)
    append(signedPayload.sign().asScaleEncoded(), ScaleEncodedByteArray::class)
}

private fun <T: Any, Data: Any> ScaleCodecTransaction<Data>.appendExtra(signedPayload: SignedPayload<T>) = apply {
    for (extension in signedPayload.runtimeMetadata.extrinsic.signedExtensions) {
        when (extension.identifier) {
            "CheckMortality" -> apply { println("[extra] add mortality: ${signedPayload.era}") }.append(signedPayload.era, Era::class)
            "CheckNonce" -> apply { println("[extra] add nonce: ${signedPayload.nonce.value}") }.append(signedPayload.nonce.value, BigInteger::class)
            "ChargeTransactionPayment" -> apply { println("[extra] add tip: ${signedPayload.tip.value}") }.append(signedPayload.tip.value, BigInteger::class)
        }
    }
}

private fun <T: Any, Data: Any> ScaleCodecTransaction<Data>.appendAdditional(signedPayload: SignedPayload<T>) = apply {
    for (extension in signedPayload.runtimeMetadata.extrinsic.signedExtensions) {
        when (extension.identifier) {
            "CheckGenesis" -> apply { println("[add] add genesis hash: ${signedPayload.genesisHash}") }.append(signedPayload.genesisHash.hex.decode().asScaleEncoded(), ScaleEncodedByteArray::class)
            "CheckMortality" -> apply { println("[add] add block hash: ${signedPayload.blockHash}") }.append(signedPayload.blockHash.hex.decode().asScaleEncoded(), ScaleEncodedByteArray::class)
            "CheckSpecVersion" -> apply { println("[add] add spec version: ${signedPayload.runtimeVersion.specVersion.value}") }.append(signedPayload.runtimeVersion.specVersion, Index::class)
            "CheckTxVersion" -> apply { println("[add] add tx version: ${signedPayload.runtimeVersion.txVersion.value}") }.append(signedPayload.runtimeVersion.txVersion, Index::class)
        }
    }
}