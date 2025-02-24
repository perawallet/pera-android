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

package com.algorand.wallet.account.detail.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface AccountType: Parcelable {

    @Parcelize
    data object Algo25 : AccountType

    @Parcelize
    data object LedgerBle : AccountType

    @Parcelize
    data object Rekeyed : AccountType

    @Parcelize
    data object RekeyedAuth : AccountType

    @Parcelize
    data object NoAuth : AccountType

    @Parcelize
    data object HdKey : AccountType

    companion object {
        fun AccountType.canSignTransaction(): Boolean {
            return this is Algo25 || this is HdKey || this is LedgerBle || this is RekeyedAuth
        }
        fun AccountType.wordCount(): Int {
            return when (this) {
                is Algo25 -> 25
                is HdKey -> 24
                else -> 0
            }
        }
    }
}
