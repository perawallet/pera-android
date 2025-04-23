/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.accountcore.ui.usecase

import android.content.res.Resources
import com.algorand.android.R
import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accounts.lite.domain.model.AccountLite
import com.algorand.android.utils.toShortenedAddress
import com.algorand.wallet.account.custom.domain.usecase.GetAccountCustomInfoOrNull
import com.algorand.wallet.account.detail.domain.model.AccountDetail
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.account.detail.domain.usecase.GetAccountDetail
import com.algorand.wallet.nameservice.domain.model.NameService
import com.algorand.wallet.nameservice.domain.usecase.GetAccountNameService
import javax.inject.Inject

/**
 *
 * Account Naming Rule;
 *
 * If account is renamed;
 *  - If account matches with NFDomain;
 *      + Primary display name: Given custom name (Spending Account)
 *      + Secondary display name: NFDomain service name (pera.algo)
 *  - If account doesn't match with NFDomain;
 *      + Primary display name: Given custom name (Spending Account)
 *      + Secondary display name: Shortened account address: (XCSASD...ZSFFSW)
 * If account didn't rename;
 *  - If account matches with NFDomain;
 *      + Primary display name: NFDomain service name (pera.algo)
 *      + Secondary display name: Shortened account address: (XCSASD...ZSFFSW)
 *  - If account doesn't match with NFDomain;
 *      + Primary display name: Shortened account address: (XCSASD...ZSFFSW)
 *      + Secondary display name:
 *          - If Ledger account: Ledger Account
 *          - If Rekeyed account: Rekeyed Account
 *          - If Watch account: Watch Account
 *          - else: empty field
 */
internal class GetAccountDisplayNameUseCase @Inject constructor(
    private val getCustomInfoOrNull: GetAccountCustomInfoOrNull,
    private val getAccountDetail: GetAccountDetail,
    private val resources: Resources,
    private val getAccountNameService: GetAccountNameService
) : GetAccountDisplayName {

    override suspend fun invoke(address: String): AccountDisplayName {
        val customAccountName = getCustomInfoOrNull(address)?.customName
            ?: return getAccountDisplayNameWithAccountAddressOnly(address)
        val nameService = getAccountNameService(address)
        return AccountDisplayName(
            accountAddress = address,
            primaryDisplayName = getPrimaryName(address, customAccountName, nameService),
            secondaryDisplayName = getSecondaryName(address, customAccountName, nameService)
        )
    }

    override suspend fun invoke(address: String, name: String?, type: AccountType?): AccountDisplayName {
        if (name.isNullOrBlank() || type == null) return getAccountDisplayNameWithAccountAddressOnly(address)
        return AccountDisplayName(
            accountAddress = address,
            primaryDisplayName = name,
            secondaryDisplayName = getAccountTypeName(type)
        )
    }

    override suspend fun invoke(accountDetail: AccountDetail): AccountDisplayName {
        val nameService = getAccountNameService(accountDetail.address)
        return with(accountDetail) {
            AccountDisplayName(
                accountAddress = address,
                primaryDisplayName = getPrimaryName(address, customAccountInfo?.customName, nameService),
                secondaryDisplayName = getSecondaryName(address, customAccountInfo?.customName, nameService)
            )
        }
    }

    override suspend fun invoke(accountLite: AccountLite): AccountDisplayName {
        val nameService = getAccountNameService(accountLite.address)
        return with(accountLite) {
            AccountDisplayName(
                accountAddress = address,
                primaryDisplayName = getPrimaryName(address, customName, nameService),
                secondaryDisplayName = getSecondaryName(address, customName, nameService)
            )
        }
    }

    private fun getPrimaryName(address: String, customAccountName: String?, nameService: NameService?): String {
        val isAccountRenamed = customAccountName != address.toShortenedAddress() && !customAccountName.isNullOrBlank()
        val nameServiceName = nameService?.nameServiceName.orEmpty()
        val isAccountMatchedNfDomain = nameServiceName.isNotBlank()
        return when {
            isAccountRenamed -> customAccountName.orEmpty()
            isAccountMatchedNfDomain -> nameServiceName
            else -> address.toShortenedAddress()
        }
    }

    private suspend fun getSecondaryName(
        address: String,
        customAccountName: String?,
        nameService: NameService?
    ): String? {
        val isAccountRenamed = customAccountName != address.toShortenedAddress() && !customAccountName.isNullOrBlank()
        val nameServiceName = nameService?.nameServiceName.orEmpty()
        val isAccountMatchedNfDomain = nameServiceName.isNotBlank()
        return when {
            isAccountRenamed && isAccountMatchedNfDomain -> nameServiceName
            isAccountRenamed || isAccountMatchedNfDomain -> address.toShortenedAddress()
            else -> getAccountTypeName(address)
        }
    }

    private suspend fun getAccountTypeName(address: String): String? {
        val accountType = getAccountDetail(address).accountType
        return getAccountTypeName(accountType)
    }

    private fun getAccountTypeName(type: AccountType?): String? {
        return when (type) {
            AccountType.LedgerBle -> R.string.ledger_account
            AccountType.Rekeyed, AccountType.RekeyedAuth -> R.string.rekeyed_account
            AccountType.NoAuth -> R.string.watch_account
            AccountType.HdKey -> R.string.hd_account
            else -> null
        }?.run { resources.getString(this) }
    }

    private fun getAccountDisplayNameWithAccountAddressOnly(address: String): AccountDisplayName {
        return AccountDisplayName(
            accountAddress = address,
            primaryDisplayName = address.toShortenedAddress(),
            secondaryDisplayName = address.toShortenedAddress()
        )
    }
}
