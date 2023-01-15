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

import dev.sublab.encrypting.keys.KeyPair
import dev.sublab.encrypting.signing.SignatureEngine
import dev.sublab.scale.ScaleCodec
import dev.sublab.ss58.AccountId
import dev.sublab.ss58.ss58
import dev.sublab.substrate.extrinsics.*
import dev.sublab.substrate.metadata.RuntimeMetadata
import dev.sublab.substrate.metadata.lookup.RuntimeType
import dev.sublab.substrate.metadata.lookup.type.RuntimeTypeDef
import dev.sublab.substrate.metadata.lookup.type.def.RuntimeTypeDefVariant
import dev.sublab.substrate.metadata.modules.RuntimeModule
import dev.sublab.substrate.modules.chain.ChainRpc
import dev.sublab.substrate.modules.system.SystemRpc
import dev.sublab.substrate.scale.Balance
import dev.sublab.substrate.scale.Index
import dev.sublab.sugar.or
import kotlinx.coroutines.flow.*
import java.math.BigInteger
import kotlin.reflect.KClass

private data class RuntimeCall(val module: RuntimeModule, val variant: RuntimeTypeDefVariant.Variant)

/**
 * Substrate extrinsics service
 */
class SubstrateExtrinsicsService(
    private val runtimeMetadata: Flow<RuntimeMetadata>,
    private val systemRpc: SystemRpc,
    private val chainRpc: ChainRpc,
    private val codec: ScaleCodec<ByteArray>,
    private val lookup: SubstrateLookupService,
    private val storage: SubstrateStorageService,
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
//                println("call with '${module.name}_$callName' index: ${module.index}, ${it.index}")
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
        ?: throw RuntimeCallUnknownException()

    internal suspend fun <T: Any> makeUnsigned(
        moduleName: String,
        callName: String,
        callValue: T,
        callValueType: KClass<T>
    ): Payload = makePayload(moduleName, callName, callValue, callValueType)

    /**
     * Makes an unsigned payload from a call
     * @param call a call used to make an unsigned payload
     */
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
        signatureEngine: SignatureEngine
    ): Payload = SignedPayload(
        runtimeMetadata = runtimeMetadata.first(),
        codec = codec,
        payload = makePayload(moduleName, callName, callValue, callValueType),
        runtimeVersion = systemRpc.runtimeVersion() ?: throw RuntimeVersionNotKnownException(),
        genesisHash = chainRpc.getBlockHash(0) ?: throw GenesisHashNotKnownException(),
        accountId = accountId,
        nonce = systemRpc.accountByAccountId(accountId)?.nonce ?: throw NonceNotKnownException(),
        tip = tip,
        signatureEngine = signatureEngine
    )

    // Tests for empty account
    internal suspend fun <T: Any> makeSigned(
        moduleName: String,
        callName: String,
        callValue: T,
        callValueType: KClass<T>,
        tip: Balance,
        accountId: AccountId,
        nonce: Index,
        signatureEngine: SignatureEngine
    ): Payload = SignedPayload(
        runtimeMetadata = runtimeMetadata.first(),
        codec = codec,
        payload = makePayload(moduleName, callName, callValue, callValueType),
        runtimeVersion = systemRpc.runtimeVersion() ?: throw RuntimeVersionNotKnownException(),
        genesisHash = chainRpc.getBlockHash(0) ?: throw GenesisHashNotKnownException(),
        accountId = accountId,
        nonce = nonce,
        tip = tip,
        signatureEngine = signatureEngine
    )

    /**
     * Makes a signed payload
     * @param call a call with a generic type [T]
     * @param tip a balance representation, used in transfers
     * @param accountId an account id
     * @param signatureEngine a signature engine
     * @return Returns a signed payload
     */
    suspend fun <T: Any> makeSigned(
        call: Call<T>,
        tip: Balance,
        accountId: AccountId,
        signatureEngine: SignatureEngine
    ) = makeSigned(
        moduleName = call.moduleName,
        callName = call.name,
        callValue = call.value,
        callValueType = call.type,
        tip = tip,
        accountId = accountId,
        signatureEngine = signatureEngine
    )

    /**
     * Makes a signed payload
     * @param call a call with a generic type [T]
     * @param tip a balance representation, used in transfers
     * @param keyPair a [KeyPair] of public and private keys
     * @return Returns a signed payload
     */
    suspend fun <T: Any> makeSigned(
        call: Call<T>,
        tip: Balance,
        keyPair: KeyPair
    ) = makeSigned(call, tip, keyPair.publicKey.ss58.accountId(), keyPair.getSignatureEngine(keyPair.privateKey))
}