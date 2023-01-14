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

@Serializable
internal data class QueryFeeDetailsResponse(
    val inclusionFee: InclusionFee
) {
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