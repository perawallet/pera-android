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

package com.algorand.android.modules.backup.contact.di

import com.algorand.android.modules.backup.contact.data.DefaultContactsBackupDataImporter
import com.algorand.android.modules.backup.contact.data.DefaultContactsBackupDataProvider
import com.algorand.android.modules.backup.contact.data.DefaultGetAllContactsFlow
import com.algorand.backup.contact.domain.usecase.ContactsBackupDataImporter
import com.algorand.backup.contact.domain.usecase.ContactsBackupDataProvider
import com.algorand.backup.contact.domain.usecase.GetAllContactsFlow
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ContactsBackupModule {

    @Binds
    abstract fun bindContactsBackupDataProvider(impl: DefaultContactsBackupDataProvider): ContactsBackupDataProvider

    @Binds
    abstract fun bindContactsBackupDataImporter(impl: DefaultContactsBackupDataImporter): ContactsBackupDataImporter

    @Binds
    abstract fun bindGetAllContactsFlow(impl: DefaultGetAllContactsFlow): GetAllContactsFlow
}
