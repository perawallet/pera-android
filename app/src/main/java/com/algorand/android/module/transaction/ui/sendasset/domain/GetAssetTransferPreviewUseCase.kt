/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.module.transaction.ui.sendasset.domain

import com.algorand.android.module.account.core.ui.usecase.GetAccountDisplayName
import com.algorand.android.module.account.core.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.module.account.core.ui.usecase.GetAssetName
import com.algorand.android.module.asset.detail.component.AssetConstants.ALGO_ASSET_ID
import com.algorand.android.module.asset.detail.component.asset.domain.model.detail.Asset
import com.algorand.android.module.asset.detail.component.asset.domain.usecase.FetchAsset
import com.algorand.android.module.asset.detail.component.asset.domain.usecase.GetAsset
import com.algorand.android.module.account.core.component.assetdata.usecase.GetAccountBaseOwnedAssetData
import com.algorand.android.parity.domain.usecase.GetSelectedCurrencyDetail
import com.algorand.android.parity.domain.usecase.primary.GetPrimaryCurrencySymbolOrName
import com.algorand.android.module.transaction.component.domain.TransactionConstants.MIN_FEE
import com.algorand.android.module.transaction.ui.sendasset.model.AssetTransferPreview
import com.algorand.android.module.transaction.ui.sendasset.model.SendTransactionPayload
import java.math.BigDecimal.ZERO
import java.math.BigInteger
import javax.inject.Inject
import kotlinx.coroutines.delay

internal class GetAssetTransferPreviewUseCase @Inject constructor(
    private val getSelectedCurrencyDetail: GetSelectedCurrencyDetail,
    private val getPrimaryCurrencySymbolOrName: GetPrimaryCurrencySymbolOrName,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAsset: GetAsset,
    private val fetchAsset: FetchAsset,
    private val getAssetName: GetAssetName,
    private val getAccountBaseOwnedAssetData: GetAccountBaseOwnedAssetData
) : GetAssetTransferPreview {

    override suspend fun invoke(transactionPayload: SendTransactionPayload): AssetTransferPreview {
        val exchangePrice = getSelectedCurrencyDetail()?.getDataOrNull()?.algoToSelectedCurrencyConversionRate ?: ZERO
        return with(transactionPayload) {
            AssetTransferPreview(
                senderAccountDisplayName = getAccountDisplayName(senderAddress),
                accountIconDrawablePreview = getAccountIconDrawablePreview(senderAddress),
                amount = amount,
                isAlgo = assetId == ALGO_ASSET_ID,
                targetUser = targetUser,
                exchangePrice = exchangePrice,
                currencySymbol = getPrimaryCurrencySymbolOrName(),
                fee = MIN_FEE.toLong(),
                note = note?.xnote ?: note?.note,
                isNoteEditable = note?.xnote == null,
                assetDetail = getPreviewAssetDetail(assetId),
                accountBalance = getAccountBaseOwnedAssetData(senderAddress, assetId)?.amount ?: BigInteger.ZERO,
                onSendAssetTransaction = null
            )
        }
    }

    private suspend fun getPreviewAssetDetail(assetId: Long): AssetTransferPreview.AssetDetail {
        val cachedAssetDetail = getAsset(assetId)
        return cachedAssetDetail?.mapToPreviewAssetDetail()
            ?: fetchAsset(assetId).use(
                onSuccess = { it.mapToPreviewAssetDetail() },
                onFailed = { _, _ ->
                    delay(3000L)
                    getPreviewAssetDetail(assetId)
                }
            )
    }

    private fun Asset.mapToPreviewAssetDetail(): AssetTransferPreview.AssetDetail {
        return AssetTransferPreview.AssetDetail(
            assetId = id,
            assetName = getAssetName(assetInfo?.name?.fullName).assetName,
            shortName = getAssetName(assetInfo?.name?.shortName).assetName,
            decimals = getDecimalsOrZero()
        )
    }
}
