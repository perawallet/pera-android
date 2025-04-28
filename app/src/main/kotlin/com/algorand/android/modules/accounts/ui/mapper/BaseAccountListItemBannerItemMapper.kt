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

package com.algorand.android.modules.accounts.ui.mapper

import com.algorand.android.banner.domain.model.BaseBanner
import com.algorand.android.modules.accounts.ui.model.BaseAccountListItem
import javax.inject.Inject

class BaseAccountListItemBannerItemMapper @Inject constructor() {

    fun map(baseBanner: BaseBanner?): BaseAccountListItem.BannerItem? {
        return baseBanner?.let { banner ->
            val isButtonVisible = !banner.buttonTitle.isNullOrBlank() && !banner.buttonUrl.isNullOrBlank()
            val isTitleVisible = !banner.title.isNullOrBlank()
            val isDescriptionVisible = !banner.description.isNullOrBlank()

            val bannerType = when (banner) {
                is BaseBanner.GovernanceBanner -> BaseAccountListItem.BannerItem.BannerType.Governance
                is BaseBanner.StakingBanner -> BaseAccountListItem.BannerItem.BannerType.Staking
                is BaseBanner.CardBanner -> BaseAccountListItem.BannerItem.BannerType.Card
                is BaseBanner.GenericBanner -> BaseAccountListItem.BannerItem.BannerType.Generic
            }
            BaseAccountListItem.BannerItem(
                bannerId = banner.bannerId,
                title = banner.title,
                description = banner.description,
                buttonText = banner.buttonTitle,
                buttonUrl = banner.buttonUrl,
                isButtonVisible = isButtonVisible,
                isTitleVisible = isTitleVisible,
                isDescriptionVisible = isDescriptionVisible,
                type = bannerType
            )
        }
    }
}
