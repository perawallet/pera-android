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
import javax.inject.Inject

class AddJointAccountSelectionUseCase @Inject constructor(
    private val getAccountSelectionAccountItems: GetAccountSelectionAccountItems,
    private val getAccountSelectionContactItems: GetAccountSelectionContactItems,
    private val getAccountSelectionNameServiceItems: GetAccountSelectionNameServiceItems,
    private val jointAccountSelectionListItemMapper: JointAccountSelectionListItemMapper
) {

    suspend fun getAccountSelectionList(
        query: String,
    ): List<JointAccountSelectionListItem> {
        val accountList = fetchAccountList(query)
        val contactList = fetchContactList(query)
        val trimmedQuery = query.trim()
        val nfdList = if (trimmedQuery.lowercase().isValidNFTDomain()) {
            fetchNfdList(trimmedQuery)
        } else {
            emptyList()
        }
        val externalAddressList = fetchExternalAddressIfValid(query)

        val allAddresses = (
                accountList.map { it.address } +
                        contactList.map { it.address } +
                        nfdList.map { it.address }
                ).toSet()
        val filteredExternal = externalAddressList.filter { it.address !in allAddresses }

        return buildList {
            addAll(filteredExternal)
            addAll(nfdList)
            addAll(accountList)
            addAll(contactList)
        }
    }

    private suspend fun fetchAccountList(query: String): List<JointAccountSelectionListItem> {
        val accounts = getAccountSelectionAccountItems(
            showHoldings = true,
            showFailedAccounts = false
        )
        val filteredAccounts = accounts.mapNotNull { item ->
            val accountItem = item as? AccountItem ?: return@mapNotNull null
            val accountType = accountItem.accountListItem.itemConfiguration.accountType
            if (accountType is AccountType.Joint) return@mapNotNull null
            val displayName = accountItem.displayName
            val address = accountItem.address
            if (!displayName.contains(query, ignoreCase = true) &&
                !address.contains(query, ignoreCase = true)
            ) {
                return@mapNotNull null
            }
            jointAccountSelectionListItemMapper.mapToAccountItem(accountItem)
        }
        return filteredAccounts
    }

    private suspend fun fetchContactList(query: String): List<JointAccountSelectionListItem> {
        val contacts = getAccountSelectionContactItems()
        val filteredContacts = contacts.mapNotNull { item ->
            val contactItem = item as? ContactItem ?: return@mapNotNull null
            val displayName = contactItem.displayName
            val address = contactItem.address
            if (!displayName.contains(query, ignoreCase = true) &&
                !address.contains(query, ignoreCase = true)
            ) {
                return@mapNotNull null
            }
            jointAccountSelectionListItemMapper.mapToContactItem(contactItem)
        }
        return filteredContacts
    }

    private suspend fun fetchNfdList(query: String): List<JointAccountSelectionListItem> {
        val nfdAccounts = getAccountSelectionNameServiceItems(query)
        val filteredNfds = nfdAccounts.map { nfdItem ->
            jointAccountSelectionListItemMapper.mapToNfdItem(nfdItem)
        }
        return filteredNfds
    }

    private fun fetchExternalAddressIfValid(query: String): List<JointAccountSelectionListItem> {
        val trimmedQuery = query.trim()
        return if (trimmedQuery.isValidAddress()) {
            val externalAddressItem = jointAccountSelectionListItemMapper.mapToExternalAddressItem(
                address = trimmedQuery,
                shortenedAddress = trimmedQuery.toShortenedAddress()
            )
            listOf(externalAddressItem)
        } else {
            emptyList()
        }
    }
}
