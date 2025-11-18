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

package com.algorand.wallet.banner.data.mapper

import com.algorand.wallet.banner.data.model.BannerDetailResponse
import com.algorand.wallet.banner.data.model.BannerTypeResponse
import com.algorand.wallet.banner.data.model.BannerTypeResponse.CARD
import com.algorand.wallet.banner.data.model.BannerTypeResponse.GENERIC
import com.algorand.wallet.banner.data.model.BannerTypeResponse.GOVERNANCE
import com.algorand.wallet.banner.data.model.BannerTypeResponse.OTHER
import com.algorand.wallet.banner.data.model.BannerTypeResponse.RETAIL
import com.algorand.wallet.banner.data.model.BannerTypeResponse.STAKING
import com.algorand.wallet.banner.domain.model.Banner
import javax.inject.Inject

internal class DefaultBannerMapper @Inject constructor() : BannerMapper {

    override fun map(response: BannerDetailResponse): Banner? {
        return Banner(
            bannerId = response.bannerId ?: return null,
            title = response.title,
            description = response.description,
            buttonTitle = response.buttonText,
            buttonUrl = response.buttonUrl,
            type = getBannerType(response.bannerTypeResponse)
        )
    }

    private fun getBannerType(bannerTypeResponse: BannerTypeResponse?): Banner.BannerType {
        return when (bannerTypeResponse) {
            GENERIC -> Banner.BannerType.Generic
            GOVERNANCE -> Banner.BannerType.Governance
            STAKING -> Banner.BannerType.Staking
            CARD -> Banner.BannerType.Card
            RETAIL -> Banner.BannerType.Retail
            OTHER -> Banner.BannerType.Generic
            null -> Banner.BannerType.Generic
        }
    }
}
