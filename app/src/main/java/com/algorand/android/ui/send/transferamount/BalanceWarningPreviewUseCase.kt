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

package com.algorand.android.ui.send.transferamount

import com.algorand.android.assetdetail.component.AssetConstants.ALGO_ASSET_ID
import com.algorand.android.module.account.core.component.assetdata.usecase.GetAccountBaseOwnedAssetData
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData
import com.algorand.android.module.account.core.component.utils.BalanceConstants.MIN_BALANCE_TO_KEEP_PER_OPTED_IN_APPS
import com.algorand.android.utils.toAlgoDisplayValue
import javax.inject.Inject

class BalanceWarningPreviewUseCase @Inject constructor(
    private val getAccountBaseOwnedAssetData: GetAccountBaseOwnedAssetData,
    private val balanceWarningPreviewMapper: BalanceWarningPreviewMapper
) {
    suspend fun getInitialPreview(accountAddress: String): BalanceWarningPreview {
        return balanceWarningPreviewMapper.mapTo(
            formattedAlgoAmount = getFormattedAlgoAmount(accountAddress),
            formattedAlgoPrimaryCurrencyValue = getFormattedAlgoPrimaryCurrencyValue(accountAddress),
            formattedMinBalanceToKeepPerOptedInAsset = getFormattedMinBalanceToKeepPerOptedInAsset()
        )
    }

    private suspend fun getFormattedAlgoAmount(accountAddress: String): String? {
        return getAlgoData(accountAddress)?.formattedAmount
    }

    private suspend fun getFormattedAlgoPrimaryCurrencyValue(accountAddress: String): String? {
        return getAlgoData(accountAddress)?.getSelectedCurrencyParityValue()?.getFormattedCompactValue()
    }

    private fun getFormattedMinBalanceToKeepPerOptedInAsset(): String {
        return MIN_BALANCE_TO_KEEP_PER_OPTED_IN_APPS
            .toBigInteger()
            .toAlgoDisplayValue()
            .stripTrailingZeros()
            .toPlainString()
    }

    private suspend fun getAlgoData(accountAddress: String): BaseAccountAssetData.BaseOwnedAssetData? {
        return getAccountBaseOwnedAssetData(accountAddress, ALGO_ASSET_ID)
    }
}
