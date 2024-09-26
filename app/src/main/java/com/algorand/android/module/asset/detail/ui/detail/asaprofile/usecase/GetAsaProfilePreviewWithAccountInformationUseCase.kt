/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.module.asset.detail.ui.detail.asaprofile.usecase

import com.algorand.android.module.account.core.ui.usecase.GetAssetName
import com.algorand.android.module.account.info.domain.model.AccountInformation
import com.algorand.android.module.account.info.domain.usecase.GetAccountInformationFlow
import com.algorand.android.module.asset.detail.component.assetabout.domain.usecase.GetAssetFlowFromAsaProfileCache
import com.algorand.android.module.asset.detail.ui.detail.asaprofile.mapper.AsaStatusPreviewMapper
import com.algorand.android.module.asset.detail.ui.detail.asaprofile.model.AsaProfilePreview
import com.algorand.android.module.asset.detail.ui.detail.asaprofile.model.AsaStatusPreview
import com.algorand.android.module.account.core.component.assetdata.usecase.GetAccountBaseOwnedAssetData
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

internal class GetAsaProfilePreviewWithAccountInformationUseCase @Inject constructor(
    private val getAssetFlowFromAsaProfileCache: GetAssetFlowFromAsaProfileCache,
    private val getAccountInformationFlow: GetAccountInformationFlow,
    private val asaStatusPreviewMapper: AsaStatusPreviewMapper,
    private val getAssetName: GetAssetName,
    private val getAccountBaseOwnedAssetData: GetAccountBaseOwnedAssetData,
    private val createAsaProfilePreviewFromAssetDetail: CreateAsaProfilePreviewFromAssetDetail
) : GetAsaProfilePreviewWithAccountInformation {

    override suspend fun invoke(accountAddress: String, assetId: Long): Flow<AsaProfilePreview?> {
        return combine(
            getAssetFlowFromAsaProfileCache(),
            getAccountInformationFlow(accountAddress)
        ) { cachedAssetDetail, accountInfo ->
            val asaStatusPreview = getAsaStatusPreview(accountAddress, accountInfo, assetId)
            cachedAssetDetail?.getDataOrNull()?.run {
                createAsaProfilePreviewFromAssetDetail(this, asaStatusPreview)
            }
        }
    }

    private suspend fun getAsaStatusPreview(
        address: String,
        accountInformation: AccountInformation?,
        assetId: Long
    ): AsaStatusPreview? {
        val ownedAssetData = getAccountBaseOwnedAssetData(address, assetId)
        return asaStatusPreviewMapper(
            isAlgo = false,
            isUserOptedInAsset = accountInformation?.hasAsset(assetId) == true,
            accountAddress = address,
            hasUserAmount = accountInformation?.hasAssetAmount(assetId) == true,
            formattedAccountBalance = ownedAssetData?.formattedAmount,
            assetShortName = getAssetName(ownedAssetData?.shortName.orEmpty())
        )
    }
}
