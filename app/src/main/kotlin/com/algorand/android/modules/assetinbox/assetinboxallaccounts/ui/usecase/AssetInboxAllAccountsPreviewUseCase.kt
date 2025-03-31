package com.algorand.android.modules.assetinbox.assetinboxallaccounts.ui.usecase

import com.algorand.android.modules.assetinbox.assetinboxallaccounts.ui.mapper.AssetInboxAllAccountsPreviewMapper
import com.algorand.android.modules.assetinbox.assetinboxallaccounts.ui.model.AssetInboxAllAccountsPreview
import com.algorand.android.utils.ErrorResource
import com.algorand.android.utils.Event
import com.algorand.wallet.asset.assetinbox.domain.model.AssetInboxRequest
import com.algorand.wallet.asset.assetinbox.domain.usecase.GetAssetInboxRequests
import com.algorand.wallet.asset.assetinbox.domain.usecase.GetAssetInboxValidAddresses
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AssetInboxAllAccountsPreviewUseCase @Inject constructor(
    private val getAssetInboxRequests: GetAssetInboxRequests,
    private val assetInboxAllAccountsPreviewMapper: AssetInboxAllAccountsPreviewMapper,
    private val getAssetInboxValidAddresses: GetAssetInboxValidAddresses
) {

    fun getInitialPreview(): AssetInboxAllAccountsPreview {
        return assetInboxAllAccountsPreviewMapper.getInitialPreview()
    }

    fun getAssetInboxAllAccountsPreview(
        preview: AssetInboxAllAccountsPreview
    ): Flow<AssetInboxAllAccountsPreview> = flow {
        val accountAddresses = getAssetInboxValidAddresses()
        if (accountAddresses.isEmpty()) {
            emit(createAssetInboxAllAccountsPreview(emptyList(), accountAddresses))
            return@flow
        }
        getAssetInboxRequests(accountAddresses).use(
            onSuccess = {
                emit(createAssetInboxAllAccountsPreview(it, accountAddresses))
            },
            onFailed = { exception, _ ->
                val errorEvent = Event(ErrorResource.Api(exception.message.orEmpty()))
                val newPreview = preview.copy(isLoading = false, showError = errorEvent)
                emit(newPreview)
            }
        )
    }

    private suspend fun createAssetInboxAllAccountsPreview(
        assetInboxAllAccountsList: List<AssetInboxRequest>,
        addresses: List<String>,
    ): AssetInboxAllAccountsPreview {
        return assetInboxAllAccountsPreviewMapper.invoke(
            assetInboxAllAccountsList,
            addresses,
            isEmptyStateVisible = assetInboxAllAccountsList.none { it.requestCount > 0 },
            isLoading = false,
            showError = null,
            onNavBack = null
        )
    }
}
