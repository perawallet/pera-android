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

package com.algorand.android.modules.accountcore.domain.usecase

import com.algorand.android.modules.accountcore.domain.model.AccountAssetData
import com.algorand.wallet.account.info.domain.usecase.GetAccountInformationFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class GetAccountAssetDataFlowUseCase @Inject constructor(
    private val createAccountAssetData: CreateAccountAssetData,
    private val getAccountInformationFlow: GetAccountInformationFlow,
) : GetAccountAssetDataFlow {

    override fun invoke(address: String, includeAlgo: Boolean): Flow<AccountAssetData> {
        return getAccountInformationFlow(address).map { accountInformation ->
            if (accountInformation == null) return@map AccountAssetData()
            createAccountAssetData(accountInformation, includeAlgo)
        }
    }
}
