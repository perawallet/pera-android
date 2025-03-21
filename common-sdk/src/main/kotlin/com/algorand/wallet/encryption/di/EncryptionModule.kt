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

import com.algorand.wallet.encryption.data.manager.Base64ManagerImpl
import com.algorand.wallet.encryption.data.repository.StrongBoxRepositoryImpl
import com.algorand.wallet.encryption.domain.manager.AESPlatformManager
import com.algorand.wallet.encryption.domain.manager.AESPlatformManagerImpl
import com.algorand.wallet.encryption.domain.manager.Base64Manager
import com.algorand.wallet.encryption.domain.repository.StrongBoxRepository
import com.algorand.wallet.encryption.domain.usecase.GetStrongBoxUsedCheck
import com.algorand.wallet.encryption.domain.usecase.SaveStrongBoxUsedCheck
import com.algorand.wallet.encryption.domain.utils.Constants
import com.algorand.wallet.foundation.cache.PersistentCacheProvider
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
    fun provideAESPlatformManager(impl: AESPlatformManagerImpl): AESPlatformManager = impl

    @Provides
    @Singleton
    fun provideStrongBoxRepository(
        persistentCacheProvider: PersistentCacheProvider
    ): StrongBoxRepository {
        return StrongBoxRepositoryImpl(
            persistentCacheProvider.getPersistentCache(Boolean::class.java, Constants.STRONGBOX_USED),
        )
    }

    @Provides
    fun provideGetStrongBoxUsedCheck(repository: StrongBoxRepository): GetStrongBoxUsedCheck =
        GetStrongBoxUsedCheck(repository::getStrongBoxUsed)

    @Provides
    fun provideSaveStrongBoxUsedCheck(repository: StrongBoxRepository): SaveStrongBoxUsedCheck =
        SaveStrongBoxUsedCheck(repository::saveStrongBoxUsed)
}
