package com.algorand.android.modules.settings.di

import com.algorand.android.modules.settings.data.repository.MigrationTo6xRepositoryImpl
import com.algorand.android.modules.settings.domain.repository.MigrationTo6xRepository
import com.algorand.android.modules.settings.domain.usecase.GetMigratedTo6xCheck
import com.algorand.android.modules.settings.domain.usecase.SaveMigratedTo6xCheck
import com.algorand.android.modules.settings.domain.utils.SettingsConstants
import com.algorand.wallet.foundation.cache.PersistentCacheProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object SettingsModule {

    @Provides
    @Singleton
    fun provideMigrationTo6xRepository(
        persistentCacheProvider: PersistentCacheProvider
    ): MigrationTo6xRepository {
        return MigrationTo6xRepositoryImpl(
            persistentCacheProvider.getPersistentCache(Boolean::class.java, SettingsConstants.MIGRATE_TO_6X),
        )
    }

    @Provides
    fun provideGetMigratedTo6xCheck(repository: MigrationTo6xRepository): GetMigratedTo6xCheck =
        GetMigratedTo6xCheck(repository::getMigratedTo6xCheck)

    @Provides
    fun provideSSaveMigratedTo6xCheck(repository: MigrationTo6xRepository): SaveMigratedTo6xCheck =
        SaveMigratedTo6xCheck(repository::saveMigratedTo6xCheck)
}
