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

package com.algorand.common.account.local.domain.model

sealed interface LocalAccount {

    val address: String

    data class Bip39(
        override val address: String,
        val secretKey: ByteArray
    ) : LocalAccount {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Algo25

            if (address != other.address) return false
            if (!secretKey.contentEquals(other.secretKey)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = address.hashCode()
            result = 31 * result + secretKey.contentHashCode()
            return result
        }
    }

    data class Algo25(
        override val address: String,
        val secretKey: ByteArray
    ) : LocalAccount {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Algo25

            if (address != other.address) return false
            if (!secretKey.contentEquals(other.secretKey)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = address.hashCode()
            result = 31 * result + secretKey.contentHashCode()
            return result
        }
    }

    data class LedgerBle(
        override val address: String,
        val deviceMacAddress: String,
        val indexInLedger: Int
    ) : LocalAccount

    data class NoAuth(
        override val address: String
    ) : LocalAccount
}
