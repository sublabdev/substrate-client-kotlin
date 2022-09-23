package dev.sublab.substrate.rpcClient

import kotlinx.serialization.Serializable

@Serializable
data class RpcResponseError(
    val code: Int,
    val message: String
)