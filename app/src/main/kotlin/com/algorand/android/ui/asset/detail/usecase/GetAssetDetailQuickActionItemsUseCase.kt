/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.ui.asset.detail.usecase

import com.algorand.android.ui.asset.detail.model.AssetDetailQuickActionItem
import com.algorand.android.ui.asset.detail.model.AssetDetailQuickActionItem.BuyAlgoButton
import com.algorand.android.ui.asset.detail.model.AssetDetailQuickActionItem.ReceiveButton
import com.algorand.android.ui.asset.detail.model.AssetDetailQuickActionItem.SendButton
import com.algorand.android.ui.asset.detail.model.AssetDetailQuickActionItem.StakeButton
import com.algorand.android.ui.asset.detail.model.AssetDetailQuickActionItem.SwapButton
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.account.detail.domain.usecase.GetAccountType
import com.algorand.wallet.account.info.domain.usecase.IsAssetOptedInByAccount
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_ID
import com.algorand.wallet.remoteconfig.domain.model.FeatureToggle
import com.algorand.wallet.remoteconfig.domain.usecase.IsFeatureToggleEnabled
import javax.inject.Inject

internal class GetAssetDetailQuickActionItemsUseCase @Inject constructor(
    private val getAccountType: GetAccountType,
    private val isAssetOptedInByAccount: IsAssetOptedInByAccount,
    private val isFeatureToggleEnabled: IsFeatureToggleEnabled
) : GetAssetDetailQuickActionItems {

    override suspend fun invoke(address: String, assetId: Long): List<AssetDetailQuickActionItem> {
        val isWatchAccount = getAccountType(address) == AccountType.NoAuth
        if (isWatchAccount) return emptyList()
        val isAlgo = assetId == ALGO_ID
        return buildList {
            if (isAssetOptedInByAccount(address, assetId)) {
                add(SwapButton)
            }

            if (isAlgo) {
                if (isFeatureToggleEnabled(FeatureToggle.XO_SWAP.key)) {
                    add(StakeButton)
                } else {
                    add(BuyAlgoButton)
                }
            }
            add(SendButton)
            add(ReceiveButton)
        }
    }
}
