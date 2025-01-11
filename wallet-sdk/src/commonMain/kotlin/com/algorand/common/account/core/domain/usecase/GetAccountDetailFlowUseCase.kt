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

package com.algorand.common.account.core.domain.usecase

import com.algorand.common.account.custom.domain.usecase.GetAccountCustomInfoOrNull
import com.algorand.common.account.detail.domain.model.AccountDetail
import com.algorand.common.account.detail.domain.usecase.GetAccountRegistrationType
import com.algorand.common.account.detail.domain.usecase.GetAccountType
import com.algorand.common.account.info.domain.usecase.GetAccountInformationFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class GetAccountDetailFlowUseCase(
    private val getAccountInformationFlow: GetAccountInformationFlow,
    private val getAccountCustomInfoOrNull: GetAccountCustomInfoOrNull,
    private val getAccountType: GetAccountType,
    private val getAccountRegistrationType: GetAccountRegistrationType,
) : GetAccountDetailFlow {

    override fun invoke(address: String): Flow<AccountDetail?> {
        return getAccountInformationFlow(address).map {
            if (it == null) return@map null
            AccountDetail(
                address = address,
                customInfo = getAccountCustomInfoOrNull(address),
                accountRegistrationType = getAccountRegistrationType(address),
                accountType = getAccountType(address),
            )
        }
    }
}
