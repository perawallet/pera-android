package com.algorand.android.module.asset.detail.component.asset.data.mapper.model

import com.algorand.android.module.asset.detail.component.asset.data.model.AssetCreatorResponse
import com.algorand.android.module.asset.detail.component.asset.domain.model.AssetCreator

internal interface AssetCreatorMapper {
    operator fun invoke(response: AssetCreatorResponse): AssetCreator?
    operator fun invoke(id: Long?, address: String?, isVerified: Boolean?): AssetCreator?
}
