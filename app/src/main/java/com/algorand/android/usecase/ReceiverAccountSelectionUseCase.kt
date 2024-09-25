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

import com.algorand.android.R
import com.algorand.android.module.account.core.ui.accountselection.model.BaseAccountSelectionListItem
import com.algorand.android.module.account.core.ui.accountselection.usecase.GetAccountSelectionAccountItems
import com.algorand.android.module.account.core.ui.accountselection.usecase.GetAccountSelectionContactItems
import com.algorand.android.module.account.core.ui.accountselection.usecase.GetAccountSelectionItemsFromAccountAddress
import com.algorand.android.module.account.core.ui.accountselection.usecase.GetAccountSelectionNameServiceItems
import com.algorand.android.module.transaction.component.domain.model.ValidateReceiverAccountPayload
import com.algorand.android.module.transaction.component.domain.usecase.ValidateReceiverAccount
import com.algorand.android.module.transaction.ui.sendasset.model.AssetTransferTargetUser
import com.algorand.android.ui.send.receiveraccount.mapper.GetReceiverAccountSelectionValidationResult
import com.algorand.android.utils.Resource
import com.algorand.android.utils.isValidAddress
import java.math.BigInteger
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

class ReceiverAccountSelectionUseCase @Inject constructor(
    private val getAccountSelectionContactItems: GetAccountSelectionContactItems,
    private val getAccountSelectionNameServiceItems: GetAccountSelectionNameServiceItems,
    private val getAccountSelectionItemsFromAccountAddress: GetAccountSelectionItemsFromAccountAddress,
    private val getAccountSelectionAccountItems: GetAccountSelectionAccountItems,
    private val validateReceiverAccount: ValidateReceiverAccount,
    private val getReceiverAccountSelectionValidationResult: GetReceiverAccountSelectionValidationResult
) {

    fun getToAccountList(
        query: String,
        latestCopiedMessage: String?
    ): Flow<List<BaseAccountSelectionListItem>> {
        val contactList = fetchContactList(query)
        val accountList = fetchAccountList(query)
        val nftDomainAccountList = fetchNftDomainAccountList(query)
        val queriedAddress = query.takeIf { it.isValidAddress() }
        return combine(
            accountList,
            contactList,
            nftDomainAccountList
        ) { accounts, contacts, nftDomainAccounts ->
            mutableListOf<BaseAccountSelectionListItem>().apply {
                createPasteItem(latestCopiedMessage)?.run { add(this) }
                createQueriedAccountItem(
                    accountAddresses = accounts.map { it.address },
                    contactAddresses = contacts.map { it.address },
                    queriedAddress = queriedAddress
                )?.run {
                    add(BaseAccountSelectionListItem.HeaderItem(R.string.account))
                    add(this)
                }
                if (nftDomainAccounts.isNotEmpty()) {
                    add(BaseAccountSelectionListItem.HeaderItem(R.string.matched_accounts))
                    addAll(nftDomainAccounts)
                }
                if (accounts.isNotEmpty()) {
                    add(BaseAccountSelectionListItem.HeaderItem(R.string.my_accounts))
                    addAll(accounts)
                }
                if (contacts.isNotEmpty()) {
                    add(BaseAccountSelectionListItem.HeaderItem(R.string.contacts))
                    addAll(contacts)
                }
            }
        }
    }

    private fun createPasteItem(latestCopiedMessage: String?): BaseAccountSelectionListItem.PasteItem? {
        return latestCopiedMessage.takeIf { it.isValidAddress() }?.let { copiedAccount ->
            BaseAccountSelectionListItem.PasteItem(copiedAccount)
        }
    }

    private suspend fun createQueriedAccountItem(
        queriedAddress: String?,
        accountAddresses: List<String>,
        contactAddresses: List<String>
    ): BaseAccountSelectionListItem.BaseAccountItem.AccountItem? {
        if (queriedAddress.isNullOrBlank()) return null
        val shouldInsertQueriedAccount = shouldInsertQueriedAccount(
            accountAddresses = accountAddresses,
            contactAddresses = contactAddresses,
            queriedAccount = queriedAddress
        )
        if (!shouldInsertQueriedAccount) return null
        return getAccountSelectionItemsFromAccountAddress(accountAddress = queriedAddress)
    }

    private fun shouldInsertQueriedAccount(
        accountAddresses: List<String>,
        contactAddresses: List<String>,
        queriedAccount: String?
    ): Boolean {
        return !accountAddresses.contains(queriedAccount) && !contactAddresses.contains(queriedAccount)
    }

    private fun fetchContactList(query: String) = flow {
        val contacts = getAccountSelectionContactItems().filter {
            it.displayName.contains(query, true) || it.address.contains(query, true)
        }
        emit(contacts)
    }

    private fun fetchNftDomainAccountList(query: String) = flow {
        val nftDomainAccounts = getAccountSelectionNameServiceItems(query)
        emit(nftDomainAccounts)
    }

    private fun fetchAccountList(query: String) = flow {
        val localAccounts = getAccountSelectionAccountItems(
            showHoldings = false,
            showFailedAccounts = false
        ).filter { it.displayName.contains(query, true) || it.address.contains(query, true) }
        emit(localAccounts)
    }

    @SuppressWarnings("ReturnCount", "LongMethod")
    suspend fun checkToAccountTransactionRequirements(
        receiverAddress: String,
        assetId: Long,
        fromAccountAddress: String,
        amount: BigInteger,
        nftDomainAddress: String?,
        nftDomainServiceLogoUrl: String?
    ): Resource<AssetTransferTargetUser> {
        val validationPayload = ValidateReceiverAccountPayload(
            senderAddress = fromAccountAddress,
            receiverAddress = receiverAddress,
            assetId = assetId,
            amount = amount
        )
        val validationResult = validateReceiverAccount(validationPayload)

        return getReceiverAccountSelectionValidationResult(
            payload = validationPayload,
            validationResult = validationResult,
            nftDomainAddress = nftDomainAddress,
            nftDomainServiceLogoUrl = nftDomainServiceLogoUrl
        )
    }
}
