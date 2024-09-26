package com.algorand.android.module.account.core.component.polling.accountdetail.domain.usecase.implementation

import com.algorand.android.account.localaccount.domain.usecase.GetLocalAccounts
import com.algorand.android.module.account.core.component.polling.accountdetail.domain.usecase.UpdateAccountCache
import com.algorand.android.accountinfo.component.domain.usecase.FetchAndCacheAccountInformation
import com.algorand.android.assetdetail.component.asset.domain.usecase.FetchAndCacheAssets
import javax.inject.Inject

internal class UpdateAccountCacheUseCase @Inject constructor(
    private val getLocalAccounts: GetLocalAccounts,
    private val fetchAndCacheAccountInformation: FetchAndCacheAccountInformation,
    private val fetchAndCacheAssets: FetchAndCacheAssets
) : UpdateAccountCache {

    override suspend fun invoke() {
        val localAccountAddresses = getLocalAccounts().map { it.address }
        val assetIds = fetchAndCacheAccountInformation(localAccountAddresses).mapNotNull {
            if (it.value == null) return@mapNotNull null
            it.value?.assetHoldings?.map { assetHolding ->
                assetHolding.assetId
            }
        }.flatten()
        fetchAndCacheAssets(assetIds, false)
    }
}
