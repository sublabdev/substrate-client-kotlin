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

package dev.sublab.substrate.scale

import dev.sublab.common.FromByteArray
import java.math.BigInteger

/**
 * Index type, like nonce
 */
@DynamicType(lookupIndex = 4)
class Index(byteArray: ByteArray): FromByteArray(byteArray) {
    constructor(value: BigInteger) : this(value.toByteArray().reversedArray())
    constructor(value: Int) : this(BigInteger.valueOf(value.toLong()))
    constructor(value: Long) : this(BigInteger.valueOf(value))

    val value = BigInteger(byteArray.reversedArray())
    // Required to convert to and from actual type
    override fun toByteArray(): ByteArray = value.toByteArray().reversedArray()
}

/**
 * Balance representation, used in transfers, etc
 */
@DynamicType(lookupIndex = 6)
class Balance(byteArray: ByteArray): FromByteArray(byteArray) {
    constructor(value: Int) : this(BigInteger.valueOf(value.toLong()))
    constructor(value: Long) : this(BigInteger.valueOf(value))
    constructor(value: BigInteger) : this(value.toByteArray().reversedArray())

    val value = BigInteger(byteArray.reversedArray())
    // Required to convert to and from actual type
    override fun toByteArray(): ByteArray = value.toByteArray().reversedArray()
}