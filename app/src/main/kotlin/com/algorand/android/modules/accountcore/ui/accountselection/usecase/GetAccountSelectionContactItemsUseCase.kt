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

package com.algorand.android.modules.accountcore.ui.accountselection.usecase

import com.algorand.android.models.BaseAccountSelectionListItem
import com.algorand.android.modules.accountcore.ui.accountselection.mapper.AccountSelectionListItemMapper
import com.algorand.android.usecase.ContactUseCase
import javax.inject.Inject

internal class GetAccountSelectionContactItemsUseCase @Inject constructor(
    private val contactUseCase: ContactUseCase,
    private val accountSelectionListItemMapper: AccountSelectionListItemMapper
) : GetAccountSelectionContactItems {

    override suspend fun invoke(): List<BaseAccountSelectionListItem.BaseAccountItem> {
        return contactUseCase.getAllContacts().map { contact ->
            accountSelectionListItemMapper.mapToContactItem(
                name = contact.name,
                address = contact.publicKey,
                imageUri = contact.imageUriAsString
            )
        }
    }
}
