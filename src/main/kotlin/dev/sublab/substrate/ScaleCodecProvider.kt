package dev.sublab.substrate

import dev.sublab.scale.ScaleCodec

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
}