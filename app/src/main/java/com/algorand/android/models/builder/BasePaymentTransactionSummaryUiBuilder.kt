/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License
 *
 */

package com.algorand.android.models.builder

import com.algorand.android.models.BasePaymentTransaction
import com.algorand.android.models.WalletConnectTransactionSummary
import com.algorand.android.utils.ALGO_DECIMALS
import com.algorand.android.utils.ALGO_SHORT_NAME
import javax.inject.Inject

class BasePaymentTransactionSummaryUiBuilder @Inject constructor() :
    WalletConnectTransactionSummaryUIBuilder<BasePaymentTransaction> {

    override fun buildTransactionSummary(txn: BasePaymentTransaction): WalletConnectTransactionSummary {
        return with(txn) {
            WalletConnectTransactionSummary(
                accountName = fromAccount?.name,
                accountIconDrawablePreview = getFromAccountIconResource(),
                accountBalance = assetInformation?.amount,
                assetShortName = ALGO_SHORT_NAME,
                assetDecimal = ALGO_DECIMALS,
                transactionAmount = transactionAmount,
                showWarning = warningCount != null,
                formattedSelectedCurrencyValue = assetInformation?.formattedSelectedCurrencyValue
            )
        }
    }
}
