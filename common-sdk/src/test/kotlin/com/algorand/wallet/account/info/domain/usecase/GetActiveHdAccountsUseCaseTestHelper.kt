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

import com.algorand.test.peraFixture
import com.algorand.wallet.account.info.domain.model.AccountFastLookup
import com.algorand.wallet.account.info.domain.model.ActiveHdAccount.HdAccountAddress
import com.algorand.wallet.account.info.domain.model.HdKeyDetail

internal class GetActiveHdAccountsUseCaseTestHelper(accountCount: Int, addressCount: Int) {

    private data class AccountIndex(val index: Int)

    private data class Address(val index: Int, val address: String)

    private val accounts: Map<AccountIndex, List<Address>>

    init {
        accounts = generateAccounts(accountCount, addressCount)
    }

    fun getAccountAddresses(accountIndex: Int): List<String> {
        return accounts[AccountIndex(accountIndex)]!!.map { it.address }
    }

    fun getAccountIndexAndAddressesPair(): List<Pair<Int, List<String>>> {
        return accounts.map { (account, addresses) ->
            account.index to addresses.map { it.address }
        }
    }

    fun getActiveAccountFastLookupResult(index: Int): Map<String, AccountFastLookup?> {
        return accounts[AccountIndex((index))]!!.associate { address ->
            address.address to peraFixture<AccountFastLookup>().copy(accountExists = true)
        }
    }

    fun getInactiveAccountFastLookupResult(index: Int): Map<String, AccountFastLookup?> {
        return accounts[AccountIndex(index)]!!.associate { address ->
            address.address to peraFixture<AccountFastLookup?>()?.copy(accountExists = false)
        }
    }

    fun getHdAccountAddresses(
        accountIndex: Int,
        fastLookups: List<AccountFastLookup?>
    ): List<HdAccountAddress> {
        return fastLookups.mapIndexed { index, accountFastLookup ->
            val address = getAccountAddresses(accountIndex)[index]
            HdAccountAddress(address, accountIndex, 0, index, accountFastLookup)
        }
    }

    fun getHdKeyDetails(accountIndex: Int): List<HdKeyDetail> {
        return accounts[AccountIndex(accountIndex)]!!.map { address ->
            HdKeyDetail(address.address, accountIndex, 0, keyIndex = address.index)
        }
    }

    private fun generateAccounts(accountCount: Int, addressCount: Int): Map<AccountIndex, List<Address>> {
        return (0 until accountCount).associate { accountIndex ->
            AccountIndex(accountIndex) to (0 until addressCount).map { addressIndex ->
                Address(addressIndex, "acc_${accountIndex}_addr_$addressIndex")
            }
        }
    }
}
