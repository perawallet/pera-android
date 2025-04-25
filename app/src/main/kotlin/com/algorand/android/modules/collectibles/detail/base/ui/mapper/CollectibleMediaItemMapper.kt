/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.collectibles.detail.base.ui.mapper

import com.algorand.android.decider.AssetDrawableProviderDecider
import com.algorand.android.modules.collectibles.detail.base.ui.model.BaseCollectibleMediaItem
import com.algorand.wallet.asset.domain.model.BaseCollectibleMedia
import com.algorand.wallet.asset.domain.model.CollectibleDetail
import javax.inject.Inject

class CollectibleMediaItemMapper @Inject constructor(
    private val assetDrawableProviderDecider: AssetDrawableProviderDecider
) {

    @SuppressWarnings("LongMethod")
    fun mapToCollectibleMediaItem(
        baseCollectibleMedia: BaseCollectibleMedia,
        shouldDecreaseOpacity: Boolean,
        collectibleDetail: CollectibleDetail,
        showMediaButtons: Boolean
    ): BaseCollectibleMediaItem {
        return when (baseCollectibleMedia) {
            is BaseCollectibleMedia.GifCollectibleMedia -> {
                mapToGifCollectibleMediaItem(
                    collectibleId = collectibleDetail.id,
                    shouldDecreaseOpacity = shouldDecreaseOpacity,
                    collectibleMedia = baseCollectibleMedia,
                    collectibleDetail = collectibleDetail,
                    has3dSupport = showMediaButtons,
                    hasFullScreenSupport = showMediaButtons
                )
            }

            is BaseCollectibleMedia.AudioCollectibleMedia -> {
                mapToAudioCollectibleMediaItem(
                    collectibleId = collectibleDetail.id,
                    isOwnedByTheUser = shouldDecreaseOpacity,
                    collectibleMedia = baseCollectibleMedia,
                    collectibleDetail = collectibleDetail,
                    hasFullScreenSupport = showMediaButtons,
                    showPlayButton = showMediaButtons
                )
            }

            is BaseCollectibleMedia.ImageCollectibleMedia -> {
                mapToImageCollectibleMediaItem(
                    collectibleId = collectibleDetail.id,
                    shouldDecreaseOpacity = shouldDecreaseOpacity,
                    collectibleMedia = baseCollectibleMedia,
                    collectibleDetail = collectibleDetail,
                    has3dSupport = showMediaButtons,
                    hasFullScreenSupport = showMediaButtons
                )
            }

            is BaseCollectibleMedia.NoMediaCollectibleMedia -> {
                mapToNoMediaCollectibleMediaItem(
                    collectibleId = collectibleDetail.id,
                    isOwnedByTheUser = shouldDecreaseOpacity,
                    collectibleMedia = baseCollectibleMedia,
                    collectibleDetail = collectibleDetail
                )
            }

            is BaseCollectibleMedia.UnsupportedCollectibleMedia -> {
                mapToUnsupportedCollectibleMediaItem(
                    collectibleId = collectibleDetail.id,
                    isOwnedByTheUser = shouldDecreaseOpacity,
                    collectibleMedia = baseCollectibleMedia,
                    collectibleDetail = collectibleDetail
                )
            }

            is BaseCollectibleMedia.VideoCollectibleMedia -> {
                mapToVideoCollectibleMediaItem(
                    collectibleId = collectibleDetail.id,
                    isOwnedByTheUser = shouldDecreaseOpacity,
                    collectibleMedia = baseCollectibleMedia,
                    collectibleDetail = collectibleDetail,
                    hasFullScreenSupport = showMediaButtons,
                    showPlayButton = showMediaButtons
                )
            }
        }
    }

    private fun mapToImageCollectibleMediaItem(
        collectibleId: Long,
        shouldDecreaseOpacity: Boolean,
        collectibleMedia: BaseCollectibleMedia,
        collectibleDetail: CollectibleDetail,
        has3dSupport: Boolean,
        hasFullScreenSupport: Boolean,
    ): BaseCollectibleMediaItem.ImageCollectibleMediaItem {
        return BaseCollectibleMediaItem.ImageCollectibleMediaItem(
            downloadUrl = collectibleMedia.downloadUrl,
            previewUrl = collectibleMedia.previewUrl,
            mediaExtension = collectibleMedia.mediaExtension,
            collectibleId = collectibleId,
            shouldDecreaseOpacity = shouldDecreaseOpacity,
            baseAssetDrawableProvider = assetDrawableProviderDecider.getAssetDrawableProvider(collectibleDetail),
            has3dSupport = has3dSupport,
            hasFullScreenSupport = hasFullScreenSupport,
            showPlayButton = false
        )
    }

    private fun mapToGifCollectibleMediaItem(
        collectibleId: Long,
        shouldDecreaseOpacity: Boolean,
        collectibleMedia: BaseCollectibleMedia,
        collectibleDetail: CollectibleDetail,
        has3dSupport: Boolean,
        hasFullScreenSupport: Boolean,
    ): BaseCollectibleMediaItem.GifCollectibleMediaItem {
        return BaseCollectibleMediaItem.GifCollectibleMediaItem(
            downloadUrl = collectibleMedia.downloadUrl,
            previewUrl = collectibleMedia.previewUrl,
            mediaExtension = collectibleMedia.mediaExtension,
            collectibleId = collectibleId,
            shouldDecreaseOpacity = shouldDecreaseOpacity,
            baseAssetDrawableProvider = assetDrawableProviderDecider.getAssetDrawableProvider(collectibleDetail),
            has3dSupport = has3dSupport,
            hasFullScreenSupport = hasFullScreenSupport,
            showPlayButton = false
        )
    }

    private fun mapToVideoCollectibleMediaItem(
        collectibleId: Long,
        isOwnedByTheUser: Boolean,
        collectibleMedia: BaseCollectibleMedia,
        collectibleDetail: CollectibleDetail,
        hasFullScreenSupport: Boolean,
        showPlayButton: Boolean
    ): BaseCollectibleMediaItem.VideoCollectibleMediaItem {
        return BaseCollectibleMediaItem.VideoCollectibleMediaItem(
            downloadUrl = collectibleMedia.downloadUrl,
            previewUrl = collectibleMedia.previewUrl,
            mediaExtension = collectibleMedia.mediaExtension,
            collectibleId = collectibleId,
            shouldDecreaseOpacity = isOwnedByTheUser,
            baseAssetDrawableProvider = assetDrawableProviderDecider.getAssetDrawableProvider(collectibleDetail),
            has3dSupport = false,
            hasFullScreenSupport = hasFullScreenSupport,
            showPlayButton = showPlayButton
        )
    }

    private fun mapToAudioCollectibleMediaItem(
        collectibleId: Long,
        isOwnedByTheUser: Boolean,
        collectibleMedia: BaseCollectibleMedia,
        collectibleDetail: CollectibleDetail,
        hasFullScreenSupport: Boolean,
        showPlayButton: Boolean
    ): BaseCollectibleMediaItem.AudioCollectibleMediaItem {
        return BaseCollectibleMediaItem.AudioCollectibleMediaItem(
            downloadUrl = collectibleMedia.downloadUrl,
            previewUrl = collectibleMedia.previewUrl,
            mediaExtension = collectibleMedia.mediaExtension,
            collectibleId = collectibleId,
            shouldDecreaseOpacity = isOwnedByTheUser,
            baseAssetDrawableProvider = assetDrawableProviderDecider.getAssetDrawableProvider(collectibleDetail),
            has3dSupport = false,
            hasFullScreenSupport = hasFullScreenSupport,
            showPlayButton = showPlayButton
        )
    }

    private fun mapToUnsupportedCollectibleMediaItem(
        collectibleId: Long,
        isOwnedByTheUser: Boolean,
        collectibleMedia: BaseCollectibleMedia,
        collectibleDetail: CollectibleDetail
    ): BaseCollectibleMediaItem.UnsupportedCollectibleMediaItem {
        return BaseCollectibleMediaItem.UnsupportedCollectibleMediaItem(
            downloadUrl = collectibleMedia.downloadUrl,
            previewUrl = collectibleMedia.previewUrl,
            mediaExtension = collectibleMedia.mediaExtension,
            collectibleId = collectibleId,
            shouldDecreaseOpacity = isOwnedByTheUser,
            baseAssetDrawableProvider = assetDrawableProviderDecider.getAssetDrawableProvider(collectibleDetail),
            has3dSupport = false,
            hasFullScreenSupport = false,
            showPlayButton = false
        )
    }

    private fun mapToNoMediaCollectibleMediaItem(
        collectibleId: Long,
        isOwnedByTheUser: Boolean,
        collectibleMedia: BaseCollectibleMedia,
        collectibleDetail: CollectibleDetail
    ): BaseCollectibleMediaItem.NoMediaCollectibleMediaItem {
        return BaseCollectibleMediaItem.NoMediaCollectibleMediaItem(
            downloadUrl = collectibleMedia.downloadUrl,
            previewUrl = collectibleMedia.previewUrl,
            mediaExtension = collectibleMedia.mediaExtension,
            collectibleId = collectibleId,
            shouldDecreaseOpacity = isOwnedByTheUser,
            baseAssetDrawableProvider = assetDrawableProviderDecider.getAssetDrawableProvider(collectibleDetail),
            has3dSupport = false,
            hasFullScreenSupport = false,
            showPlayButton = false
        )
    }
}
