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

package com.algorand.android.mapper

import com.algorand.android.models.AssetTransferPreview
import com.algorand.android.models.TransactionSignData
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.wallet.account.detail.domain.model.AccountDetail
import java.math.BigDecimal
import java.math.BigInteger
import javax.inject.Inject

class AssetTransferPreviewMapper @Inject constructor() {

    @Suppress("LongParameterList")
    fun mapToAssetTransferPreview(
        transactionData: TransactionSignData.Send,
        exchangePrice: BigDecimal,
        currencySymbol: String,
        assetId: Long,
        assetShortName: String,
        assetDecimals: Int,
        note: String?,
        isNoteEditable: Boolean,
        accountIconDrawablePreview: AccountIconDrawablePreview,
        senderAssetAmount: BigInteger,
        fee: Long,
        targetAccountDetail: AccountDetail
    ): AssetTransferPreview {
        with(transactionData) {
            return AssetTransferPreview(
                amount = amount,
                targetUser = targetUser,
                exchangePrice = exchangePrice,
                currencySymbol = currencySymbol,
                fee = fee,
                note = note,
                isNoteEditable = isNoteEditable,
                accountIconDrawablePreview = accountIconDrawablePreview,
                senderAccountName = senderAccountName,
                senderAccountAddress = senderAccountAddress,
                targetAccountDetail = targetAccountDetail,
                senderAssetAmount = senderAssetAmount,
                assetId = assetId,
                assetShortName = assetShortName,
                assetDecimals = assetDecimals
            )
        }
    }
}
