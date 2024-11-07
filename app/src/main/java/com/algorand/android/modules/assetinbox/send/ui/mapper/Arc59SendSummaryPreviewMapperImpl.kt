/*
 *  Copyright 2022 Pera Wallet, LDA
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.algorand.android.modules.assetinbox.send.ui.mapper

import android.content.res.Resources
import com.algorand.android.models.BaseAssetDetail
import com.algorand.android.modules.assetinbox.send.domain.model.Arc59SendSummary
import com.algorand.android.modules.assetinbox.send.domain.model.Arc59SendTransaction
import com.algorand.android.modules.assetinbox.send.ui.model.Arc59SendSummaryPreview
import com.algorand.android.modules.currency.domain.model.Currency
import com.algorand.android.utils.AssetName
import com.algorand.android.utils.ErrorResource
import com.algorand.android.utils.Event
import com.algorand.android.utils.formatAmount
import com.algorand.android.utils.formatAsCurrency
import com.algorand.android.utils.toAlgoDisplayValue
import java.math.BigInteger
import javax.inject.Inject

class Arc59SendSummaryPreviewMapperImpl @Inject constructor(
    private val resources: Resources
) : Arc59SendSummaryPreviewMapper {

    override fun invoke(
        summary: Arc59SendSummary,
        amount: BigInteger,
        assetDetail: BaseAssetDetail,
        isLoading: Boolean,
        showError: Event<ErrorResource>?,
        onNavBack: Event<Unit>?,
        arc59Transactions: Event<List<Arc59SendTransaction>>?
    ): Arc59SendSummaryPreview {
        return Arc59SendSummaryPreview(
            assetName = assetDetail.fullName.orEmpty(),
            formattedAssetAmount = getFormattedAssetAmount(amount, assetDetail),
            formattedMinBalanceFee = getFormattedProtocolAndMbrFee(summary.totalProtocolAndMbrFee),
            summary = summary,
            isLoading = isLoading,
            showError = showError,
            onNavBack = onNavBack,
            onTxnSendSuccessfully = null,
            arc59Transactions = arc59Transactions
        )
    }

    override fun getInitialPreview(): Arc59SendSummaryPreview {
        return Arc59SendSummaryPreview(
            assetName = "",
            formattedAssetAmount = "",
            formattedMinBalanceFee = "",
            summary = null,
            isLoading = true,
            showError = null,
            onNavBack = null,
            onTxnSendSuccessfully = null,
            arc59Transactions = null
        )
    }

    private fun getFormattedAssetAmount(amount: BigInteger, assetDetail: BaseAssetDetail): String {
        val safeDecimals = assetDetail.fractionDecimals ?: 0
        val safeShortName = AssetName.createShortName(assetDetail.shortName).getName(resources)
        val formattedAmount = amount.formatAmount(safeDecimals)
        return "$formattedAmount $safeShortName"
    }

    private fun getFormattedProtocolAndMbrFee(totalProtocolAndMbrFee: BigInteger): String {
        return totalProtocolAndMbrFee.toAlgoDisplayValue().formatAsCurrency(Currency.ALGO.symbol)
    }
}
