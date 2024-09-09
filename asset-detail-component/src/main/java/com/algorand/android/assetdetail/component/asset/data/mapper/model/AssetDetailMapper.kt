package com.algorand.android.assetdetail.component.asset.data.mapper.model

import com.algorand.android.assetdetail.component.asset.data.model.AssetResponse
import com.algorand.android.assetdetail.component.asset.data.model.NodeAssetDetailResponse
import com.algorand.android.assetdetail.component.asset.domain.model.detail.AssetDetail
import com.algorand.android.shared_db.assetdetail.model.AssetDetailEntity

internal interface AssetDetailMapper {
    operator fun invoke(response: AssetResponse): AssetDetail?

    operator fun invoke(assetId: Long, nodeResponse: NodeAssetDetailResponse): AssetDetail

    operator fun invoke(entity: AssetDetailEntity): AssetDetail
}
