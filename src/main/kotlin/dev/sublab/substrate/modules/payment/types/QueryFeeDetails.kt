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

package dev.sublab.substrate.modules.payment.types

import dev.sublab.hex.hex
import dev.sublab.substrate.HexScaleCodec
import dev.sublab.substrate.scale.Balance
import kotlinx.serialization.Serializable

/**
 * Contains details of query fee
 */
data class QueryFeeDetails(
    val baseFee: Balance,
    val lenFee: Balance,
    val adjustedWeightFee: Balance
)

/**
 * Query fee details response
 */
@Serializable
internal data class QueryFeeDetailsResponse(
    val inclusionFee: InclusionFee
) {
    /**
     * Inclusion fee object
     */
    @Serializable
    data class InclusionFee(
        val baseFee: String,
        val lenFee: String,
        val adjustedWeightFee: String
    )

    /**
     * Creates a query fee details from inclusion fee
     * @return Generated query fee details
     */
    fun toFinal() = QueryFeeDetails(
        baseFee = Balance(inclusionFee.baseFee.hex.toBigInteger()),
        lenFee = Balance(inclusionFee.lenFee.hex.toBigInteger()),
        adjustedWeightFee = Balance(inclusionFee.adjustedWeightFee.hex.toBigInteger())
    )
}