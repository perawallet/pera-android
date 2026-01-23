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

import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.repository.Algo25AccountRepository
import com.algorand.wallet.account.local.domain.repository.HdKeyAccountRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class GetSignableAccountsByAddressesUseCase @Inject constructor(
    private val hdKeyAccountRepository: HdKeyAccountRepository,
    private val algo25AccountRepository: Algo25AccountRepository,
    private val dispatcher: CoroutineDispatcher
) : GetSignableAccountsByAddresses {

    override suspend fun invoke(addresses: List<String>): List<LocalAccount> {
        if (addresses.isEmpty()) return emptyList()

        return withContext(dispatcher) {
            val deferredHdKeyAccounts = async { hdKeyAccountRepository.getAccountsByAddresses(addresses) }
            val deferredAlgo25Accounts = async { algo25AccountRepository.getAccountsByAddresses(addresses) }
            awaitAll(deferredHdKeyAccounts, deferredAlgo25Accounts).flatten()
        }
    }
}
