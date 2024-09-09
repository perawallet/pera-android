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

package com.algorand.android.assetdetailui.detail.asaprofile.usecase

import com.algorand.android.assetdetail.component.AssetConstants.ALGO_ASSET_ID
import com.algorand.android.assetdetailui.detail.asaprofile.model.AsaProfilePreview
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

internal class GetAsaProfilePreviewUseCase @Inject constructor(
    private val getAsaProfilePreviewWithoutAccountInformation: GetAsaProfilePreviewWithoutAccountInformation,
    private val getAsaProfilePreviewWithAccountInformation: GetAsaProfilePreviewWithAccountInformation,
    private val getAlgoProfilePreviewWithAccountInformation: GetAlgoProfilePreviewWithAccountInformation
) : GetAsaProfilePreview {

    override suspend fun invoke(accountAddress: String?, assetId: Long): Flow<AsaProfilePreview?> {
        return when {
            accountAddress.isNullOrBlank() -> getAsaProfilePreviewWithoutAccountInformation()
            assetId == ALGO_ASSET_ID -> getAlgoProfilePreviewWithAccountInformation(accountAddress)
            else -> getAsaProfilePreviewWithAccountInformation(accountAddress, assetId)
        }.distinctUntilChanged()
    }
}
