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

package com.algorand.android.module.asset.action.ui.usecase

import com.algorand.android.module.asset.action.ui.model.AssetActionAccountDetail
import com.algorand.android.module.asset.action.ui.model.AssetActionInformation
import com.algorand.android.module.asset.action.ui.model.AssetActionPreview
import com.algorand.android.foundation.Event
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class GetAssetActionPreviewUseCase @Inject constructor(
    private val getAssetActionInformation: GetAssetActionInformation,
    private val getAssetActionAccountDetail: GetAssetActionAccountDetail
) : GetAssetActionPreview {

    override fun invoke(assetId: Long, accountAddress: String?): Flow<AssetActionPreview> = flow {
        emit(AssetActionPreview(isLoading = true))

        val preview = getAssetActionInformation(assetId).use(
            onSuccess = { getSuccessAssetActionPreview(it, accountAddress) },
            onFailed = { exception, _ -> getErrorAssetActionPreview(exception.message, accountAddress) }
        )
        emit(preview)
    }

    private suspend fun getSuccessAssetActionPreview(
        assetInfo: AssetActionInformation,
        accountAddress: String?
    ): AssetActionPreview {
        return AssetActionPreview(
            isLoading = false,
            assetActionInformation = assetInfo,
            accountDetail = getAccountDetail(accountAddress)
        )
    }

    private suspend fun getErrorAssetActionPreview(errorMessage: String?, accountAddress: String?): AssetActionPreview {
        return AssetActionPreview(
            isLoading = false,
            accountDetail = getAccountDetail(accountAddress),
            showError = Event(errorMessage)
        )
    }

    private suspend fun getAccountDetail(accountAddress: String?): AssetActionAccountDetail? {
        if (accountAddress == null) return null
        return getAssetActionAccountDetail(accountAddress)
    }
}
