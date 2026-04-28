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

package com.algorand.android.ui.backup.list.usecase

import com.algorand.android.ui.backup.list.model.BackupAccountListItem
import com.algorand.android.ui.backup.list.model.BackupContactListItem
import com.algorand.backup.account.domain.model.AddressBackupPayload
import com.algorand.backup.contact.domain.model.ContactBackupPayload
import com.algorand.wallet.foundation.PeraResult

fun interface GetSyncedBackupAccounts {
    suspend operator fun invoke(): List<BackupAccountListItem>
}

fun interface GetNotSyncedBackupAccounts {
    suspend operator fun invoke(): List<BackupAccountListItem>
}

fun interface AddBackupAccountToLocal {
    suspend operator fun invoke(payload: AddressBackupPayload): PeraResult<Unit>
}

fun interface GetSyncedBackupContacts {
    suspend operator fun invoke(): List<BackupContactListItem>
}

fun interface GetNotSyncedBackupContacts {
    suspend operator fun invoke(): List<BackupContactListItem>
}

fun interface AddBackupContactToLocal {
    suspend operator fun invoke(payload: ContactBackupPayload): PeraResult<Unit>
}
