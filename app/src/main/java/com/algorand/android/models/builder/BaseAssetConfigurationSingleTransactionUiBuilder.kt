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

import com.algorand.android.R
import com.algorand.android.accountcore.ui.mapper.VerificationTierConfigurationMapper
import com.algorand.android.models.BaseAssetConfigurationTransaction
import com.algorand.android.models.WalletConnectTransactionAmount
import com.algorand.android.models.WalletConnectTransactionShortDetail
import com.algorand.android.utils.AssetName
import javax.inject.Inject

class BaseAssetConfigurationSingleTransactionUiBuilder @Inject constructor(
    private val verificationTierConfigurationMapper: VerificationTierConfigurationMapper
) : WalletConnectSingleTransactionUiBuilder<BaseAssetConfigurationTransaction> {

    override fun buildToolbarTitleRes(txn: BaseAssetConfigurationTransaction): Int {
        return when (txn) {
            is BaseAssetConfigurationTransaction.BaseAssetCreationTransaction -> {
                R.string.asset_creation_request
            }
            is BaseAssetConfigurationTransaction.BaseAssetDeletionTransaction -> {
                R.string.asset_deletion_request
            }
            is BaseAssetConfigurationTransaction.BaseAssetReconfigurationTransaction -> {
                R.string.asset_reconfiguration_request
            }
        }
    }

    override fun buildTransactionShortDetail(
        txn: BaseAssetConfigurationTransaction
    ): WalletConnectTransactionShortDetail {
        return with(txn) {
            WalletConnectTransactionShortDetail(
                accountIconDrawablePreview = getFromAccountIconResource(),
                accountName = fromAccount?.name,
                warningCount = warningCount,
                decimal = assetDecimal,
                fee = fee
            )
        }
    }

    override fun buildTransactionAmount(txn: BaseAssetConfigurationTransaction): WalletConnectTransactionAmount {
        return when (txn) {
            is BaseAssetConfigurationTransaction.BaseAssetCreationTransaction -> {
                buildAssetCreationTransactionAmount(txn)
            }
            else -> buildGeneralAmountInfo(txn)
        }
    }

    private fun buildAssetCreationTransactionAmount(
        baseAssetCreationTransaction: BaseAssetConfigurationTransaction.BaseAssetCreationTransaction
    ): WalletConnectTransactionAmount {
        return with(baseAssetCreationTransaction) {
            val assetName = assetName.takeIf { assetName != null }
            WalletConnectTransactionAmount(
                assetId = assetId,
                assetName = AssetName.create(assetName),
                isAssetUnnamed = assetName == null,
                verificationTierConfiguration = verificationTierConfigurationMapper(verificationTier)
            )
        }
    }

    private fun buildGeneralAmountInfo(txn: BaseAssetConfigurationTransaction): WalletConnectTransactionAmount {
        return with(txn) {
            WalletConnectTransactionAmount(
                assetId = assetId,
                assetName = AssetName.create(assetName),
                verificationTierConfiguration = verificationTierConfigurationMapper(verificationTier)
            )
        }
    }
}
