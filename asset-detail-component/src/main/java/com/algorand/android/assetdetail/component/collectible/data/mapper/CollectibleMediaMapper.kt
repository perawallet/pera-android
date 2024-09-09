package com.algorand.android.assetdetail.component.collectible.data.mapper

import com.algorand.android.assetdetail.component.asset.data.model.collectible.CollectibleMediaResponse
import com.algorand.android.assetdetail.component.collectible.domain.model.BaseCollectibleMedia

internal interface CollectibleMediaMapper {
    operator fun invoke(response: CollectibleMediaResponse): BaseCollectibleMedia
}
