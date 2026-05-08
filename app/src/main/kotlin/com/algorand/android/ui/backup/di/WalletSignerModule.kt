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

package com.algorand.android.ui.backup.di

import com.algorand.android.ui.device.usecase.GetDeviceConfig
import com.algorand.backup.domain.model.DeviceId
import com.algorand.backup.domain.security.BackupRegistrationSigner
import com.algorand.backup.domain.usecase.ProvideBackupDeviceId
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object WalletSignerModule {

    @Provides
    fun provideBackupRegistrationSigner(
        signer: AlgoSdkBackupRegistrationSigner
    ): BackupRegistrationSigner = signer

    @Provides
    fun provideBackupDeviceId(
        getDeviceConfig: GetDeviceConfig
    ): ProvideBackupDeviceId = ProvideBackupDeviceId {
        DeviceId(getDeviceConfig().deviceId)
    }
}
