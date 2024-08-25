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

package com.algorand.android.banner.data.mapper

import com.algorand.android.banner.data.model.BannerDetailResponse
import com.algorand.android.banner.data.model.BannerTypeResponse.GENERIC
import com.algorand.android.banner.data.model.BannerTypeResponse.GOVERNANCE
import com.algorand.android.banner.data.model.BannerTypeResponse.OTHER
import com.algorand.android.banner.domain.model.Banner
import javax.inject.Inject

internal class BannerMapperImpl @Inject constructor() : BannerMapper {

    override fun invoke(bannerDetailResponse: BannerDetailResponse): Banner? {
        return when (bannerDetailResponse.bannerTypeResponse) {
            GENERIC -> mapToGenericBanner(bannerDetailResponse)
            GOVERNANCE -> mapToGovernanceBanner(bannerDetailResponse)
            OTHER -> mapToGenericBanner(bannerDetailResponse)
            null -> null
        }
    }

    private fun mapToGovernanceBanner(response: BannerDetailResponse): Banner.Governance? {
        return Banner.Governance(
            bannerId = response.bannerId ?: return null,
            title = response.title,
            description = response.description,
            buttonTitle = response.buttonText,
            buttonUrl = response.buttonUrl
        )
    }

    private fun mapToGenericBanner(response: BannerDetailResponse): Banner.Generic? {
        return Banner.Generic(
            bannerId = response.bannerId ?: return null,
            title = response.title,
            description = response.description,
            buttonTitle = response.buttonText,
            buttonUrl = response.buttonUrl
        )
    }
}
