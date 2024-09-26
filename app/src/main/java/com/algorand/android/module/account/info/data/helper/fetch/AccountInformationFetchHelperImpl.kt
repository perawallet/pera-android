package com.algorand.android.module.account.info.data.helper.fetch

import com.algorand.android.module.account.info.data.helper.querybuilder.IndexerAccountFetchRequestExcludes.ASSETS
import com.algorand.android.module.account.info.data.helper.querybuilder.IndexerAccountFetchRequestExcludes.CREATED_APPS
import com.algorand.android.module.account.info.data.helper.querybuilder.IndexerAccountFetchRequestExcludes.CREATED_ASSETS
import com.algorand.android.module.account.info.data.helper.querybuilder.IndexerAccountFetchRequestExcludesQueryBuilder
import com.algorand.android.module.account.info.data.mapper.AccountInformationResponseMapper
import com.algorand.android.module.account.info.data.model.AccountInformationResponse
import com.algorand.android.module.account.info.data.service.IndexerApi
import com.algorand.android.foundation.PeraResult
import com.algorand.android.network_utils.request
import javax.inject.Inject
import okio.IOException

internal class AccountInformationFetchHelperImpl @Inject constructor(
    private val indexerApi: IndexerApi,
    private val accountAssetHoldingsFetchHelper: AccountAssetHoldingsFetchHelper,
    private val accountInformationResponseMapper: AccountInformationResponseMapper,
) : AccountInformationFetchHelper {

    override suspend fun fetchAccount(address: String): PeraResult<AccountInformationResponse> {
        val excludesQuery = IndexerAccountFetchRequestExcludesQueryBuilder.newBuilder()
            .addExclude(CREATED_ASSETS)
            .addExclude(CREATED_APPS)
            .build()
        return request { indexerApi.getAccountInformation(address, excludesQuery) }.use(
            onSuccess = { response ->
                PeraResult.Success(response)
            },
            onFailed = { exception, errorCode ->
                processFailedResponse(address, exception, errorCode)
            }
        )
    }

    private suspend fun processFailedResponse(
        address: String,
        exception: Exception,
        errorCode: Int?
    ): PeraResult<AccountInformationResponse> {
        return when {
            errorCode == ACCOUNT_NOT_FOUND -> {
                PeraResult.Success(accountInformationResponseMapper.createEmptyAccount(address))
            }
            exception is IOException -> PeraResult.Error(exception, errorCode)
            else -> fetchAccountAndAssetsSeparately(address)
        }
    }

    private suspend fun fetchAccountAndAssetsSeparately(address: String): PeraResult<AccountInformationResponse> {
        val excludesQuery = IndexerAccountFetchRequestExcludesQueryBuilder.newBuilder()
            .addExclude(CREATED_ASSETS)
            .addExclude(CREATED_APPS)
            .addExclude(ASSETS)
            .build()
        return request { indexerApi.getAccountInformation(address, excludesQuery) }.use(
            onSuccess = { response ->
                fetchAssets(address, response)
            },
            onFailed = { exception, code ->
                PeraResult.Error(exception, code)
            }
        )
    }

    private suspend fun fetchAssets(
        address: String,
        response: AccountInformationResponse
    ): PeraResult<AccountInformationResponse> {
        return accountAssetHoldingsFetchHelper.fetchAccountAssetHoldings(address).use(
            onSuccess = { assetHoldings ->
                val accountInfo = response.copy(
                    accountInformation = response.accountInformation?.copy(allAssetHoldingList = assetHoldings)
                )
                PeraResult.Success(accountInfo)
            },
            onFailed = { exception, code ->
                PeraResult.Error(exception, code)
            }
        )
    }

    private companion object {
        const val ACCOUNT_NOT_FOUND = 404
    }
}
