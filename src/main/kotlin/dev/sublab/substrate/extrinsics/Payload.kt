package dev.sublab.substrate.extrinsics

import dev.sublab.common.ByteArrayConvertible

interface Payload: ByteArrayConvertible {
    val moduleName: String
    val callName: String
}