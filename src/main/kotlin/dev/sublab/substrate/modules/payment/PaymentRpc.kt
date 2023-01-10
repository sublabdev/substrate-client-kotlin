package dev.sublab.substrate.modules.payment

import dev.sublab.hex.hex
import dev.sublab.substrate.HexScaleCodec
import dev.sublab.substrate.extrinsics.Payload
import dev.sublab.substrate.modules.payment.types.QueryFeeDetails
import dev.sublab.substrate.modules.payment.types.QueryFeeDetailsResponse
import dev.sublab.substrate.rpcClient.RpcClient

interface PaymentRpc {
    suspend fun getQueryFeeDetails(payload: Payload): QueryFeeDetails?
}

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