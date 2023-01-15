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

import dev.sublab.scale.ScaleCodec
import dev.sublab.scale.ScaleCodecAdapterFactory
import dev.sublab.substrate.metadata.RuntimeMetadata
import dev.sublab.substrate.scale.DynamicAdapter
import dev.sublab.substrate.scale.DynamicAdapterProvider
import kotlinx.coroutines.flow.Flow

data class ScaleCodecProvider(
    val byteArray: ScaleCodec<ByteArray>,
    val hex: ScaleCodec<String>
) {
    companion object {
        fun default() = ScaleCodecProvider(
            ScaleCodec.default(),
            ScaleCodec.hex()
        )
    }

    internal fun applyRuntimeMetadata(runtimeMetadata: Flow<RuntimeMetadata>) {
        byteArray.provideDynamicAdapter(runtimeMetadata)
        hex.provideDynamicAdapter(runtimeMetadata)
    }
}

private fun <Data: Any> ScaleCodec<Data>.provideDynamicAdapter(runtimeMetadata: Flow<RuntimeMetadata>) = apply {
    this.settings.adapterProvider.addGenericAdapter(object : ScaleCodecAdapterFactory {
        override fun <T> make() = DynamicAdapter<T>(
            DynamicAdapterProvider(
                adapterResolver = settings.adapterProvider,
                runtimeMetadata = runtimeMetadata
            )
        )
    })
}