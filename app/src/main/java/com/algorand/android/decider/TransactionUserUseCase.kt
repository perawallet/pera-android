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

package com.algorand.android.decider

import com.algorand.android.account.localaccount.domain.usecase.IsThereAnyAccountWithAddress
import com.algorand.android.module.account.core.ui.usecase.GetAccountDisplayName
import com.algorand.android.module.account.core.ui.usecase.GetAccountIconResourceByAccountType
import com.algorand.android.contacts.component.domain.usecase.GetContactByAddress
import com.algorand.android.core.component.detail.domain.usecase.GetAccountDetail
import com.algorand.android.mapper.TransactionTargetUserMapper
import com.algorand.android.models.TransactionTargetUser
import com.algorand.android.utils.toShortenedAddress
import javax.inject.Inject

class TransactionUserUseCase @Inject constructor(
    private val getContactByAddress: GetContactByAddress,
    private val isThereAnyAccountWithAddress: IsThereAnyAccountWithAddress,
    private val transactionTargetUserMapper: TransactionTargetUserMapper,
    private val getAccountIconResourceByAccountType: GetAccountIconResourceByAccountType,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAccountDetail: GetAccountDetail
) {

    suspend fun getTransactionTargetUser(nonOwnerPublicKey: String): TransactionTargetUser {
        val foundContact = getContactByAddress(nonOwnerPublicKey)
        if (foundContact != null) {
            return transactionTargetUserMapper.mapTo(
                publicKey = nonOwnerPublicKey,
                displayName = getAccountDisplayName(nonOwnerPublicKey).primaryDisplayName,
                contact = foundContact
            )
        }
        if (isThereAnyAccountWithAddress(nonOwnerPublicKey)) {
            val accountDetail = getAccountDetail(nonOwnerPublicKey)
            return transactionTargetUserMapper.mapTo(
                publicKey = nonOwnerPublicKey,
                displayName = getAccountDisplayName(accountDetail).primaryDisplayName,
                accountIconResource = getAccountIconResourceByAccountType(accountDetail.accountType)
            )
        }
        return transactionTargetUserMapper.mapTo(
            publicKey = nonOwnerPublicKey,
            displayName = nonOwnerPublicKey.toShortenedAddress()
        )
    }
}
