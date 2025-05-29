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

package com.algorand.wallet.account.info.domain.usecase

import com.algorand.wallet.account.info.domain.mapper.RegisteredHdKeyMapper
import com.algorand.wallet.account.info.domain.model.ActiveHdAccount
import com.algorand.wallet.account.info.domain.model.RegisteredHdKey
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccountsAddresses
import com.algorand.wallet.algosdk.transaction.sdk.PeraBip39Sdk
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope

internal class GetRegisteredHdKeysUseCase @Inject constructor(
    private val getLocalAccountsAddresses: GetLocalAccountsAddresses,
    private val getActiveHdAccounts: GetActiveHdAccounts,
    private val getActiveHdAccountAddresses: GetActiveHdAccountAddresses,
    private val registeredHdKeyMapper: RegisteredHdKeyMapper,
    private val peraBip39Sdk: PeraBip39Sdk,
) : GetRegisteredHdKeys {

    override suspend fun invoke(entropy: ByteArray): List<RegisteredHdKey> {
        val localAccountAddresses = getLocalAccountsAddresses()
        val activeHdAccounts = getActiveHdAccounts(entropy)

        if (activeHdAccounts.isEmpty()) {
            return getFirstAccountFirstAddress(entropy, localAccountAddresses)
        }

        val activeHdAccountAddresses = supervisorScope {
            activeHdAccounts.map { activeHdAccount ->
                async {
                    getActiveHdAccountAddresses(activeHdAccount)
                }
            }.awaitAll().flatten()
        }

        return activeHdAccountAddresses.mapNotNull { hdAccountAddress ->
            if (hdAccountAddress.fastLookup == null || !hdAccountAddress.fastLookup.accountExists) {
                return@mapNotNull null
            }
            val isAlreadyImported = localAccountAddresses.contains(hdAccountAddress.address)
            registeredHdKeyMapper(hdAccountAddress, hdAccountAddress.fastLookup, isAlreadyImported)
        }.ifEmpty {
            getFirstAccountFirstAddress(entropy, localAccountAddresses)
        }
    }

    private fun getFirstAccountFirstAddress(
        entropy: ByteArray,
        localAccountAddresses: List<String>
    ): List<RegisteredHdKey> {
        val address = peraBip39Sdk.generateHdKeyAddress(entropy, 0, 0, 0)
        val hdAccountAddress = ActiveHdAccount.HdAccountAddress(address, 0, 0, 0, null)
        val isAlreadyImported = localAccountAddresses.contains(address)
        return listOf(registeredHdKeyMapper(hdAccountAddress, null, isAlreadyImported))
    }
}
