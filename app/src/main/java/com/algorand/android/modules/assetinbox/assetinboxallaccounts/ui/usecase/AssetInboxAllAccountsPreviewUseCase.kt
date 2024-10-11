package com.algorand.android.modules.assetinbox.assetinboxallaccounts.ui.usecase

import com.algorand.android.core.AccountManager
import com.algorand.android.customviews.accountandassetitem.mapper.AccountItemConfigurationMapper
import com.algorand.android.models.Account
import com.algorand.android.modules.assetinbox.assetinboxallaccounts.domain.model.AssetInboxAllAccounts
import com.algorand.android.modules.assetinbox.assetinboxallaccounts.domain.repository.AssetInboxAllAccountsRepository
import com.algorand.android.modules.assetinbox.assetinboxallaccounts.domain.usecase.GetAssetInboxAllAccounts
import com.algorand.android.modules.assetinbox.assetinboxallaccounts.ui.model.AssetInboxAllAccountsPreview
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.ui.mapper.AssetInboxAllAccountsPreviewMapper
import com.algorand.android.utils.CacheResult
import com.algorand.android.utils.ErrorResource
import com.algorand.android.utils.Event
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow

class AssetInboxAllAccountsPreviewUseCase @Inject constructor(
    private val getAssetInboxAllAccounts: GetAssetInboxAllAccounts,
    private val assetInboxAllAccountsPreviewMapper: AssetInboxAllAccountsPreviewMapper,
    private val assetInboxAllAccountsRepository: AssetInboxAllAccountsRepository,
    private val accountManager: AccountManager,
    private val accountItemConfigurationMapper: AccountItemConfigurationMapper
) {

    fun getInitialPreview(): AssetInboxAllAccountsPreview {
        return assetInboxAllAccountsPreviewMapper.getInitialPreview()
    }

    suspend fun getAssetInboxAllAccountsPreview(
        preview: AssetInboxAllAccountsPreview
    ): Flow<AssetInboxAllAccountsPreview> = flow {

        val assetInboxAllAccounts = getAssetInboxAllAccounts(getAllAccountAddresses())
        assetInboxAllAccounts.use(
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
            getAllAccounts(),
            isEmptyStateVisible = assetInboxAllAccountsList.filter { it.requestCount > 0 }.isEmpty(),
            isLoading = false,
            showError = null,
            onNavBack = null
        )
    }

    suspend fun updateAssetInboxAllAccountsCache() {
        with(assetInboxAllAccountsRepository) {
            assetInboxAllAccountsRepository.getAssetInboxAllAccounts(getAllAccountAddresses()).use(
                onSuccess = { assetInboxAllAccounts ->
                    assetInboxAllAccountsRepository.cacheAssetInboxAllAccounts(
                        assetInboxAllAccounts.map { com.algorand.android.utils.CacheResult.Success.create(it) }
                    )
                },
                onFailed = { exception, code ->
                    android.util.Log.e("AssetInboxAllAccountsUseCase", "updateAssetInboxAllAccountsCache: $exception")
                })
        }
    }

    suspend fun getAssetInboxAllAccountsCacheFlow():
            StateFlow<HashMap<String, CacheResult<AssetInboxAllAccounts>>> {
        return assetInboxAllAccountsRepository.getAssetInboxAllAccountsCacheFlow()
    }

    suspend fun getAssetInboxCountCacheFlow(): Flow<Int> {
        return assetInboxAllAccountsRepository.getAssetInboxCountCacheFlow()
    }

    fun getAllAccountAddresses(): List<String> {
        return accountManager.getAccounts().map { it.address }
    }

    fun getAllAccounts(): List<Account> {
        return accountManager.getAccounts()
    }
}
