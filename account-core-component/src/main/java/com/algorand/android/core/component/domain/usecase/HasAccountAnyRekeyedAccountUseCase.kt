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

package com.algorand.android.core.component.domain.usecase

import com.algorand.android.accountinfo.component.domain.usecase.GetAllAccountInformation
import com.algorand.android.core.component.detail.domain.model.AccountType
import com.algorand.android.core.component.detail.domain.usecase.GetAccountDetail
import com.algorand.android.core.component.detail.domain.usecase.GetAccountType
import javax.inject.Inject

internal class HasAccountAnyRekeyedAccountUseCase @Inject constructor(
    private val getAllAccountInformation: GetAllAccountInformation,
    private val getAccountType: GetAccountType,
    private val getAccountDetail: GetAccountDetail
) : HasAccountAnyRekeyedAccount {

    override suspend fun invoke(address: String): Boolean {
        return getAllAccountInformation().values.filterNotNull().any {
            val cachedAccountAddress = it.address
            val authAccountDetail = it.rekeyAdminAddress?.let { getAccountDetail(it) } ?: return false
            val authAccountAddress = authAccountDetail.address
            authAccountAddress == address && getAccountType(cachedAccountAddress) != AccountType.NoAuth
        }
    }
}