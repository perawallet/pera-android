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

package com.algorand.android.ui.backup.list.di

import com.algorand.android.ui.backup.list.usecase.AddBackupAccountToLocal
import com.algorand.android.ui.backup.list.usecase.AddBackupContactToLocal
import com.algorand.android.ui.backup.list.usecase.DefaultAddBackupAccountToLocal
import com.algorand.android.ui.backup.list.usecase.DefaultAddBackupContactToLocal
import com.algorand.android.ui.backup.list.usecase.DefaultGetNotSyncedBackupAccounts
import com.algorand.android.ui.backup.list.usecase.DefaultGetNotSyncedBackupContacts
import com.algorand.android.ui.backup.list.usecase.DefaultGetSyncedBackupAccounts
import com.algorand.android.ui.backup.list.usecase.DefaultGetSyncedBackupContacts
import com.algorand.android.ui.backup.list.usecase.GetNotSyncedBackupAccounts
import com.algorand.android.ui.backup.list.usecase.GetNotSyncedBackupContacts
import com.algorand.android.ui.backup.list.usecase.GetSyncedBackupAccounts
import com.algorand.android.ui.backup.list.usecase.GetSyncedBackupContacts
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class BackupListUiModule {

    @Binds
    abstract fun bindGetSyncedBackupAccounts(impl: DefaultGetSyncedBackupAccounts): GetSyncedBackupAccounts

    @Binds
    abstract fun bindGetNotSyncedBackupAccounts(impl: DefaultGetNotSyncedBackupAccounts): GetNotSyncedBackupAccounts

    @Binds
    abstract fun bindAddBackupAccountToLocal(impl: DefaultAddBackupAccountToLocal): AddBackupAccountToLocal

    @Binds
    abstract fun bindGetSyncedBackupContacts(impl: DefaultGetSyncedBackupContacts): GetSyncedBackupContacts

    @Binds
    abstract fun bindGetNotSyncedBackupContacts(impl: DefaultGetNotSyncedBackupContacts): GetNotSyncedBackupContacts

    @Binds
    abstract fun bindAddBackupContactToLocal(impl: DefaultAddBackupContactToLocal): AddBackupContactToLocal
}
