/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.assetinbox.assetinboxoneaccount.ui.usecase

import com.algorand.android.modules.assetinbox.assetinboxoneaccount.domain.model.AssetInboxOneAccountPaginated
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.domain.usecase.GetAssetInboxOneAccountPaginated
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.ui.mapper.AssetInboxOneAccountPreviewMapper
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.ui.model.AssetInboxOneAccountPreview
import com.algorand.android.utils.ErrorResource
import com.algorand.android.utils.Event
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AssetInboxOneAccountPreviewUseCase @Inject constructor(
    private val getAssetInboxOneAccountPaginated: GetAssetInboxOneAccountPaginated,
    private val assetInboxOneAccountPreviewMapper: AssetInboxOneAccountPreviewMapper
) {

    fun getInitialPreview(): AssetInboxOneAccountPreview {
        return assetInboxOneAccountPreviewMapper.getInitialPreview()
    }

    fun getAssetInboxOneAccountPreview(
        preview: AssetInboxOneAccountPreview,
        receiverAccountAddress: String,
    ): Flow<AssetInboxOneAccountPreview> = flow {

        val assetInboxOneAccountPaginated = getAssetInboxOneAccountPaginated(receiverAccountAddress)
        assetInboxOneAccountPaginated.use(
            onSuccess = {
                emit(createAssetInboxOneAccountPreview(it))
            },
            onFailed = { exception, _ ->
                val errorEvent = Event(ErrorResource.Api(exception.message.orEmpty()))
                val newPreview = preview.copy(isLoading = false, showError = errorEvent)
                emit(newPreview)
            }
        )
    }

    private suspend fun createAssetInboxOneAccountPreview(
        assetInboxOneAccountPaginated: AssetInboxOneAccountPaginated,
    ): AssetInboxOneAccountPreview {
        return assetInboxOneAccountPreviewMapper.invoke(
            assetInboxOneAccountPaginated,
            isEmptyStateVisible = assetInboxOneAccountPaginated.results.isEmpty(),
            isLoading = false,
            showError = null,
            onNavBack = null
        )
    }
}
