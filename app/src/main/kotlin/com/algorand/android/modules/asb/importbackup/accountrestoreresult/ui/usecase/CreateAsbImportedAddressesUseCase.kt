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

package com.algorand.android.modules.asb.importbackup.accountrestoreresult.ui.usecase

import com.algorand.android.models.AccountCreation
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreviewByType
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.android.modules.asb.importbackup.accountselection.ui.model.AsbImportedAddress
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.account.info.domain.model.AccountInformation
import com.algorand.wallet.account.info.domain.usecase.FetchAccountInformationWithoutAssets
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope

internal class CreateAsbImportedAddressesUseCase @Inject constructor(
    private val fetchAccountInformationWithoutAssets: FetchAccountInformationWithoutAssets,
    private val getAccountIconDrawablePreviewByType: GetAccountIconDrawablePreviewByType
) : CreateAsbImportedAddresses {

    override suspend fun invoke(accountCreations: List<AccountCreation>): List<AsbImportedAddress> {
        return supervisorScope {
            accountCreations.map { creation ->
                async { getAccountItemFromAccountInfo(creation) }
            }.awaitAll()
        }
    }

    private suspend fun getAccountItemFromAccountInfo(creation: AccountCreation): AsbImportedAddress {
        return fetchAccountInformationWithoutAssets(creation.address, includeDeletedAccount = false).use(
            onSuccess = { accountInfo ->
                AsbImportedAddress(creation.address, creation.type, getDrawableByType(accountInfo, creation))
            },
            onFailed = { _, _ ->
                getAccountItemWithRestoreTypeDrawable(creation)
            }
        )
    }

    private fun getDrawableByType(
        accountInfo: AccountInformation,
        accountCreation: AccountCreation
    ): AccountIconDrawablePreview {
        if (accountInfo.isRekeyed()) {
            return getAccountIconDrawablePreviewByType(AccountType.Rekeyed)
        }
        val accountType = accountCreation.type.toAccountType()
        return getAccountIconDrawablePreviewByType(accountType)
    }

    private fun getAccountItemWithRestoreTypeDrawable(creation: AccountCreation): AsbImportedAddress {
        val accountType = creation.type.toAccountType()
        return AsbImportedAddress(
            address = creation.address,
            creationType = creation.type,
            accountIconDrawablePreview = getAccountIconDrawablePreviewByType(accountType)
        )
    }

    private fun AccountCreation.Type.toAccountType(): AccountType {
        return when (this) {
            is AccountCreation.Type.Algo25 -> AccountType.Algo25
            is AccountCreation.Type.NoAuth -> AccountType.NoAuth
            is AccountCreation.Type.HdKey -> AccountType.HdKey
            is AccountCreation.Type.LedgerBle -> AccountType.LedgerBle
        }
    }
}
