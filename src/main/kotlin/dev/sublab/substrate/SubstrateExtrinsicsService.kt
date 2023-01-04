package dev.sublab.substrate

import dev.sublab.encrypting.keys.KeyPair
import dev.sublab.scale.ScaleCodec
import dev.sublab.ss58.AccountId
import dev.sublab.ss58.ss58
import dev.sublab.substrate.extrinsics.*
import dev.sublab.substrate.metadata.RuntimeMetadata
import dev.sublab.substrate.metadata.lookup.RuntimeType
import dev.sublab.substrate.metadata.lookup.type.RuntimeTypeDef
import dev.sublab.substrate.metadata.lookup.type.def.RuntimeTypeDefVariant
import dev.sublab.substrate.metadata.modules.RuntimeModule
import dev.sublab.substrate.scale.Balance
import dev.sublab.substrate.scale.Index
import dev.sublab.sugar.or
import kotlinx.coroutines.flow.*
import java.math.BigInteger
import kotlin.reflect.KClass

private data class RuntimeCall(val module: RuntimeModule, val variant: RuntimeTypeDefVariant.Variant)

class SubstrateExtrinsicsService(
    private val runtimeMetadata: Flow<RuntimeMetadata>,
    private val codec: ScaleCodec<ByteArray>,
    private val lookup: SubstrateLookupService,
    private val namingPolicy: SubstrateClientNamingPolicy
) {

    private fun findCall(variant: RuntimeTypeDefVariant, callName: String) = variant.variants.firstOrNull {
        namingPolicy.equals(callName, it.name)
    }

    private fun findCall(typeDef: RuntimeTypeDef?, callName: String): RuntimeTypeDefVariant.Variant? = when (typeDef) {
        is RuntimeTypeDef.Variant -> findCall(typeDef.variant, callName)
        else -> null
    }

    private fun findCall(module: RuntimeModule, callName: String) = module.callIndex
        ?.let { lookup.findRuntimeType(it) }.or(flowOf<RuntimeType?>(null))
        .map { runtimeType ->
            findCall(runtimeType?.def, callName)?.let {
                RuntimeCall(module, it)
            }
        }

    private fun findCall(moduleName: String, callName: String) = lookup
        .findModule(moduleName)
        .flatMapLatest { module ->
            module?.let { findCall(it, callName) }.or(flowOf(null))
        }

    private suspend fun <T: Any> makePayload(
        moduleName: String,
        callName: String,
        callValue: T,
        callValueType: KClass<T>
    ) = findCall(moduleName, callName)
        .first()
        ?.let { UnsignedPayload(codec, it.module, it.variant, callValue, callValueType) }

    internal suspend fun <T: Any> makeUnsigned(
        moduleName: String,
        callName: String,
        callValue: T,
        callValueType: KClass<T>
    ): Payload? = makePayload(moduleName, callName, callValue, callValueType)

    suspend fun <T: Any> makeUnsigned(call: Call<T>) = makeUnsigned(
        moduleName = call.moduleName,
        callName = call.name,
        callValue = call.value,
        callValueType = call.type
    )

    internal suspend fun <T: Any> makeSigned(
        moduleName: String,
        callName: String,
        callValue: T,
        callValueType: KClass<T>,
        tip: Balance,
        accountId: AccountId,
        signatureBlock: SignatureBlock
    ): Payload? = makePayload(moduleName, callName, callValue, callValueType)?.let {
        SignedPayload(
            runtimeMetadata = runtimeMetadata.first(),
            codec = codec,
            payload = it,
            accountId = accountId,
            nonce = Index(BigInteger.valueOf(1)), // TODO: Provide nonce
            tip = tip,
            signatureBlock = signatureBlock
        )
    }

    suspend fun <T: Any> makeSigned(
        call: Call<T>,
        tip: Balance,
        accountId: AccountId,
        signatureBlock: SignatureBlock
    ) = makeSigned(
        moduleName = call.moduleName,
        callName = call.name,
        callValue = call.value,
        callValueType = call.type,
        tip = tip,
        accountId = accountId,
        signatureBlock = signatureBlock
    )

    suspend fun <T: Any> makeSigned(
        call: Call<T>,
        tip: Balance,
        keyPair: KeyPair
    ) = makeSigned(call, tip, keyPair.publicKey.ss58.accountId()) {
        keyPair.sign(it)
    }
}