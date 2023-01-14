package dev.sublab.substrate.extrinsics

import dev.sublab.common.ByteArrayConvertible

/**
 * An extrinsic's payload object
 */
interface Payload: ByteArrayConvertible {
    val moduleName: String
    val callName: String
}