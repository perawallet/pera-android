package com.algorand.android.core.component.collectible.domain.usecase

import com.algorand.android.accountinfo.component.domain.usecase.GetAccountInformation
import com.algorand.android.assetdetail.component.asset.domain.model.detail.CollectibleDetail
import com.algorand.android.core.component.collectible.domain.mapper.BaseOwnedCollectibleDataFactory
import com.algorand.android.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData
import javax.inject.Inject

internal class GetAccountCollectibleDetailUseCase @Inject constructor(
    private val getAccountInformation: GetAccountInformation,
    private val baseOwnedCollectibleDataFactory: BaseOwnedCollectibleDataFactory
) : GetAccountCollectibleDetail {

    override suspend fun invoke(address: String, collectible: CollectibleDetail): BaseOwnedCollectibleData? {
        val accountInformation = getAccountInformation(address) ?: return null
        val assetHolding = accountInformation.assetHoldings.firstOrNull {
            it.assetId == collectible.id
        } ?: return null
        return baseOwnedCollectibleDataFactory(assetHolding, collectible)
    }
}
