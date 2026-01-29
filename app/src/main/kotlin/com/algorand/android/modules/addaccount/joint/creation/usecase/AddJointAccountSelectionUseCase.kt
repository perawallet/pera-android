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

package com.algorand.android.modules.addaccount.joint.creation.usecase

import com.algorand.android.models.BaseAccountSelectionListItem.BaseAccountItem.AccountItem
import com.algorand.android.models.BaseAccountSelectionListItem.BaseAccountItem.ContactItem
import com.algorand.android.modules.accountcore.ui.accountselection.usecase.GetAccountSelectionAccountItems
import com.algorand.android.modules.accountcore.ui.accountselection.usecase.GetAccountSelectionContactItems
import com.algorand.android.modules.accountcore.ui.accountselection.usecase.GetAccountSelectionNameServiceItems
import com.algorand.android.modules.addaccount.joint.creation.mapper.JointAccountSelectionListItemMapper
import com.algorand.android.modules.addaccount.joint.creation.model.JointAccountSelectionListItem
import com.algorand.android.utils.isValidAddress
import com.algorand.android.utils.isValidNFTDomain
import com.algorand.android.utils.toShortenedAddress
import com.algorand.wallet.account.detail.domain.model.AccountType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AddJointAccountSelectionUseCase @Inject constructor(
    private val getAccountSelectionAccountItems: GetAccountSelectionAccountItems,
    private val getAccountSelectionContactItems: GetAccountSelectionContactItems,
    private val getAccountSelectionNameServiceItems: GetAccountSelectionNameServiceItems,
    private val jointAccountSelectionListItemMapper: JointAccountSelectionListItemMapper
) {

    fun getAccountSelectionList(
        query: String,
    ): Flow<List<JointAccountSelectionListItem>> {
        val accountList = fetchAccountList(query)
        val contactList = fetchContactList(query)
        val trimmedQuery = query.trim()
        val nfdList = if (trimmedQuery.lowercase().isValidNFTDomain()) {
            fetchNfdList(trimmedQuery)
        } else {
            flow { emit(emptyList()) }
        }
        val externalAddressList = fetchExternalAddressIfValid(query)
        return combine(accountList, contactList, nfdList, externalAddressList) { accounts, contacts, nfds, external ->
            val allAddresses = (
                accounts.map { it.address } +
                    contacts.map { it.address } +
                    nfds.map { it.address }
                ).toSet()
            val filteredExternal = external.filter { it.address !in allAddresses }

            buildList {
                addAll(filteredExternal)
                addAll(nfds)
                addAll(accounts)
                addAll(contacts)
            }
        }
    }

    private fun fetchAccountList(query: String) = flow {
        val accounts = getAccountSelectionAccountItems(
            showHoldings = true,
            showFailedAccounts = false
        )
        val filteredAccounts = accounts
            .filterIsInstance<AccountItem>()
            .filter { accountItem ->
                val accountType = accountItem.accountListItem.itemConfiguration.accountType
                accountType !is AccountType.Joint
            }
            .filter { accountItem ->
                val displayName = accountItem.displayName
                val address = accountItem.address
                (displayName.contains(query, ignoreCase = true) ||
                        address.contains(query, ignoreCase = true))
            }
            .map { accountItem ->
                jointAccountSelectionListItemMapper.mapToAccountItem(accountItem)
            }
        emit(filteredAccounts)
    }

    private fun fetchContactList(query: String) = flow {
        val contacts = getAccountSelectionContactItems()
        val filteredContacts = contacts
            .filterIsInstance<ContactItem>()
            .filter { contactItem ->
                val displayName = contactItem.displayName
                val address = contactItem.address
                (displayName.contains(query, ignoreCase = true) ||
                        address.contains(query, ignoreCase = true))
            }
            .map { contactItem ->
                jointAccountSelectionListItemMapper.mapToContactItem(contactItem)
            }
        emit(filteredContacts)
    }

    private fun fetchNfdList(query: String) = flow {
        val nfdAccounts = getAccountSelectionNameServiceItems(query)
        val filteredNfds = nfdAccounts.map { nfdItem ->
            jointAccountSelectionListItemMapper.mapToNfdItem(nfdItem)
        }
        emit(filteredNfds)
    }

    private fun fetchExternalAddressIfValid(query: String) = flow {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isValidAddress()) {
            val externalAddressItem = jointAccountSelectionListItemMapper.mapToExternalAddressItem(
                address = trimmedQuery,
                shortenedAddress = trimmedQuery.toShortenedAddress()
            )
            emit(listOf(externalAddressItem))
        } else {
            emit(emptyList())
        }
    }
}
