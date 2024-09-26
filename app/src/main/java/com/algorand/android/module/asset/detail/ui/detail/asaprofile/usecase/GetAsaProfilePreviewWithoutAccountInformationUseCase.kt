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

import com.algorand.android.module.asset.detail.component.assetabout.domain.usecase.GetAssetFlowFromAsaProfileCache
import com.algorand.android.module.asset.detail.ui.detail.asaprofile.mapper.AsaStatusPreviewMapper
import com.algorand.android.module.asset.detail.ui.detail.asaprofile.model.AsaProfilePreview
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class GetAsaProfilePreviewWithoutAccountInformationUseCase @Inject constructor(
    private val getAssetFlowFromAsaProfileCache: GetAssetFlowFromAsaProfileCache,
    private val asaStatusPreviewMapper: AsaStatusPreviewMapper,
    private val createAsaProfilePreviewFromAssetDetail: CreateAsaProfilePreviewFromAssetDetail
) : GetAsaProfilePreviewWithoutAccountInformation {

    override suspend fun invoke(): Flow<AsaProfilePreview?> {
        return getAssetFlowFromAsaProfileCache().map { cachedAssetDetailResult ->
            val asaStatusPreview = asaStatusPreviewMapper(
                isAlgo = false,
                isUserOptedInAsset = false,
                accountAddress = null,
                hasUserAmount = false,
                assetShortName = null
            )
            cachedAssetDetailResult?.getDataOrNull()?.run {
                createAsaProfilePreviewFromAssetDetail(asset = this, asaStatusPreview)
            }
        }
    }
}
