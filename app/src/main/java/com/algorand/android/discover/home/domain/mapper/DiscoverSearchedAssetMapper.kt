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

package com.algorand.android.discover.home.domain.mapper

import com.algorand.android.assetsearch.domain.mapper.VerificationTierDecider
import com.algorand.android.assetsearch.domain.model.AssetSearchDTO
import com.algorand.android.assetsearch.domain.model.BaseSearchedAsset.DiscoverSearchedAsset
import com.algorand.android.module.currency.domain.model.Currency
import com.algorand.android.utils.formatAsCurrency
import java.math.BigDecimal
import javax.inject.Inject

class DiscoverSearchedAssetMapper @Inject constructor(
    private val verificationTierDecider: VerificationTierDecider
) {

    fun mapToDiscoverSearchedAsset(assetDetailDTO: AssetSearchDTO): DiscoverSearchedAsset {
        with(assetDetailDTO) {
            return DiscoverSearchedAsset(
                assetId = assetId,
                fullName = fullName,
                shortName = shortName,
                logo = logo,
                verificationTier = verificationTierDecider.decideVerificationTier(verificationTier),
                formattedUsdValue = usdValue?.let {
                    BigDecimal(it).formatAsCurrency(symbol = Currency.USD.symbol)
                }
            )
        }
    }
}
