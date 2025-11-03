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

package com.algorand.wallet.swap.data.mapper

import com.algorand.wallet.swap.data.model.SwapQuoteAssetDetailResponse
import com.algorand.wallet.swap.data.model.TopSwapPairsResponse
import com.algorand.wallet.swap.domain.model.TopSwapPairs
import javax.inject.Inject

internal class DefaultTopSwapPairsMapper @Inject constructor() : TopSwapPairsMapper {

    override fun invoke(response: TopSwapPairsResponse): TopSwapPairs {
        val details = response.results.mapNotNull {
            TopSwapPairs.Detail(
                assetA = getAssetDetail(it.assetA) ?: return@mapNotNull null,
                assetB = getAssetDetail(it.assetB) ?: return@mapNotNull null,
                volumeUsd = it.volume24hUsd ?: return@mapNotNull null
            )
        }.sortedByDescending { it.volumeUsd }
        return TopSwapPairs(details)
    }

    private fun getAssetDetail(response: SwapQuoteAssetDetailResponse?): TopSwapPairs.AssetDetail? {
        if (response == null) return null
        return with(response) {
            TopSwapPairs.AssetDetail(
                id = assetId ?: return null,
                logoUrl = logoUrl,
                shortName = shortName
            )
        }
    }
}
