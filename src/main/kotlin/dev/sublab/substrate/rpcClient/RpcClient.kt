package dev.sublab.substrate.rpcClient

import dev.sublab.substrate.utils.serializer
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

internal const val rpcVersion = "2.0"

open class RpcResponseErrorException(override val message: String): Throwable()

class RpcRequestBuilder<P: Any, R: Any>(
    var method: String = "",
    var paramsType: KClass<P>? = null,
    var params: List<P>? = null,
    var responseType: KClass<R>? = null
)

class RpcClient(
    private val host: String,
    private val path: String? = null,
    private val params: Map<String, Any?> = mapOf()
) {
    // For testing purposes
    internal companion object

    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    private var requestCounter: Long = 0

    suspend fun send(request: RpcRequest): RpcResponse = httpClient.post {
        url {
            protocol = URLProtocol.HTTPS
            host = this@RpcClient.host
            pathSegments = listOfNotNull(this@RpcClient.path)
            for ((key, value) in params) {
                parameter(key, value)
            }
            method = HttpMethod.Post
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }.body()

    suspend fun <P: Any, R: Any> sendRequest(block: RpcRequestBuilder<P, R>.() -> Unit): R? {
        val builder = RpcRequestBuilder<P, R>()
        block(builder)

        val params = builder.params?.let {
            it.first()::class.createInstance()
            val paramsSerializer = serializer(builder.paramsType)
            Json.encodeToJsonElement(ListSerializer(paramsSerializer), it)
        } ?: JsonNull

        val responseSerializer = serializer(builder.responseType)

        val request = RpcRequest(
            id = ++requestCounter,
            method = builder.method,
            params = params
        )

        val rpcResponse: RpcResponse
        try {
            rpcResponse = send(request)
        } catch (error: Exception) {
            throw error // debugging purpose line
        }

        rpcResponse.error?.let {
            throw RpcResponseErrorException(it.message)
        }

        return rpcResponse.result?.let {
            Json.decodeFromJsonElement(responseSerializer, it)
        }
    }
}