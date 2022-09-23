package dev.sublab.substrate.rpcClient

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class RpcResponse(
    val jsonrpc: String,
    val id: Long,
    val result: JsonElement? = null,
    val error: RpcResponseError? = null
)