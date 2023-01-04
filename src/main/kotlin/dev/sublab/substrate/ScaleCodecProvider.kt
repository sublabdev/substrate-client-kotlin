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