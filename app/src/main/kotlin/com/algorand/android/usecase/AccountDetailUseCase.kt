@file:SuppressWarnings("TooManyFunctions")

/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.usecase

import com.algorand.android.core.AccountManager
import com.algorand.android.core.BaseUseCase
import com.algorand.android.models.Account
import com.algorand.android.models.AccountDetail
import com.algorand.android.models.AccountIconResource
import com.algorand.android.repository.AccountRepository
import com.algorand.android.utils.CacheResult
import com.algorand.android.utils.exceptions.AccountNotFoundException
import com.algorand.android.utils.isRekeyedToAnotherAccount
import com.algorand.android.utils.recordException
import com.algorand.android.utils.toShortenedAddress
import java.math.BigInteger
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull

class AccountDetailUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    private val accountInformationUseCase: AccountInformationUseCase,
    private val accountManager: AccountManager
) : BaseUseCase() {

    fun getAccountDetailCacheFlow() = accountRepository.getAccountDetailCacheFlow()

    fun getAccountDetailCacheFlow(publicKey: String): Flow<CacheResult<AccountDetail>?> {
        return accountRepository.getAccountDetailCacheFlow()
            .mapNotNull { it.getOrDefault(publicKey, null) }
            .distinctUntilChanged()
    }

    fun getCachedAccountDetails() = getAccountDetailCacheFlow().value.values

    fun getCachedAccountDetail(publicKey: String): CacheResult<AccountDetail>? {
        return accountRepository.getCachedAccountDetail(publicKey)
    }

    suspend fun fetchAndCacheAccountDetail(
        accountAddress: String,
        scope: CoroutineScope
    ): Flow<CacheResult<AccountDetail>> = flow {
        accountInformationUseCase.getAccountInformationAndFetchAssets(accountAddress, scope).use(
            onSuccess = { accountInformation ->
                val localAccount = accountManager.getAccount(accountAddress) ?: run {
                    emit(CacheResult.Error.create(AccountNotFoundException()))
                    recordException(AccountNotFoundException())
                    return@use
                }
                val cacheResult = CacheResult.Success.create(AccountDetail(localAccount, accountInformation))
                accountRepository.cacheAccountDetail(cacheResult)
                emit(cacheResult)
            },
            onFailed = { exception, code ->
                emit(CacheResult.Error.create(exception, code))
            }
        )
    }

    suspend fun clearAccountDetailCache() {
        accountRepository.clearAccountDetailCache()
    }

    fun isAssetOwnedByAccount(publicKey: String, assetId: Long): Boolean {
        return getCachedAccountDetail(publicKey)?.data?.accountInformation?.getAllAssetIds()?.contains(assetId) ?: false
    }

    fun isAssetOwnedByAnyAccount(assetId: Long): Boolean {
        return getCachedAccountDetails().any {
            it.data?.accountInformation?.getAllAssetIds()?.contains(assetId) ?: false
        }
    }

    fun getCachedAccountAlgoAmount(publicKey: String): BigInteger? {
        return accountRepository.getCachedAccountDetail(publicKey)?.data?.accountInformation?.amount
    }

    fun canAccountSignTransaction(publicKey: String): Boolean {
        val account = accountManager.getAccount(publicKey)
        return when (account?.type) {
            Account.Type.LEDGER, Account.Type.REKEYED_AUTH -> true
            Account.Type.STANDARD -> (account.getSecretKey() ?: byteArrayOf()).isNotEmpty()
            Account.Type.REKEYED -> isAuthAccountInDevice(account.address)
            Account.Type.WATCH, null -> false
        }
    }

    fun isAuthAccountInDevice(accountAddress: String): Boolean {
        val accountAuthAddress = getAuthAddress(accountAddress) ?: return false
        val authAccountDetail = getCachedAccountDetail(accountAuthAddress)?.data ?: return false
        return canAccountSignTransaction(authAccountDetail.account.address)
    }

    fun getAccountType(publicKey: String): Account.Type? {
        return accountManager.getAccount(publicKey)?.type
    }

    fun getAccount(publicKey: String): Account? {
        return accountManager.getAccount(publicKey)
    }

    fun getAccountName(publicKey: String): String {
        val account = accountRepository.getCachedAccountDetail(publicKey)?.data?.account
        val accountName = account?.name
        val accountAddress = account?.address
        return accountName?.ifEmpty { accountAddress.toShortenedAddress() }.orEmpty()
    }

    fun getAuthAddress(publicKey: String): String? {
        val accountInformation = accountRepository.getCachedAccountDetail(publicKey)?.data?.accountInformation
        return accountInformation?.rekeyAdminAddress
    }

    fun getAccountIcon(publicKey: String): AccountIconResource {
        return AccountIconResource.getAccountIconResourceByAccountType(accountManager.getAccount(publicKey)?.type)
    }

    fun isAccountRekeyed(publicKey: String): Boolean {
        val authAddress = accountRepository.getCachedAccountDetail(publicKey)
            ?.data
            ?.accountInformation
            ?.rekeyAdminAddress
        return isRekeyedToAnotherAccount(authAddress, publicKey)
    }

    fun isThereAnyAccountWithPublicKey(publicKey: String): Boolean {
        return accountManager.isThereAnyAccountWithPublicKey(publicKey)
    }

    fun isAccountCachedSuccessfully(accountAddress: String): Boolean {
        return accountRepository.getCachedAccountDetail(accountAddress) is CacheResult.Success
    }

    fun setAccountNameService(accountAddress: String, nameServiceName: String?) {
        accountRepository.getCachedAccountDetail(accountAddress)?.data?.nameServiceName = nameServiceName
    }

    fun getAuthAccount(accountAddress: String?): CacheResult<AccountDetail>? {
        val authAccountAddress = getAuthAddress(accountAddress ?: return null) ?: return null
        return getCachedAccountDetail(authAccountAddress)
    }

    fun hasAccountAnyRekeyedAccount(accountAddress: String): Boolean {
        return getCachedAccountDetails().any { accountDetail ->
            val cachedAccountAddress = accountDetail.data?.account?.address ?: return@any false
            val authAccountDetail = getAuthAccount(cachedAccountAddress)?.data
            val authAccountAddress = authAccountDetail?.account?.address
            authAccountAddress == accountAddress && getAccountType(cachedAccountAddress) != Account.Type.WATCH
        }
    }
}
