package com.algorand.android.modules.assetinbox.assetinboxallaccounts.ui.usecase

import com.algorand.android.core.AccountManager
import com.algorand.android.modules.assetinbox.assetinboxallaccounts.domain.model.AssetInboxAllAccounts
import com.algorand.android.modules.assetinbox.assetinboxallaccounts.domain.usecase.GetAssetInboxAllAccounts
import com.algorand.android.modules.assetinbox.assetinboxallaccounts.ui.model.AssetInboxAllAccountsPreview
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.ui.mapper.AssetInboxAllAccountsPreviewMapper
import com.algorand.android.utils.ErrorResource
import com.algorand.android.utils.Event
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AssetInboxAllAccountsPreviewUseCase @Inject constructor(
    private val getAssetInboxAllAccounts: GetAssetInboxAllAccounts,
    private val assetInboxAllAccountsPreviewMapper: AssetInboxAllAccountsPreviewMapper,
    private val accountManager: AccountManager
) {

    fun getInitialPreview(): AssetInboxAllAccountsPreview {
        return assetInboxAllAccountsPreviewMapper.getInitialPreview()
    }

    fun getAssetInboxAllAccountsPreview(
        preview: AssetInboxAllAccountsPreview
    ): Flow<AssetInboxAllAccountsPreview> = flow {
        val allAccountAddresses = accountManager.getAllAccountsAddressesExceptWatch()
        if (allAccountAddresses.isEmpty()) {
            emit(createAssetInboxAllAccountsPreview(emptyList()))
            return@flow
        }
        getAssetInboxAllAccounts(allAccountAddresses).use(
            onSuccess = {
                emit(createAssetInboxAllAccountsPreview(it))
            },
            onFailed = { exception, _ ->
                val errorEvent = Event(ErrorResource.Api(exception.message.orEmpty()))
                val newPreview = preview.copy(isLoading = false, showError = errorEvent)
                emit(newPreview)
            }
        )
    }

    private fun createAssetInboxAllAccountsPreview(
        assetInboxAllAccountsList: List<AssetInboxAllAccounts>,
    ): AssetInboxAllAccountsPreview {
        return assetInboxAllAccountsPreviewMapper.invoke(
            assetInboxAllAccountsList,
            accountManager.getAllAccountsExceptWatch(),
            isEmptyStateVisible = assetInboxAllAccountsList.none { it.requestCount > 0 },
            isLoading = false,
            showError = null,
            onNavBack = null
        )
    }
}
