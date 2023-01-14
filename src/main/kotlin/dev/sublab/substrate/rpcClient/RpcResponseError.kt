package dev.sublab.substrate.rpcClient

import kotlinx.serialization.Serializable

/**
 * RPC response error
 */
@Serializable
data class RpcResponseError(
    val code: Int,
    val message: String
)