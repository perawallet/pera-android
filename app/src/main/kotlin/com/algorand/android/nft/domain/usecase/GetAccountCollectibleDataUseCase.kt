package com.algorand.android.nft.domain.usecase

import com.algorand.android.models.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData
import com.algorand.android.modules.collectibles.common.mapper.BaseOwnedCollectibleDataFactory
import com.algorand.wallet.account.info.domain.usecase.GetAccountAssetHolding
import com.algorand.wallet.asset.domain.usecase.GetCollectibleDetail
import javax.inject.Inject

internal class GetAccountCollectibleDataUseCase @Inject constructor(
    private val getAccountAssetHolding: GetAccountAssetHolding,
    private val getCollectibleDetail: GetCollectibleDetail,
    private val baseOwnedCollectibleDataFactory: BaseOwnedCollectibleDataFactory
) : GetAccountCollectibleData {

    override suspend fun invoke(address: String, assetId: Long): BaseOwnedCollectibleData? {
        val assetHolding = getAccountAssetHolding(address, assetId) ?: return null
        val collectibleDetail = getCollectibleDetail(assetId) ?: return null
        return baseOwnedCollectibleDataFactory(assetHolding, collectibleDetail)
    }
}
