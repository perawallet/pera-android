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

package com.algorand.android.utils

import com.algorand.android.models.Account
import com.algorand.android.module.contacts.domain.model.Contact
import com.algorand.android.modules.transaction.common.domain.model.TransactionDTO

fun getUserIfSavedLocally(
    contactList: List<Contact>,
    accountList: List<Account>,
    nonOwnerPublicKey: String?
): Contact? {
    if (nonOwnerPublicKey == null) {
        return null
    }

    val foundContact = contactList.firstOrNull { it.address == nonOwnerPublicKey }
    if (foundContact != null) {
        return foundContact
    }

    val foundAccount = accountList.firstOrNull { it.address == nonOwnerPublicKey }
    if (foundAccount != null) {
        return Contact(foundAccount.name, foundAccount.address, null)
    }

    return null
}

fun getAllNestedTransactions(transactionDTO: TransactionDTO): Sequence<TransactionDTO> {
    return sequence {
        if (transactionDTO.innerTransactions != null) {
            yieldAll(transactionDTO.innerTransactions)
            transactionDTO.innerTransactions.forEach { yieldAll(getAllNestedTransactions(it)) }
        }
    }
}
