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

package com.algorand.android.ui.backup.sync.di

import com.algorand.android.ui.backup.sync.security.AesGcmBackupSyncQrPayloadCipher
import com.algorand.android.ui.backup.sync.security.BackupSyncQrPayloadCipher
import com.algorand.android.ui.backup.sync.security.BackupSyncQrPayloadJsonSerializer
import com.algorand.android.ui.backup.sync.security.BackupSyncQrPayloadSerializer
import com.algorand.android.ui.backup.sync.usecase.DefaultGetDecryptedBackupSyncQrPayload
import com.algorand.android.ui.backup.sync.usecase.DefaultGetEncryptedBackupSyncQrPayload
import com.algorand.android.ui.backup.sync.usecase.DefaultIsBackupSyncQrPayload
import com.algorand.android.ui.backup.sync.usecase.GetDecryptedBackupSyncQrPayload
import com.algorand.android.ui.backup.sync.usecase.GetEncryptedBackupSyncQrPayload
import com.algorand.android.ui.backup.sync.usecase.IsBackupSyncQrPayload
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class BackupSyncUiModule {

    @Binds
    abstract fun bindBackupSyncQrPayloadSerializer(
        impl: BackupSyncQrPayloadJsonSerializer
    ): BackupSyncQrPayloadSerializer

    @Binds
    abstract fun bindBackupSyncQrPayloadCipher(
        impl: AesGcmBackupSyncQrPayloadCipher
    ): BackupSyncQrPayloadCipher

    @Binds
    abstract fun bindGetEncryptedBackupSyncQrPayload(
        impl: DefaultGetEncryptedBackupSyncQrPayload
    ): GetEncryptedBackupSyncQrPayload

    @Binds
    abstract fun bindGetDecryptedBackupSyncQrPayload(
        impl: DefaultGetDecryptedBackupSyncQrPayload
    ): GetDecryptedBackupSyncQrPayload

    @Binds
    abstract fun bindIsBackupSyncQrPayload(
        impl: DefaultIsBackupSyncQrPayload
    ): IsBackupSyncQrPayload
}
