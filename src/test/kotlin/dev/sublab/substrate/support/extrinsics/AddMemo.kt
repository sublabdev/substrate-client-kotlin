package dev.sublab.substrate.support.extrinsics

import dev.sublab.common.numerics.UInt32
import dev.sublab.substrate.extrinsics.Call

internal data class AddMemo(val index: UInt32, val memo: ByteArray)

internal class AddMemoCall(value: AddMemo) : Call<AddMemo>(
    moduleName = "crowdloan",
    name = "add_memo",
    value = value,
    type = AddMemo::class
)