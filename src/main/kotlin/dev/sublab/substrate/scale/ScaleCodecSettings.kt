package dev.sublab.substrate.scale

import dev.sublab.scale.ScaleCodecSettings
import dev.sublab.scale.dataContainers.ScaleByteArrayDataContainer
import dev.sublab.scale.dataContainers.ScaleHexDataContainer
import dev.sublab.scale.default.DefaultScaleCodecAdapterProvider


fun ScaleCodecSettings.Companion.defaultDynamic() = ScaleCodecSettings(
    dataContainer = ScaleByteArrayDataContainer(),
    adapterProvider = DefaultScaleCodecAdapterProvider()
)

fun hex() = ScaleCodecSettings(
    dataContainer = ScaleHexDataContainer(),
    adapterProvider = DefaultScaleCodecAdapterProvider()
)