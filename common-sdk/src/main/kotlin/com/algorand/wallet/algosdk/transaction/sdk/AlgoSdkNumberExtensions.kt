/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.wallet.algosdk.transaction.sdk

import com.algorand.algosdk.sdk.Uint64
import java.math.BigInteger

internal object AlgoSdkNumberExtensions {

    fun Long.toUint64(): Uint64 {
        return Uint64().apply {
            upper = shr(Int.SIZE_BITS)
            lower = and(Int.MAX_VALUE.toLong())
        }
    }

    fun BigInteger.toUint64(): Uint64 {
        return Uint64().apply {
            upper = shr(Int.SIZE_BITS).toLong()
            lower = and(UInt.MAX_VALUE.toLong().toBigInteger()).toLong()
        }
    }
}
