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

package com.algorand.android.module.asset.detail.ui.detail.nftprofile.mapper

import com.algorand.android.module.account.core.ui.asset.assetdrawable.GetAssetDrawableProvider
import com.algorand.android.module.asset.detail.component.collectible.domain.model.BaseCollectibleMedia
import com.algorand.android.module.asset.detail.ui.detail.nftprofile.model.BaseCollectibleMediaItem
import com.algorand.android.module.asset.detail.ui.detail.nftprofile.model.BaseCollectibleMediaItem.AudioCollectibleMediaItem
import com.algorand.android.module.asset.detail.ui.detail.nftprofile.model.BaseCollectibleMediaItem.GifCollectibleMediaItem
import com.algorand.android.module.asset.detail.ui.detail.nftprofile.model.BaseCollectibleMediaItem.ImageCollectibleMediaItem
import com.algorand.android.module.asset.detail.ui.detail.nftprofile.model.BaseCollectibleMediaItem.NoMediaCollectibleMediaItem
import com.algorand.android.module.asset.detail.ui.detail.nftprofile.model.BaseCollectibleMediaItem.UnsupportedCollectibleMediaItem
import com.algorand.android.module.asset.detail.ui.detail.nftprofile.model.BaseCollectibleMediaItem.VideoCollectibleMediaItem
import com.algorand.android.module.asset.detail.ui.detail.nftprofile.model.CollectibleMediaItemMapperPayload
import javax.inject.Inject

internal class CollectibleMediaItemMapperImpl @Inject constructor(
    private val getAssetDrawableProvider: GetAssetDrawableProvider
) : CollectibleMediaItemMapper {

    override fun invoke(payload: CollectibleMediaItemMapperPayload): BaseCollectibleMediaItem {
        return when (payload.baseCollectibleMedia) {
            is BaseCollectibleMedia.GifCollectibleMedia -> payload.mapToGifItem()
            is BaseCollectibleMedia.AudioCollectibleMedia -> payload.mapToAudioItem()
            is BaseCollectibleMedia.ImageCollectibleMedia -> payload.mapToImagetem()
            is BaseCollectibleMedia.NoMediaCollectibleMedia -> payload.mapToNoMediaItem()
            is BaseCollectibleMedia.UnsupportedCollectibleMedia -> payload.mapToUnsupportedItem()
            is BaseCollectibleMedia.VideoCollectibleMedia -> payload.mapToVideoItem()
        }
    }

    private fun CollectibleMediaItemMapperPayload.mapToGifItem(): GifCollectibleMediaItem {
        return GifCollectibleMediaItem(
            downloadUrl = baseCollectibleMedia.downloadUrl,
            previewUrl = baseCollectibleMedia.previewUrl,
            collectibleId = baseCollectibleDetail.id,
            shouldDecreaseOpacity = shouldDecreaseOpacity,
            baseAssetDrawableProvider = getAssetDrawableProvider(baseCollectibleDetail),
            has3dSupport = showMediaButtons,
            hasFullScreenSupport = showMediaButtons,
            showPlayButton = false,
            assetName = assetName
        )
    }

    private fun CollectibleMediaItemMapperPayload.mapToAudioItem(): AudioCollectibleMediaItem {
        return AudioCollectibleMediaItem(
            downloadUrl = baseCollectibleMedia.downloadUrl,
            previewUrl = baseCollectibleMedia.previewUrl,
            collectibleId = baseCollectibleDetail.id,
            shouldDecreaseOpacity = shouldDecreaseOpacity,
            baseAssetDrawableProvider = getAssetDrawableProvider(baseCollectibleDetail),
            has3dSupport = false,
            hasFullScreenSupport = showMediaButtons,
            showPlayButton = showMediaButtons,
            assetName = assetName
        )
    }

    private fun CollectibleMediaItemMapperPayload.mapToImagetem(): ImageCollectibleMediaItem {
        return ImageCollectibleMediaItem(
            downloadUrl = baseCollectibleMedia.downloadUrl,
            previewUrl = baseCollectibleMedia.previewUrl,
            collectibleId = baseCollectibleDetail.id,
            shouldDecreaseOpacity = shouldDecreaseOpacity,
            baseAssetDrawableProvider = getAssetDrawableProvider(baseCollectibleDetail),
            has3dSupport = showMediaButtons,
            hasFullScreenSupport = showMediaButtons,
            showPlayButton = false,
            assetName = assetName
        )
    }

    private fun CollectibleMediaItemMapperPayload.mapToNoMediaItem(): NoMediaCollectibleMediaItem {
        return NoMediaCollectibleMediaItem(
            downloadUrl = baseCollectibleMedia.downloadUrl,
            previewUrl = baseCollectibleMedia.previewUrl,
            collectibleId = baseCollectibleDetail.id,
            shouldDecreaseOpacity = shouldDecreaseOpacity,
            baseAssetDrawableProvider = getAssetDrawableProvider(baseCollectibleDetail),
            has3dSupport = false,
            hasFullScreenSupport = false,
            showPlayButton = false,
            assetName = assetName
        )
    }

    private fun CollectibleMediaItemMapperPayload.mapToUnsupportedItem(): UnsupportedCollectibleMediaItem {
        return UnsupportedCollectibleMediaItem(
            downloadUrl = baseCollectibleMedia.downloadUrl,
            previewUrl = baseCollectibleMedia.previewUrl,
            collectibleId = baseCollectibleDetail.id,
            shouldDecreaseOpacity = shouldDecreaseOpacity,
            baseAssetDrawableProvider = getAssetDrawableProvider(baseCollectibleDetail),
            has3dSupport = false,
            hasFullScreenSupport = false,
            showPlayButton = false,
            assetName = assetName
        )
    }

    private fun CollectibleMediaItemMapperPayload.mapToVideoItem(): VideoCollectibleMediaItem {
        return VideoCollectibleMediaItem(
            downloadUrl = baseCollectibleMedia.downloadUrl,
            previewUrl = baseCollectibleMedia.previewUrl,
            collectibleId = baseCollectibleDetail.id,
            shouldDecreaseOpacity = shouldDecreaseOpacity,
            baseAssetDrawableProvider = getAssetDrawableProvider(baseCollectibleDetail),
            has3dSupport = false,
            hasFullScreenSupport = showMediaButtons,
            showPlayButton = showMediaButtons,
            assetName = assetName
        )
    }
}
