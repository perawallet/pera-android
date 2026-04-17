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

package com.algorand.android.ui.send.transferamount

import com.algorand.android.modules.accountcore.domain.usecase.GetAccountBaseOwnedAssetData
import com.algorand.android.utils.MIN_BALANCE_TO_KEEP_PER_OPTED_IN_APPS
import com.algorand.android.utils.formatAsAlgoAmount
import com.algorand.android.utils.toAlgoDisplayValue
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_ID
import javax.inject.Inject

class BalanceWarningPreviewUseCase @Inject constructor(
    private val getAccountBaseOwnedAssetData: GetAccountBaseOwnedAssetData,
    private val balanceWarningPreviewMapper: BalanceWarningPreviewMapper
) {
    suspend fun getInitialPreview(accountAddress: String): BalanceWarningPreview {
        val algoData = getAccountBaseOwnedAssetData(accountAddress, ALGO_ID)
        return balanceWarningPreviewMapper.mapTo(
            formattedAlgoAmount = algoData
                ?.formattedAmount
                ?.formatAsAlgoAmount(),
            formattedAlgoPrimaryCurrencyValue = algoData
                ?.getSelectedCurrencyParityValue()
                ?.getFormattedCompactValue(),
            formattedMinBalanceToKeepPerOptedInAsset = getFormattedMinBalanceToKeepPerOptedInAsset()
        )
    }

    private fun getFormattedMinBalanceToKeepPerOptedInAsset(): String {
        return MIN_BALANCE_TO_KEEP_PER_OPTED_IN_APPS
            .toBigInteger()
            .toAlgoDisplayValue()
            .stripTrailingZeros()
            .toPlainString()
    }
}
