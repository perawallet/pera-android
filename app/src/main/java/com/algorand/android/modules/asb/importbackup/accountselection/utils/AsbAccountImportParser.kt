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

package com.algorand.android.modules.asb.importbackup.accountselection.utils

import com.algorand.android.module.account.local.domain.usecase.IsThereAnyAccountWithAddress
import com.algorand.android.modules.algosdk.cryptoutil.domain.usecase.IsAccountAddressMatchWithSecretKeyUseCase
import com.algorand.android.modules.asb.importbackup.accountselection.ui.mapper.AsbAccountImportResultMapper
import com.algorand.android.modules.asb.importbackup.accountselection.ui.model.AsbAccountImportResult
import com.algorand.android.modules.asb.importbackup.enterkey.ui.model.RestoredAccount
import com.algorand.android.utils.isValidAddress
import javax.inject.Inject

class AsbAccountImportParser @Inject constructor(
    private val isThereAnyAccountWithAddress: IsThereAnyAccountWithAddress,
    private val asbAccountImportResultMapper: AsbAccountImportResultMapper,
    private val isAccountAddressMatchWithSecretKeyUseCase: IsAccountAddressMatchWithSecretKeyUseCase
) {

    suspend fun parseAsbImportedAccounts(
        accountImportMap: List<Pair<String, RestoredAccount>>,
        unsupportedAccounts: List<RestoredAccount>?
    ): AsbAccountImportResult {
        val importedAccountList = mutableListOf<String>()
        val existingAccountList = mutableListOf<String>()
        val unsupportedAccountList = unsupportedAccounts?.map { it.address }.orEmpty()
        accountImportMap.forEach { (accountAddress, _) ->
            val isAccountAlreadyExist = isThereAnyAccountWithAddress(accountAddress)
            if (isAccountAlreadyExist) {
                existingAccountList.add(accountAddress)
                return@forEach
            }

            importedAccountList.add(accountAddress)
        }
        return asbAccountImportResultMapper.mapToAsbAccountImportResult(
            importedAccountList = importedAccountList,
            existingAccountList = existingAccountList,
            unsupportedAccountList = unsupportedAccountList
        )
    }

    suspend fun isAccountSupported(restoredAccount: RestoredAccount): Boolean {
        if (restoredAccount.secretKey.isEmpty()) {
            return false
        }

        val isSecretKeyValid = isAccountAddressMatchWithSecretKeyUseCase.invoke(
            accountAddress = restoredAccount.address,
            secretKey = restoredAccount.secretKey
        )
        if (!isSecretKeyValid) {
            return false
        }

        return restoredAccount.address.isValidAddress()
    }
}
