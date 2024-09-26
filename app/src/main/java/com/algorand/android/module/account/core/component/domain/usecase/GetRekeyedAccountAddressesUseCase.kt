/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.module.account.core.component.domain.usecase

import com.algorand.android.module.account.info.domain.usecase.GetAllAccountInformation
import com.algorand.android.module.account.core.component.detail.domain.model.AccountType
import com.algorand.android.module.account.core.component.detail.domain.usecase.GetAccountType
import javax.inject.Inject

internal class GetRekeyedAccountAddressesUseCase @Inject constructor(
    private val getAllAccountInformation: GetAllAccountInformation,
    private val getAccountType: GetAccountType
) : GetRekeyedAccountAddresses {

    override suspend fun invoke(address: String): List<String> {
        val allAccountInformation = getAllAccountInformation()
        return allAccountInformation.mapNotNull { accountInformation ->
            val cachedAccountAddress = accountInformation.key
            val authAccountDetail = accountInformation.value?.rekeyAdminAddress?.let {
                allAccountInformation[it]
            } ?: return@mapNotNull null
            if (authAccountDetail.address == address && getAccountType(cachedAccountAddress) != AccountType.NoAuth) {
                cachedAccountAddress
            } else {
                null
            }
        }
    }
}
