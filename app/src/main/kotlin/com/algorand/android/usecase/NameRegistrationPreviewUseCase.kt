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

import com.algorand.android.core.BaseUseCase
import com.algorand.android.mapper.NameRegistrationPreviewMapper
import com.algorand.android.models.AccountCreation
import com.algorand.android.models.ui.NameRegistrationPreview
import com.algorand.android.utils.analytics.CreationType
import com.algorand.android.utils.toShortenedAddress
import com.algorand.wallet.account.custom.domain.usecase.SetAccountCustomName
import com.algorand.wallet.account.local.domain.usecase.IsThereAnyAccountWithAddress
import com.algorand.wallet.account.local.domain.usecase.IsThereAnyNoAuthAccountWithAddress
import javax.inject.Inject

class NameRegistrationPreviewUseCase @Inject constructor(
    private val nameRegistrationPreviewMapper: NameRegistrationPreviewMapper,
    private val accountAdditionUseCase: AccountAdditionUseCase,
    private val setAccountCustomName: SetAccountCustomName,
    private val isThereAnyAccountWithAddress: IsThereAnyAccountWithAddress,
    private val isThereAnyNoAuthAccountWithAddress: IsThereAnyNoAuthAccountWithAddress
) : BaseUseCase() {

    fun getInitialPreview(): NameRegistrationPreview {
        return nameRegistrationPreviewMapper.mapToInitialPreview()
    }

    fun getInitialPreviewWithHdWalletData(walletId: Int): NameRegistrationPreview {
        return nameRegistrationPreviewMapper.mapToInitialPreview(
            walletId
        )
    }

    suspend fun getPreviewWithAccountCreation(
        accountCreation: AccountCreation?,
        inputName: String,
        walletId: Int?
    ): NameRegistrationPreview? {
        if (accountCreation == null) return null
        val address = accountCreation.address
        val accountName = inputName.ifBlank { address.toShortenedAddress() }
        accountCreation.customName = accountName
        val doesAccountAlreadyExists = isThereAnyAccountWithAddress(address)
        if (doesAccountAlreadyExists.not()) {
            return nameRegistrationPreviewMapper.mapToCreateAccountPreview(
                accountCreation,
                walletId
            )
        }
        if (shouldUpdateWatchAccountEvent(address, accountCreation.creationType)) {
            return nameRegistrationPreviewMapper.mapToUpdateWatchAccountPreview(accountCreation)
        }
        return nameRegistrationPreviewMapper.mapToAccountAlreadyExistsPreview()
    }

    suspend fun updateTypeOfWatchAccount(accountCreation: AccountCreation) {
        accountAdditionUseCase.updateTypeOfWatchAccount(accountCreation.toCreateAccount())
    }

    suspend fun updateNameOfWatchAccount(accountCreation: AccountCreation) {
        if (!accountCreation.customName.isNullOrBlank()) {
            setAccountCustomName(accountCreation.address, accountCreation.customName.orEmpty())
        }
    }

    fun getOnWatchAccountUpdatedPreview(): NameRegistrationPreview {
        return nameRegistrationPreviewMapper.mapToWatchAccountUpdatedPreview()
    }

    suspend fun addNewAccount(accountCreation: AccountCreation) {
        accountAdditionUseCase.addNewAccount(accountCreation)
    }

    private suspend fun shouldUpdateWatchAccountEvent(address: String, creationType: CreationType): Boolean {
        val doesAccountExistAsWatchAccount = isThereAnyNoAuthAccountWithAddress(address)
        return doesAccountExistAsWatchAccount && creationType != CreationType.WATCH
    }
}
