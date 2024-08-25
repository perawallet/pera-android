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

package com.algorand.android.dependencyinjection

import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.algorand.android.core.AccountManager
import com.algorand.android.database.AlgorandDatabase
import com.algorand.android.database.AlgorandDatabase.Companion.MIGRATION_10_11
import com.algorand.android.database.AlgorandDatabase.Companion.MIGRATION_11_12
import com.algorand.android.database.AlgorandDatabase.Companion.MIGRATION_3_4
import com.algorand.android.database.AlgorandDatabase.Companion.MIGRATION_4_5
import com.algorand.android.database.AlgorandDatabase.Companion.MIGRATION_5_6
import com.algorand.android.database.AlgorandDatabase.Companion.MIGRATION_6_7
import com.algorand.android.database.AlgorandDatabase.Companion.MIGRATION_7_8
import com.algorand.android.database.AlgorandDatabase.Companion.MIGRATION_8_9
import com.algorand.android.database.AlgorandDatabase.Companion.MIGRATION_9_10
import com.algorand.android.database.ContactDao
import com.algorand.android.database.NodeDao
import com.algorand.android.database.NotificationFilterDao
import com.algorand.android.database.WalletConnectDao
import com.algorand.android.database.WalletConnectTypeConverters
import com.algorand.android.notification.PeraNotificationManager
import com.algorand.android.utils.ALGORAND_KEYSTORE_URI
import com.algorand.android.utils.ENCRYPTED_SHARED_PREF_NAME
import com.algorand.android.utils.KEYSET_HANDLE
import com.algorand.android.utils.KEY_TEMPLATE_AES256_GCM
import com.algorand.android.utils.preference.SETTINGS
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("TooManyFunctions")
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext appContext: Context,
        walletConnectTypeConverters: WalletConnectTypeConverters
    ): AlgorandDatabase {
        return Room
            .databaseBuilder(appContext, AlgorandDatabase::class.java, AlgorandDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .addMigrations(
                MIGRATION_3_4,
                MIGRATION_4_5,
                MIGRATION_5_6,
                MIGRATION_6_7,
                MIGRATION_7_8,
                MIGRATION_8_9,
                MIGRATION_9_10,
                MIGRATION_10_11,
                MIGRATION_11_12
            )
            .addTypeConverter(walletConnectTypeConverters)
            .build()
    }

    @Singleton
    @Provides
    fun getEncryptionAead(@ApplicationContext appContext: Context): Aead {
        AeadConfig.register()

        val algorandKeysetHandle: KeysetHandle = AndroidKeysetManager.Builder()
            .withSharedPref(appContext, KEYSET_HANDLE, ENCRYPTED_SHARED_PREF_NAME)
            .withKeyTemplate(KeyTemplates.get(KEY_TEMPLATE_AES256_GCM))
            .withMasterKeyUri(ALGORAND_KEYSTORE_URI)
            .build()
            .keysetHandle

        return algorandKeysetHandle.getPrimitive(Aead::class.java)
    }

    @Singleton
    @Provides
    fun provideSettingsSharedPref(@ApplicationContext appContext: Context): SharedPreferences {
        return appContext.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideNodeDao(database: AlgorandDatabase): NodeDao {
        return database.nodeDao()
    }

    @Singleton
    @Provides
    fun provideNotificationFilterDao(database: AlgorandDatabase): NotificationFilterDao {
        return database.notificationFilterDao()
    }

    @Singleton
    @Provides
    fun provideContactDao(database: AlgorandDatabase): ContactDao {
        return database.contactDao()
    }

    @Singleton
    @Provides
    fun provideWalletConnectDao(database: AlgorandDatabase): WalletConnectDao {
        return database.walletConnect()
    }

    @Singleton
    @Provides
    fun provideAlgorandNotificationManager(): PeraNotificationManager {
        return PeraNotificationManager()
    }

    @Singleton
    @Provides
    fun provideAccountManager(
        aead: Aead,
        gson: Gson,
        sharedPref: SharedPreferences
    ): AccountManager {
        return AccountManager(aead, gson, sharedPref)
    }

    @Singleton
    @Provides
    fun provideNotificationManager(@ApplicationContext appContext: Context): NotificationManager? {
        return appContext.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
    }
}
