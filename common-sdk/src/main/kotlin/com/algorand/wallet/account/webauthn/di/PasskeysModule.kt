package com.algorand.wallet.account.webauthn.di

import android.content.Context
import androidx.room.Room
import com.algorand.wallet.account.webauthn.data.database.PasskeyDatabase
import com.algorand.wallet.account.webauthn.data.repository.PasskeyRepositoryImpl
import com.algorand.wallet.account.webauthn.domain.PasskeyManager
import com.algorand.wallet.account.webauthn.domain.PasskeyManagerImpl
import com.algorand.wallet.account.webauthn.domain.repository.PasskeyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PasskeysModule {
    @Provides
    @Singleton
    fun providePasskeyDatabase(@ApplicationContext context: Context): PasskeyDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = PasskeyDatabase::class.java,
            name = PasskeyDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideSiteDao(passkeyDatabase: PasskeyDatabase) = passkeyDatabase.siteDao()

    @Provides
    @Singleton
    fun providePasskeyDao(passkeyDatabase: PasskeyDatabase) = passkeyDatabase.passkeyDao()

    @Provides
    fun providePasskeyRepository(repository: PasskeyRepositoryImpl): PasskeyRepository = repository

    @Provides
    fun providePasskeyManager(manager: PasskeyManagerImpl): PasskeyManager = manager
}
