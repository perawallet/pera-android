package com.algorand.android.core.component.domain.usecase

import com.algorand.android.accountinfo.component.domain.usecase.GetAccountInformationFlow
import com.algorand.android.core.component.assetdata.usecase.CreateAccountAssetData
import com.algorand.android.core.component.assetdata.usecase.GetAccountAssetDataFlow
import com.algorand.android.core.component.assetdata.model.AccountAssetData
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class GetAccountAssetDataFlowUseCase @Inject constructor(
    private val createAccountAssetData: CreateAccountAssetData,
    private val getAccountInformationFlow: GetAccountInformationFlow,
) : GetAccountAssetDataFlow {

    override fun invoke(address: String, includeAlgo: Boolean): Flow<AccountAssetData> {
        return getAccountInformationFlow(address).map { accountInformation ->
            if (accountInformation == null) return@map AccountAssetData()
            createAccountAssetData(accountInformation, includeAlgo)
        }
    }
}
