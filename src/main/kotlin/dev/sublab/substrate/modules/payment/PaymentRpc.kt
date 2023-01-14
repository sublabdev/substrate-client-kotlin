package dev.sublab.substrate.modules.payment

import dev.sublab.hex.hex
import dev.sublab.substrate.HexScaleCodec
import dev.sublab.substrate.extrinsics.Payload
import dev.sublab.substrate.modules.payment.types.QueryFeeDetails
import dev.sublab.substrate.modules.payment.types.QueryFeeDetailsResponse
import dev.sublab.substrate.rpcClient.RpcClient

/**
 * An interface for getting a query fee details response
 */
interface PaymentRpc {
    /**
     * Gets query fee details for a payload
     * @param payload a payload for which query fee details should be fetched
     * @return A nullable query fee details
     */
    suspend fun getQueryFeeDetails(payload: Payload): QueryFeeDetails?
}

/**
 * Handles payment query fee details fetching
 */
class PaymentRpcClient(
    private val codec: HexScaleCodec,
    private val rpcClient: RpcClient
): PaymentRpc {
    override suspend fun getQueryFeeDetails(payload: Payload) = rpcClient.sendRequest<String, QueryFeeDetailsResponse> {
        method = "payment_queryFeeDetails"
        responseType = QueryFeeDetailsResponse::class
        params = listOf(payload.toByteArray().hex.encode(true))
        paramsType = String::class
    }?.toFinal()
}