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
import com.algorand.android.repository.AccountRepository
import com.algorand.android.utils.CacheResult
import com.algorand.android.utils.toShortenedAddress
import javax.inject.Inject

class AccountDetailUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    private val accountManager: AccountManager
) : BaseUseCase() {

    fun getCachedAccountDetail(publicKey: String): CacheResult<AccountDetail>? {
        return accountRepository.getCachedAccountDetail(publicKey)
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
}
