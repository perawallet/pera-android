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
import com.algorand.android.assetdetail.component.AssetConstants.ALGO_ASSET_ID
import com.algorand.android.assetdetail.component.AssetConstants.ALGO_SHORT_NAME
import com.algorand.android.assetdetail.component.asset.domain.usecase.GetAssetDetail
import com.algorand.android.module.asset.detail.ui.detail.asaprofile.mapper.AsaStatusPreviewMapper
import com.algorand.android.module.asset.detail.ui.detail.asaprofile.model.AsaProfilePreview
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow

internal class GetAlgoProfilePreviewWithAccountInformationUseCase @Inject constructor(
    private val getAssetDetail: GetAssetDetail,
    private val asaStatusPreviewMapper: AsaStatusPreviewMapper,
    private val createAsaProfilePreviewFromAssetDetail: CreateAsaProfilePreviewFromAssetDetail,
    private val getAssetName: GetAssetName
) : GetAlgoProfilePreviewWithAccountInformation {

    override suspend fun invoke(accountAddress: String): Flow<AsaProfilePreview?> = flow {
        getAssetDetail(ALGO_ASSET_ID)?.run {
            val asaStatusPreview = asaStatusPreviewMapper(
                isAlgo = true,
                isUserOptedInAsset = true,
                accountAddress = accountAddress,
                hasUserAmount = true,
                assetShortName = getAssetName(ALGO_SHORT_NAME)
            )
            val preview = createAsaProfilePreviewFromAssetDetail(asset = this, asaStatusPreview)
            emit(preview)
        }
    }.distinctUntilChanged()
}
