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

package com.algorand.wallet.algosdk.domain.model

data class HdKeyAccount(
    val address: String,
    val publicKey: ByteArray,
    val privateKey: ByteArray,
    val entropy: ByteArray,
    val account: Int,
    val change: Int,
    val keyIndex: Int,
    val derivationType: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as HdKeyAccount

        if (address != other.address) return false
        if (!privateKey.contentEquals(other.privateKey)) return false
        if (!entropy.contentEquals(other.entropy)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = address.hashCode()
        result = 31 * result + privateKey.hashCode()
        result = 31 * result + entropy.hashCode()
        return result
    }
}
