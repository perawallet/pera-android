package com.algorand.android.modules.assetinbox.assetinboxoneaccount.ui.usecase

import com.algorand.android.modules.assetinbox.assetinboxoneaccount.domain.model.AssetInboxOneAccountPaginated
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.domain.usecase.GetAssetInboxOneAccountPaginated
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.ui.mapper.AssetInboxOneAccountPreviewMapper
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.ui.model.AssetInboxOneAccountPreview
import com.algorand.android.utils.ErrorResource
import com.algorand.android.utils.Event
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AssetInboxOneAccountPreviewUseCase @Inject constructor(
    private val getAssetInboxOneAccountPaginated: GetAssetInboxOneAccountPaginated,
    private val assetInboxOneAccountPreviewMapper: AssetInboxOneAccountPreviewMapper
) {

    fun getInitialPreview(): AssetInboxOneAccountPreview {
        return assetInboxOneAccountPreviewMapper.getInitialPreview()
    }

    suspend fun getAssetInboxOneAccountPreview(
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

    private fun createAssetInboxOneAccountPreview(
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
