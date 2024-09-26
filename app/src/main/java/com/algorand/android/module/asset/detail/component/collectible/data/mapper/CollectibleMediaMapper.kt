package com.algorand.android.module.asset.detail.component.collectible.data.mapper

import com.algorand.android.module.asset.detail.component.asset.data.model.collectible.CollectibleMediaResponse
import com.algorand.android.module.asset.detail.component.collectible.domain.model.BaseCollectibleMedia

internal interface CollectibleMediaMapper {
    operator fun invoke(response: CollectibleMediaResponse): BaseCollectibleMedia
}
