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

package com.algorand.common.asset.data.mapper.model

import com.algorand.common.asset.domain.util.AssetConstants
import com.algorand.common.asset.domain.util.AssetConstants.ALGORAND_DISCORD_URL
import com.algorand.common.asset.domain.util.AssetConstants.ALGORAND_TELEGRAM_URL
import com.algorand.common.asset.domain.util.AssetConstants.ALGORAND_TWITTER_USERNAME
import com.algorand.common.asset.domain.util.AssetConstants.ALGORAND_WEBSITE_URL
import com.algorand.common.asset.domain.util.AssetConstants.ALGO_DECIMALS
import com.algorand.common.asset.domain.util.AssetConstants.ALGO_FULL_NAME
import com.algorand.common.asset.domain.util.AssetConstants.ALGO_SHORT_NAME
import com.algorand.common.asset.domain.util.AssetConstants.ALGO_TOTAL_SUPPLY
import com.algorand.common.asset.domain.model.Asset
import com.algorand.common.asset.domain.model.AssetDetail
import com.algorand.common.asset.domain.model.VerificationTier

internal class AlgoAssetDetailMapperImpl : AlgoAssetDetailMapper {

    override fun invoke(): AssetDetail {
        return AssetDetail(
            id = AssetConstants.ALGO_ID,
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
