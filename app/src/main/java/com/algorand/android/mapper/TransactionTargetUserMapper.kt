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

package com.algorand.android.mapper

import com.algorand.android.models.TransactionTargetUser
import com.algorand.android.module.account.core.ui.model.AccountIconResource
import com.algorand.android.module.contacts.domain.model.Contact
import javax.inject.Inject

class TransactionTargetUserMapper @Inject constructor() {

    fun mapTo(
        publicKey: String,
        displayName: String,
        contact: Contact? = null,
        accountIconResource: AccountIconResource? = null
    ): TransactionTargetUser {
        return TransactionTargetUser(
            publicKey = publicKey,
            displayName = displayName,
            contact = contact,
            accountIconResource = accountIconResource
        )
    }
}
