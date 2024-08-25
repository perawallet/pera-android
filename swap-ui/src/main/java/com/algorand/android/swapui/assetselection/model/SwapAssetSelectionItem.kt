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

package com.algorand.android.swapui.assetselection.model

import com.algorand.android.accountcore.ui.model.AssetName
import com.algorand.android.accountcore.ui.model.VerificationTierConfiguration
import com.algorand.android.designsystem.RecyclerListItem
import com.algorand.android.drawableui.asset.BaseAssetDrawableProvider

data class SwapAssetSelectionItem(
    val assetId: Long,
    val assetFullName: AssetName,
    val assetShortName: AssetName,
    val formattedPrimaryValue: String,
    val formattedSecondaryValue: String,
    val arePrimaryAndSecondaryValueVisible: Boolean,
    val verificationTier: VerificationTierConfiguration,
    val assetDrawableProvider: BaseAssetDrawableProvider
) : RecyclerListItem {

    override fun areItemsTheSame(other: RecyclerListItem): Boolean {
        return other is SwapAssetSelectionItem && assetId == other.assetId
    }

    override fun areContentsTheSame(other: RecyclerListItem): Boolean {
        return other is SwapAssetSelectionItem && this == other
    }
}
