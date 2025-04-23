/*
 * Copyright 2025 Pera Wallet, LDA
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

package com.algorand.android.usecase

import com.algorand.android.models.BaseAccountAssetData
import com.algorand.android.modules.accountcore.domain.usecase.GetAccountBaseOwnedAssetData
import com.algorand.android.modules.assetinbox.expresssend.domain.usecase.Arc59ExpressSendUseCase
import com.algorand.android.utils.ALGO_DECIMALS
import com.algorand.android.utils.formatAmount
import javax.inject.Inject

class AssetTransferAmountUseCase @Inject constructor(
    private val transactionTipsUseCase: TransactionTipsUseCase,
    private val getAccountBaseOwnedAssetData: GetAccountBaseOwnedAssetData,
    private val arc59ExpressSendUseCase: Arc59ExpressSendUseCase
) {

    fun shouldShowTransactionTips(): Boolean {
        return transactionTipsUseCase.shouldShowTransactionTips()
    }

    suspend fun getMaximumAmountOfAsset(assetId: Long, senderAddress: String): String {
        val assetDetail = getAccountAssetData(assetId, senderAddress)
        return if (assetDetail?.isAlgo == true) {
            assetDetail.amount.formatAmount(ALGO_DECIMALS)
        } else {
            assetDetail?.formattedAmount.orEmpty()
        }
    }

    private suspend fun getAccountAssetData(assetId: Long, address: String): BaseAccountAssetData.BaseOwnedAssetData? {
        return getAccountBaseOwnedAssetData.invoke(address, assetId)
    }

    fun isExpressSendWarningEnabled(isArc59Transaction: Boolean): Boolean {
        return arc59ExpressSendUseCase.isExpressSendWarningEnabled(isArc59Transaction)
    }
}
