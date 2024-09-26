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

package com.algorand.android.mapper

import com.algorand.android.module.account.core.ui.asset.assetdrawable.GetAssetDrawableProvider
import com.algorand.android.module.account.core.ui.mapper.VerificationTierConfigurationMapper
import com.algorand.android.module.account.core.ui.model.AssetName
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.OwnedAssetData
import com.algorand.android.models.LedgerInformationListItem
import javax.inject.Inject

class LedgerInformationAssetItemMapper @Inject constructor(
    private val verificationTierConfigurationMapper: VerificationTierConfigurationMapper,
    private val getAssetDrawableProvider: GetAssetDrawableProvider
) {

    suspend fun mapTo(
        accountAssetData: OwnedAssetData,
        assetName: AssetName,
        assetShortName: AssetName
    ): LedgerInformationListItem.AssetInformationItem {
        return LedgerInformationListItem.AssetInformationItem(
            id = accountAssetData.id,
            name = assetName,
            shortName = assetShortName,
            isAmountInDisplayedCurrencyVisible = accountAssetData.isAmountInSelectedCurrencyVisible,
            verificationTierConfiguration = verificationTierConfigurationMapper(accountAssetData.verificationTier),
            baseAssetDrawableProvider = getAssetDrawableProvider(accountAssetData.id),
            formattedDisplayedCurrencyValue = accountAssetData.getSelectedCurrencyParityValue().getFormattedValue(),
            formattedAmount = accountAssetData.formattedAmount
        )
    }
}
