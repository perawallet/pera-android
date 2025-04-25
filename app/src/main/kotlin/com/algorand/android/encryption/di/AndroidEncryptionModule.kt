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

package com.algorand.android.encryption.di

import com.algorand.android.encryption.data.repository.StrongBoxRepositoryImpl
import com.algorand.android.encryption.domain.repository.StrongBoxRepository
import com.algorand.android.encryption.domain.usecase.AndroidEncryptionManager
import com.algorand.android.encryption.domain.usecase.AndroidEncryptionManagerImpl
import com.algorand.android.encryption.domain.usecase.GetStrongBoxUsedCheck
import com.algorand.android.encryption.domain.usecase.SaveStrongBoxUsedCheck
import com.algorand.wallet.analytics.domain.usecase.IsStrongBoxUsedForEncryption
import com.algorand.wallet.encryption.domain.usecase.GetEncryptionSecretKey
import com.algorand.wallet.foundation.cache.PersistentCacheProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AndroidEncryptionModule {

    @Provides
    fun provideAndroidEncryptionManager(impl: AndroidEncryptionManagerImpl): AndroidEncryptionManager = impl

    @Provides
    @Singleton
    fun provideStrongBoxRepository(
        persistentCacheProvider: PersistentCacheProvider
    ): StrongBoxRepository {
        return StrongBoxRepositoryImpl(
            persistentCacheProvider.getPersistentCache(Boolean::class.java, key = "strongbox_used"),
        )
    }

    @Provides
    fun provideGetStrongBoxUsedCheck(repository: StrongBoxRepository): GetStrongBoxUsedCheck {
        return GetStrongBoxUsedCheck(repository::getStrongBoxUsed)
    }

    @Provides
    fun provideSaveStrongBoxUsedCheck(repository: StrongBoxRepository): SaveStrongBoxUsedCheck {
        return SaveStrongBoxUsedCheck(repository::saveStrongBoxUsed)
    }

    @Provides
    fun providesGetEncryptionSecretKey(manager: AndroidEncryptionManager): GetEncryptionSecretKey {
        return GetEncryptionSecretKey(manager::getSecretKey)
    }

    @Provides
    fun provideCommonGetStrongBoxUsedCheck(useCase: GetStrongBoxUsedCheck): IsStrongBoxUsedForEncryption {
        return IsStrongBoxUsedForEncryption(useCase::invoke)
    }
}
