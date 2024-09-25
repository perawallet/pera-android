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

package com.algorand.android.banner.ui.mapper

import com.algorand.android.module.banner.domain.model.Banner
import com.algorand.android.modules.accounts.domain.model.BaseAccountListItem
import javax.inject.Inject

class BaseBannerItemMapper @Inject constructor() {

    fun mapToGovernanceBannerItem(
        governanceBanner: Banner.Governance,
        isButtonVisible: Boolean,
        isTitleVisible: Boolean,
        isDescriptionVisible: Boolean
    ): BaseAccountListItem.BaseBannerItem.GovernanceBannerItem {
        return BaseAccountListItem.BaseBannerItem.GovernanceBannerItem(
            bannerId = governanceBanner.bannerId,
            title = governanceBanner.title,
            description = governanceBanner.description,
            buttonText = governanceBanner.buttonTitle,
            buttonUrl = governanceBanner.buttonUrl,
            isButtonVisible = isButtonVisible,
            isTitleVisible = isTitleVisible,
            isDescriptionVisible = isDescriptionVisible
        )
    }

    fun mapToGenericBannerItem(
        genericBanner: Banner.Generic,
        isButtonVisible: Boolean,
        isTitleVisible: Boolean,
        isDescriptionVisible: Boolean
    ): BaseAccountListItem.BaseBannerItem.GenericBannerItem {
        return BaseAccountListItem.BaseBannerItem.GenericBannerItem(
            bannerId = genericBanner.bannerId,
            title = genericBanner.title,
            description = genericBanner.description,
            buttonText = genericBanner.buttonTitle,
            buttonUrl = genericBanner.buttonUrl,
            isButtonVisible = isButtonVisible,
            isTitleVisible = isTitleVisible,
            isDescriptionVisible = isDescriptionVisible
        )
    }
}
