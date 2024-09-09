package com.algorand.android.assetdetail.component.collectible.data.repository

import com.algorand.android.assetdetail.component.asset.data.service.AssetDetailApi
import com.algorand.android.assetdetail.component.asset.domain.model.detail.CollectibleDetail
import com.algorand.android.assetdetail.component.collectible.data.mapper.CollectibleDetailMapper
import com.algorand.android.assetdetail.component.collectible.domain.repository.CollectibleDetailRepository
import com.algorand.android.foundation.PeraResult
import com.algorand.android.network_utils.request
import javax.inject.Inject

internal class CollectibleDetailRepositoryImpl @Inject constructor(
    private val assetDetailApi: AssetDetailApi,
    private val collectibleDetailMapper: CollectibleDetailMapper
) : CollectibleDetailRepository {

    override suspend fun fetchCollectibleDetail(collectibleAssetId: Long): PeraResult<CollectibleDetail> {
        return request { assetDetailApi.getAssetDetail(collectibleAssetId) }.use(
            onSuccess = {
                val collectibleDetail = collectibleDetailMapper(it)
                if (collectibleDetail == null) {
                    PeraResult.Error(Exception("CollectibleDetail is null"))
                } else {
                    PeraResult.Success(collectibleDetail)
                }
            },
            onFailed = { exception, code ->
                PeraResult.Error(exception, code)
            }
        )
    }
}
