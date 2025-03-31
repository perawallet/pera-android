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

package com.algorand.android.mapper

import com.algorand.android.models.WalletConnectTransactionAssetDetail
import com.algorand.android.modules.verificationtier.ui.decider.VerificationTierConfigurationDecider
import com.algorand.wallet.asset.domain.model.Asset
import javax.inject.Inject

class WalletConnectTransactionAssetDetailMapper @Inject constructor(
    private val verificationTierConfigurationDecider: VerificationTierConfigurationDecider
) {

    fun map(
        asset: Asset
    ): WalletConnectTransactionAssetDetail {
        return with(asset) {
            WalletConnectTransactionAssetDetail(
                assetId = id,
                fullName = fullName,
                shortName = shortName,
                fractionDecimals = getDecimalsOrZero(),
                verificationTier = verificationTierConfigurationDecider
                    .decideVerificationTierConfiguration(verificationTier)
            )
        }
    }
}
