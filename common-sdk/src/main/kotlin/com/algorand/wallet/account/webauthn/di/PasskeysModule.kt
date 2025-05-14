package com.algorand.wallet.account.webauthn.di

import android.content.Context
import androidx.room.Room
import com.algorand.wallet.account.webauthn.data.database.PasskeyDatabase
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
}
