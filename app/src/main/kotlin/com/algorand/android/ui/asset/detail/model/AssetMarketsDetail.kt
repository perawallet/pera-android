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

package com.algorand.android.ui.asset.detail.model

import com.algorand.android.ui.common.amount.AmountRenderer

sealed interface AssetMarketsDetail {

    data class Statistics(
        val price: AmountRenderer,
        val totalSupply: String?
    ) : AssetMarketsDetail

    data class About(
        val assetName: String?,
        val assetId: Long?,
        val assetCreatorAddress: String?,
        val asaUrl: String?,
        val displayAsaUrl: String?,
        val peraExplorerUrl: String?,
        val projectWebsiteUrl: String?
    ) : AssetMarketsDetail

    data class BadgeDescription(
        val backgroundColorResId: Int,
        val textColorResId: Int,
        val drawableResId: Int,
        val titleTextResId: Int,
        val descriptionTextResId: Int
    ) : AssetMarketsDetail

    sealed interface AssetDescription : AssetMarketsDetail {
        data class Text(val text: String) : AssetDescription
        data class TextResource(val textResId: Int) : AssetDescription
    }

    data class SocialMedia(
        val discordUrl: String?,
        val telegramUrl: String?,
        val twitterUrl: String?
    ) : AssetMarketsDetail

    data class Report(
        val assetName: String?,
        val assetId: Long
    ) : AssetMarketsDetail
}
