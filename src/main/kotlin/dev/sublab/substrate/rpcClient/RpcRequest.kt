package dev.sublab.substrate.rpcClient

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement

/**
 * RPC request with generic params
 */
@Serializable
data class RpcRequest constructor(
    @EncodeDefault val jsonrpc: String = rpcVersion,
    val id: Long,
    val method: String,
    val params: JsonElement = JsonArray(listOf())
)