package com.algorand.android.module.account.core.component.domain.usecase

import com.algorand.android.module.account.info.domain.usecase.GetAccountInformationFlow
import com.algorand.android.assetdetail.component.asset.domain.usecase.GetCollectibleDetail
import com.algorand.android.module.account.core.component.collectible.domain.mapper.BaseOwnedCollectibleDataFactory
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class GetAccountCollectibleDataFlowUseCase @Inject constructor(
    private val getAccountInformationFlow: GetAccountInformationFlow,
    private val getCollectibleDetail: GetCollectibleDetail,
    private val baseOwnedCollectibleDataFactory: BaseOwnedCollectibleDataFactory
) : GetAccountCollectibleDataFlow {

    override fun invoke(address: String): Flow<List<BaseOwnedCollectibleData>> {
        return getAccountInformationFlow(address).map {
            val accountInformation = it ?: return@map emptyList()
            val accountAssetDataList = mutableListOf<BaseOwnedCollectibleData>()
            accountInformation.assetHoldings.forEach { assetHolding ->
                val collectibleDetail = getCollectibleDetail(assetHolding.assetId)
                if (collectibleDetail != null) {
                    val collectibleData = baseOwnedCollectibleDataFactory(assetHolding, collectibleDetail)
                    accountAssetDataList.add(collectibleData)
                }
            }
            accountAssetDataList
        }
    }
}
