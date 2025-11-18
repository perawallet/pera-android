/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.wallet.account.local.domain.usecase

import com.algorand.wallet.account.detail.domain.model.AccountRegistrationType
import com.algorand.wallet.account.detail.domain.usecase.GetAccountRegistrationType
import com.algorand.wallet.account.local.domain.repository.Algo25AccountRepository
import com.algorand.wallet.account.local.domain.repository.HdKeyAccountRepository
import com.algorand.wallet.account.local.domain.repository.HdSeedRepository
import com.algorand.wallet.account.local.domain.repository.LedgerBleAccountRepository
import com.algorand.wallet.account.local.domain.repository.NoAuthAccountRepository
import javax.inject.Inject

internal class DeleteLocalAccountUseCase @Inject constructor(
    private val hdKeyAccountRepository: HdKeyAccountRepository,
    private val algo25AccountRepository: Algo25AccountRepository,
    private val noAuthAccountRepository: NoAuthAccountRepository,
    private val ledgerBleAccountRepository: LedgerBleAccountRepository,
    private val getAccountRegistrationType: GetAccountRegistrationType,
    private val hdSeedRepository: HdSeedRepository
) : DeleteLocalAccount {

    override suspend fun invoke(address: String) {
        val registrationType = getAccountRegistrationType(address)
        when (registrationType) {
            AccountRegistrationType.Algo25 -> algo25AccountRepository.deleteAccount(address)
            AccountRegistrationType.HdKey -> deleteHdKeyAccount(address)
            AccountRegistrationType.LedgerBle -> ledgerBleAccountRepository.deleteAccount(address)
            AccountRegistrationType.NoAuth -> noAuthAccountRepository.deleteAccount(address)
            null -> Unit
        }
    }

    private suspend fun deleteHdKeyAccount(address: String) {
        val hdKey = hdKeyAccountRepository.getAccount(address) ?: return
        hdKeyAccountRepository.deleteAccount(address)
        val derivedAddressesCount = hdKeyAccountRepository.getDerivedAddressCountOfSeed(hdKey.seedId)
        if (derivedAddressesCount == 0) {
            hdSeedRepository.deleteHdSeed(hdKey.seedId)
        }
    }
}
