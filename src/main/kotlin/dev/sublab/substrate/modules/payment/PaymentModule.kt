/**
 *
 * Copyright 2023 SUBSTRATE LABORATORY LLC <info@sublab.dev>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

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
interface PaymentModule {
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
class PaymentModuleClient(
    private val codec: HexScaleCodec,
    private val rpcClient: RpcClient
): PaymentModule {
    override suspend fun getQueryFeeDetails(payload: Payload) = rpcClient.sendRequest<String, QueryFeeDetailsResponse> {
        method = "payment_queryFeeDetails"
        responseType = QueryFeeDetailsResponse::class
        params = listOf(payload.toByteArray().hex.encode(true))
        paramsType = String::class
    }?.toFinal()
}