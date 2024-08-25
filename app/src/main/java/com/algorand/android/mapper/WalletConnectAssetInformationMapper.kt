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

import com.algorand.android.core.component.domain.model.BaseAccountAssetData
import com.algorand.android.models.WalletConnectAssetInformation
import com.algorand.android.utils.formatAsCurrency
import java.math.BigDecimal
import javax.inject.Inject

class WalletConnectAssetInformationMapper @Inject constructor() {

    fun mapToWalletConnectAssetInformation(
        ownedAsset: BaseAccountAssetData.BaseOwnedAssetData,
        safeAmount: BigDecimal
    ): WalletConnectAssetInformation {
        return WalletConnectAssetInformation(
            assetId = ownedAsset.id,
            shortName = ownedAsset.shortName,
            fullName = ownedAsset.name,
            decimal = ownedAsset.decimals,
            amount = ownedAsset.amount,
            formattedSelectedCurrencyValue = safeAmount.formatAsCurrency(
                symbol = ownedAsset.getSelectedCurrencyParityValue().selectedCurrencySymbol
            ),
            verificationTier = ownedAsset.verificationTier
        )
    }
}
