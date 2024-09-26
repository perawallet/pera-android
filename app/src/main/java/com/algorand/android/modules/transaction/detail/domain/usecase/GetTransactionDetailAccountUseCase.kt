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

package com.algorand.android.modules.transaction.detail.domain.usecase

import androidx.annotation.StringRes
import androidx.core.net.toUri
import com.algorand.android.R
import com.algorand.android.module.account.local.domain.usecase.GetLocalAccounts
import com.algorand.android.module.account.core.ui.usecase.GetAccountDisplayName
import com.algorand.android.module.account.core.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.module.contacts.domain.usecase.GetContactByAddress
import com.algorand.android.modules.transaction.detail.ui.mapper.TransactionDetailItemMapper
import com.algorand.android.modules.transaction.detail.ui.model.TransactionDetailItem
import com.algorand.android.utils.toShortenedAddress
import javax.inject.Inject

class GetTransactionDetailAccountUseCase @Inject constructor(
    private val transactionDetailItemMapper: TransactionDetailItemMapper,
    private val getContactByAddress: GetContactByAddress,
    private val getLocalAccounts: GetLocalAccounts,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview
) {

    suspend fun getTransactionToAccount(publicKey: String): TransactionDetailItem.StandardTransactionItem.AccountItem {
        val foundAccount = getAccountFromLocalAccountsByPublicKey(publicKey, R.string.to)
        if (foundAccount != null) return foundAccount

        val foundContact = getAccountFromContactByPublicKey(publicKey, R.string.to)
        if (foundContact != null) return foundContact

        return createNormalAccountItem(publicKey, R.string.to)
    }

    suspend fun getTransactionFromAccount(
        publicKey: String,
        showToolTipView: Boolean
    ): TransactionDetailItem.StandardTransactionItem.AccountItem {
        val foundAccount = getAccountFromLocalAccountsByPublicKey(publicKey, R.string.from, showToolTipView)
        if (foundAccount != null) return foundAccount

        val foundContact = getAccountFromContactByPublicKey(publicKey, R.string.from)
        if (foundContact != null) return foundContact

        return createNormalAccountItem(
            publicKey = publicKey,
            labelTextResource = R.string.from,
            showToolTipView = showToolTipView
        )
    }

    suspend fun getTransactionCloseToAccount(
        publicKey: String
    ): TransactionDetailItem.StandardTransactionItem.AccountItem {
        val foundContact = getAccountFromContactByPublicKey(publicKey, R.string.close_to)
        if (foundContact != null) return foundContact

        val foundAccount = getAccountFromLocalAccountsByPublicKey(publicKey, R.string.close_to)
        if (foundAccount != null) return foundAccount

        return createNormalAccountItem(
            publicKey = publicKey,
            labelTextResource = R.string.close_to
        )
    }

    private suspend fun getAccountFromContactByPublicKey(
        publicKey: String,
        @StringRes labelTextResource: Int,
        showToolTipView: Boolean = false
    ): TransactionDetailItem.StandardTransactionItem.AccountItem.ContactItem? {
        val foundContact = getContactByAddress(publicKey)
        if (foundContact != null) {
            return transactionDetailItemMapper.mapToContactAccountItem(
                labelTextRes = labelTextResource,
                displayAddress = foundContact.name.ifBlank { foundContact.address.toShortenedAddress() },
                publicKey = foundContact.address,
                isAccountAdditionButtonVisible = false,
                isCopyButtonVisible = true,
                contactUri = foundContact.imageUri?.toUri(),
                showToolTipView = showToolTipView
            )
        }
        return null
    }

    private suspend fun getAccountFromLocalAccountsByPublicKey(
        publicKey: String,
        @StringRes labelTextResource: Int,
        showToolTipView: Boolean = false
    ): TransactionDetailItem.StandardTransactionItem.AccountItem.WalletItem? {
        val foundAccount = getLocalAccounts().firstOrNull { it.address == publicKey }
        if (foundAccount != null) {
            return transactionDetailItemMapper.mapToWalletAccountItem(
                labelTextRes = labelTextResource,
                displayAddress = getAccountDisplayName(publicKey).primaryDisplayName,
                publicKey = foundAccount.address,
                isCopyButtonVisible = true,
                isAccountAdditionButtonVisible = false,
                accountIconDrawablePreview = getAccountIconDrawablePreview(publicKey),
                showToolTipView = showToolTipView
            )
        }
        return null
    }

    private fun createNormalAccountItem(
        publicKey: String,
        @StringRes labelTextResource: Int,
        showToolTipView: Boolean = false
    ): TransactionDetailItem.StandardTransactionItem.AccountItem.NormalItem {
        return transactionDetailItemMapper.mapToNormalAccountItem(
            labelTextRes = labelTextResource,
            displayAddress = publicKey,
            publicKey = publicKey,
            isAccountAdditionButtonVisible = true,
            isCopyButtonVisible = true,
            showToolTipView = showToolTipView
        )
    }
}
