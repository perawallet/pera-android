/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.accounts.lite.domain.model

import com.algorand.android.modules.parity.domain.model.AlgoAmountValue
import com.algorand.wallet.account.detail.domain.model.AccountRegistrationType
import com.algorand.wallet.account.detail.domain.model.AccountType
import java.math.BigDecimal
import java.math.BigInteger

data class AccountLite(
    val address: String,
    val customName: String,
    val isBackedUp: Boolean,
    val cachedInfo: CachedInfo?,
    val sortIndex: Int,
    val registrationType: AccountRegistrationType
) {

    data class CachedInfo(
        val type: AccountType,
        val algoAmountValue: AlgoAmountValue,
        val primaryAccountValue: BigDecimal,
        val secondaryAccountValue: BigDecimal,
        val assetCount: Int,
        val minRequiredBalance: BigInteger,
        val rekeyAuthAddress: String?
    ) {

        val isRekeyed
            get(): Boolean = !rekeyAuthAddress.isNullOrEmpty()

        fun getPrimaryAccountValueWithoutAlgo(): BigDecimal {
            return primaryAccountValue - algoAmountValue.parityValueInSelectedCurrency.amountAsCurrency
        }

        fun getSecondaryAccountValueWithoutAlgo(): BigDecimal {
            return secondaryAccountValue - algoAmountValue.parityValueInSecondaryCurrency.amountAsCurrency
        }
    }
}
