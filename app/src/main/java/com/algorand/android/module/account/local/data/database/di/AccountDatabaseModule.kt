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

package com.algorand.android.module.account.local.data.database.di

import android.content.Context
import androidx.room.Room
import com.algorand.android.module.account.local.data.database.AccountDatabase
import com.algorand.android.module.account.local.data.database.AccountDatabase.Companion.DATABASE_NAME
import com.algorand.android.module.account.local.data.database.dao.*
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AccountDatabaseModule {

    @Provides
    @Singleton
    fun provideAccountDatabase(
        @ApplicationContext appContext: Context
    ): AccountDatabase {
        return Room
            .databaseBuilder(appContext, AccountDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideLedgerBleDao(database: AccountDatabase): LedgerBleDao {
        return database.ledgerBleDao()
    }

    @Provides
    @Singleton
    fun provideLedgerUsbDao(database: AccountDatabase): LedgerUsbDao {
        return database.ledgerUsbDao()
    }

    @Provides
    @Singleton
    fun provideNoAuthDao(database: AccountDatabase): NoAuthDao {
        return database.noAuthDao()
    }

    @Provides
    @Singleton
    fun provideAlgo25Dao(database: AccountDatabase): Algo25Dao {
        return database.algo25Dao()
    }
}
