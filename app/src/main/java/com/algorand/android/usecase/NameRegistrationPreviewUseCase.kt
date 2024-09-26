/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License
 *
 */

package com.algorand.android.usecase

import com.algorand.android.module.account.local.domain.usecase.IsThereAnyAccountWithAddress
import com.algorand.android.core.BaseUseCase
import com.algorand.android.module.account.core.component.detail.domain.model.AccountType
import com.algorand.android.module.account.core.component.detail.domain.usecase.GetAccountDetail
import com.algorand.android.module.account.core.component.domain.usecase.UpdateAccountName
import com.algorand.android.mapper.NameRegistrationPreviewMapper
import com.algorand.android.models.CreateAccount
import com.algorand.android.models.ui.NameRegistrationPreview
import com.algorand.android.utils.analytics.CreationType
import com.algorand.android.utils.toShortenedAddress
import javax.inject.Inject

class NameRegistrationPreviewUseCase @Inject constructor(
    private val updateAccountName: UpdateAccountName,
    private val nameRegistrationPreviewMapper: NameRegistrationPreviewMapper,
    private val accountAdditionUseCase: AccountAdditionUseCase,
    private val isThereAnyAccountWithAddress: IsThereAnyAccountWithAddress,
    private val getAccountDetail: GetAccountDetail
) : BaseUseCase() {

    fun getInitialPreview(): NameRegistrationPreview {
        return nameRegistrationPreviewMapper.mapToInitialPreview()
    }

    suspend fun getPreviewWithAccountCreation(
        accountCreation: CreateAccount,
        inputName: String
    ): NameRegistrationPreview {
        val address = accountCreation.address
        val accountName = inputName.ifBlank { address.toShortenedAddress() }
        accountCreation.customName = accountName
        val doesAccountAlreadyExists = isThereAnyAccountWithAddress(address)
        if (doesAccountAlreadyExists.not()) {
            return nameRegistrationPreviewMapper.mapToCreateAccountPreview(accountCreation)
        }
        if (shouldUpdateWatchAccountEvent(address, accountCreation.creationType)) {
            return nameRegistrationPreviewMapper.mapToUpdateWatchAccountPreview(accountCreation)
        }
        return nameRegistrationPreviewMapper.mapToAccountAlreadyExistsPreview()
    }

    suspend fun updateTypeOfWatchAccount(accountCreation: CreateAccount) {
        accountAdditionUseCase.updateTypeOfWatchAccount(accountCreation)
    }

    suspend fun updateNameOfWatchAccount(accountCreation: CreateAccount) {
        updateAccountName(accountCreation.address, accountCreation.customName)
    }

    fun getOnWatchAccountUpdatedPreview(): NameRegistrationPreview {
        return nameRegistrationPreviewMapper.mapToWatchAccountUpdatedPreview()
    }

    suspend fun addNewAccount(createAccount: CreateAccount) {
        accountAdditionUseCase.addNewAccount(createAccount)
    }

    private suspend fun isThereAWatchAccountWithThisAddress(address: String): Boolean {
        return getAccountDetail(address).accountType == AccountType.NoAuth
    }

    private suspend fun shouldUpdateWatchAccountEvent(address: String, creationType: CreationType): Boolean {
        val doesAccountExistAsWatchAccount = isThereAWatchAccountWithThisAddress(address)
        return doesAccountExistAsWatchAccount && creationType != CreationType.WATCH
    }
}
