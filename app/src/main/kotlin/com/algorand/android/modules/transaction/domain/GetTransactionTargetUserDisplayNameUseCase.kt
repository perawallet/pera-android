/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.transaction.domain

import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.repository.ContactRepository
import com.algorand.android.utils.toShortenedAddress
import javax.inject.Inject

internal class GetTransactionTargetUserDisplayNameUseCase @Inject constructor(
    private val contactRepository: ContactRepository,
    private val getAccountDisplayName: GetAccountDisplayName,
) : GetTransactionTargetUserDisplayName {

    override suspend fun invoke(address: String): String {
        val existingContact = contactRepository.getContactByAddress(address)
        if (existingContact != null) {
            return existingContact.name.ifEmpty { address.toShortenedAddress() }
        }
        return getAccountDisplayName(address).primaryDisplayName
    }
}
