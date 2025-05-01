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

package com.algorand.wallet.asb.di

import com.algorand.wallet.asb.algosdk.AlgorandSdkEncryptionUtils
import com.algorand.wallet.asb.algosdk.AlgorandSdkEncryptionUtilsImpl
import com.algorand.wallet.asb.domain.mapper.AsbBackupDataMapper
import com.algorand.wallet.asb.domain.mapper.AsbBackupDataMapperImpl
import com.algorand.wallet.asb.domain.usecase.GetAsbEligibleAccounts
import com.algorand.wallet.asb.domain.usecase.GetAsbEligibleAccountsUseCase
import com.algorand.wallet.asb.domain.usecase.RestoreEncryptedBackupProtocolPayload
import com.algorand.wallet.asb.domain.usecase.RestoreEncryptedBackupProtocolPayloadUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object AsbBackupModule {

    @Provides
    fun provideAsbBackupDataMapper(impl: AsbBackupDataMapperImpl): AsbBackupDataMapper = impl

    @Provides
    fun provideAlgorandSdkEncryptionUtils(impl: AlgorandSdkEncryptionUtilsImpl): AlgorandSdkEncryptionUtils = impl

    @Provides
    fun provideRestoreEncryptedBackupProtocolPayload(
        useCase: RestoreEncryptedBackupProtocolPayloadUseCase
    ): RestoreEncryptedBackupProtocolPayload = useCase

    @Provides
    fun provideGetAsbEligibleAccounts(useCase: GetAsbEligibleAccountsUseCase): GetAsbEligibleAccounts = useCase
}
