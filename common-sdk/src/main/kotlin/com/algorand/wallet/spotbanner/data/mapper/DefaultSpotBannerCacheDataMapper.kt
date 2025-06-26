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

package com.algorand.wallet.spotbanner.data.mapper

import com.algorand.wallet.spotbanner.data.model.SpotBannerCacheData
import com.algorand.wallet.spotbanner.data.model.SpotBannerResponse
import javax.inject.Inject

internal class DefaultSpotBannerCacheDataMapper @Inject constructor() : SpotBannerCacheDataMapper {

    override fun map(response: SpotBannerResponse): SpotBannerCacheData? {
        return SpotBannerCacheData(
            id = response.id ?: return null,
            text = response.text ?: return null,
            image = response.image,
            url = response.url,
            isExternalButtonUrl = response.isExternalButtonUrl ?: true
        )
    }
}
