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

package com.algorand.android.module.asset.detail.component.asset.data.mapper.model

import com.algorand.android.module.asset.detail.component.AssetConstants.ALGORAND_DISCORD_URL
import com.algorand.android.module.asset.detail.component.AssetConstants.ALGORAND_TELEGRAM_URL
import com.algorand.android.module.asset.detail.component.AssetConstants.ALGORAND_TWITTER_USERNAME
import com.algorand.android.module.asset.detail.component.AssetConstants.ALGORAND_WEBSITE_URL
import com.algorand.android.module.asset.detail.component.AssetConstants.ALGO_ASSET_ID
import com.algorand.android.module.asset.detail.component.AssetConstants.ALGO_DECIMALS
import com.algorand.android.module.asset.detail.component.AssetConstants.ALGO_FULL_NAME
import com.algorand.android.module.asset.detail.component.AssetConstants.ALGO_SHORT_NAME
import com.algorand.android.module.asset.detail.component.AssetConstants.ALGO_TOTAL_SUPPLY
import com.algorand.android.module.asset.detail.component.asset.domain.model.VerificationTier
import com.algorand.android.module.asset.detail.component.asset.domain.model.detail.Asset
import com.algorand.android.module.asset.detail.component.asset.domain.model.detail.AssetDetail
import javax.inject.Inject

internal class AlgoAssetDetailMapperImpl @Inject constructor() : AlgoAssetDetailMapper {

    override fun invoke(): AssetDetail {
        return AssetDetail(
            id = ALGO_ASSET_ID,
            verificationTier = VerificationTier.TRUSTED,
            assetInfo = Asset.AssetInfo(
                name = Asset.Name(
                    fullName = ALGO_FULL_NAME,
                    shortName = ALGO_SHORT_NAME
                ),
                decimals = ALGO_DECIMALS,
                fiat = null,
                creator = null,
                logo = null,
                explorerUrl = null,
                project = null,
                social = Asset.Social(
                    discordUrl = ALGORAND_DISCORD_URL,
                    telegramUrl = ALGORAND_TELEGRAM_URL,
                    twitterUsername = ALGORAND_TWITTER_USERNAME
                ),
                description = null,
                supply = Asset.Supply(
                    total = ALGO_TOTAL_SUPPLY,
                    max = null
                ),
                url = ALGORAND_WEBSITE_URL,
                isAvailableOnDiscoverMobile = true
            )
        )
    }
}
