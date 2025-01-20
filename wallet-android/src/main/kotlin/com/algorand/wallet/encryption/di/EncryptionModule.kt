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

package com.algorand.wallet.encryption.di

import com.algorand.wallet.encryption.Base64Manager
import com.algorand.wallet.encryption.Base64ManagerImpl
import com.algorand.wallet.encryption.EntropyEncryptionManager
import com.algorand.wallet.encryption.EntropyEncryptionManagerImpl
import com.algorand.wallet.encryption.SecretKeyEncryptionManager
import com.algorand.wallet.encryption.SecretKeyEncryptionManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object EncryptionModule {

    @Provides
    @Singleton
    fun provideBase64Manager(impl: Base64ManagerImpl): Base64Manager = impl

    @Provides
    @Singleton
    fun provideSecretKeyEncryptionManager(impl: SecretKeyEncryptionManagerImpl): SecretKeyEncryptionManager = impl

    @Provides
    @Singleton
    fun provideEntropyEncryptionManager(impl: EntropyEncryptionManagerImpl): EntropyEncryptionManager = impl
}
