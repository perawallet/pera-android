/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.wallet.account.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface TransactionSigner : Parcelable {

    val address: String

    @Parcelize
    data class LedgerBle(
        override val address: String,
        val bluetoothAddress: String,
        val positionInLedger: Int = 0
    ) : TransactionSigner

    @Parcelize
    data class Algo25(override val address: String) : TransactionSigner

    @Parcelize
    data class HdKey(override val address: String) : TransactionSigner

    sealed interface SignerNotFound : TransactionSigner {

        @Parcelize
        data class NoAuth(override val address: String) : SignerNotFound

        @Parcelize
        data class Rekeyed(override val address: String) : SignerNotFound

        @Parcelize
        data class AccountNotFound(override val address: String) : SignerNotFound

        @Parcelize
        data class AuthAccountIsNoAuth(override val address: String) : SignerNotFound

        @Parcelize
        data class AuthAccountSigningDetailsNotFound(
            override val address: String,
            val authAddress: String
        ) : SignerNotFound

        @Parcelize
        data class AuthAddressNotFound(override val address: String) : SignerNotFound
    }
}
