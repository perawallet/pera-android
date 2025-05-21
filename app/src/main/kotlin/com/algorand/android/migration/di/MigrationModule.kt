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

package com.algorand.android.migration.di

import com.algorand.android.migration.domain.usecase.GetMigratedTo6xCheck
import com.algorand.android.migration.domain.usecase.MigrateTo6x
import com.algorand.android.migration.domain.usecase.MigrateTo6xUseCase
import com.algorand.android.migration.domain.usecase.SaveMigratedTo6xCheck
import com.algorand.android.migration.data.repository.MigrationTo6xRepositoryImpl
import com.algorand.android.migration.domain.repository.MigrationTo6xRepository
import com.algorand.android.migration.domain.utils.MigrationConstants
import com.algorand.wallet.foundation.cache.PersistentCacheProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object MigrationModule {

    @Provides
    @Singleton
    fun provideMigrateTo6x(useCase: MigrateTo6xUseCase): MigrateTo6x = useCase

    @Provides
    @Singleton
    fun provideMigrationTo6xRepository(persistentCacheProvider: PersistentCacheProvider): MigrationTo6xRepository {
        return MigrationTo6xRepositoryImpl(
            persistentCacheProvider.getPersistentCache(Boolean::class.java, MigrationConstants.MIGRATE_TO_6X),
        )
    }

    @Provides
    fun provideGetMigratedTo6xCheck(
        repository: MigrationTo6xRepository
    ): GetMigratedTo6xCheck = GetMigratedTo6xCheck(repository::getMigratedTo6xCheck)

    @Provides
    fun provideSaveMigratedTo6xCheck(
        repository: MigrationTo6xRepository
    ): SaveMigratedTo6xCheck = SaveMigratedTo6xCheck(repository::saveMigratedTo6xCheck)
}
