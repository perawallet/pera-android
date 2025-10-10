@file:Suppress("TooManyFunctions")
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

package com.algorand.android.credentials.passkeys.di

import android.content.Context
import androidx.room.Room
import com.algorand.android.credentials.passkeys.data.database.PasskeyDatabase
import com.algorand.android.credentials.passkeys.data.mapper.DefaultPasskeyEntityMapper
import com.algorand.android.credentials.passkeys.data.mapper.DefaultPasskeyMapper
import com.algorand.android.credentials.passkeys.data.mapper.PasskeyEntityMapper
import com.algorand.android.credentials.passkeys.data.mapper.PasskeyMapper
import com.algorand.android.credentials.passkeys.data.repository.DefaultPasskeyRepository
import com.algorand.android.credentials.passkeys.domain.Bip39SignManager
import com.algorand.android.credentials.passkeys.domain.DeterministicBip39SignManager
import com.algorand.android.credentials.passkeys.domain.repository.PasskeyRepository
import com.algorand.android.credentials.passkeys.domain.usecase.AddNewPasskey
import com.algorand.android.credentials.passkeys.domain.usecase.AddNewPasskeyUseCase
import com.algorand.android.credentials.passkeys.domain.usecase.ClearAllPasskeys
import com.algorand.android.credentials.passkeys.domain.usecase.DoesPasskeyExist
import com.algorand.android.credentials.passkeys.domain.usecase.GetAllPasskeysAsFlow
import com.algorand.android.credentials.passkeys.domain.usecase.GetPasskeyByCredentialId
import com.algorand.android.credentials.passkeys.domain.usecase.GetSitePasskeyCount
import com.algorand.android.credentials.passkeys.domain.usecase.GetSitePasskeys
import com.algorand.android.credentials.passkeys.domain.usecase.RemovePasskeyByCredentialId
import com.algorand.android.credentials.passkeys.domain.usecase.SetPasskeyLastUsedTime
import com.algorand.android.credentials.passkeys.foundation.CoseMapper
import com.algorand.android.credentials.passkeys.foundation.DefaultCoseMapper
import com.algorand.android.credentials.passkeys.ui.builder.DefaultPasskeyCreateCredentialEntryBuilder
import com.algorand.android.credentials.passkeys.ui.builder.DefaultPasskeyGetCredentialsEntryBuilder
import com.algorand.android.credentials.passkeys.ui.builder.PasskeyCreateCredentialEntryBuilder
import com.algorand.android.credentials.passkeys.ui.builder.PasskeyGetCredentialsEntryBuilder
import com.algorand.android.credentials.passkeys.ui.viewmodel.DefaultGetCredentialResponseProcessor
import com.algorand.android.credentials.passkeys.ui.viewmodel.GetCredentialResponseProcessor
import com.algorand.wallet.account.local.domain.usecase.GetAllHdSeedFirstAddresses
import com.algorand.wallet.account.local.domain.usecase.GetHdEntropy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import foundation.algorand.deterministicP256.DeterministicP256
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object PasskeyModule {

    @Provides
    @Singleton
    fun providePasskeyDatabase(@ApplicationContext context: Context): PasskeyDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = PasskeyDatabase::class.java,
            name = PasskeyDatabase.Companion.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun providePasskeyDao(database: PasskeyDatabase) = database.passkeyDao()

    @Provides
    @Singleton
    fun providePasskeySiteDao(database: PasskeyDatabase) = database.passkeySiteDao()

    @Provides
    fun providePasskeyRepository(repository: DefaultPasskeyRepository): PasskeyRepository = repository

    @Provides
    fun providePasskeyCreateCredentialEntryBuilder(
        builder: DefaultPasskeyCreateCredentialEntryBuilder
    ): PasskeyCreateCredentialEntryBuilder = builder

    @Provides
    fun provideGetSitePasskeyCount(repository: PasskeyRepository): GetSitePasskeyCount {
        return GetSitePasskeyCount(repository::getSitePasskeysCount)
    }

    @Provides
    fun providePasskeyGetCredentialsEntryBuilder(
        builder: DefaultPasskeyGetCredentialsEntryBuilder
    ): PasskeyGetCredentialsEntryBuilder = builder

    @Provides
    fun provideGetSitePasskeys(
        passkeyRepository: PasskeyRepository
    ): GetSitePasskeys {
        return GetSitePasskeys(passkeyRepository::getSitePasskeys)
    }

    @Provides
    fun provideCoseMapper(mapper: DefaultCoseMapper): CoseMapper = mapper

    @Provides
    fun provideAddNewPasskey(useCase: AddNewPasskeyUseCase): AddNewPasskey = useCase

    @Provides
    fun provideBip39SignManager(
        getHdEntropy: GetHdEntropy,
        getAllHdSeedFirstAddresses: GetAllHdSeedFirstAddresses
    ): Bip39SignManager {
        return DeterministicBip39SignManager(DeterministicP256(), getAllHdSeedFirstAddresses, getHdEntropy)
    }

    @Provides
    fun providePasskeyMapper(mapper: DefaultPasskeyMapper): PasskeyMapper = mapper

    @Provides
    fun providePasskeyEntityMapper(mapper: DefaultPasskeyEntityMapper): PasskeyEntityMapper = mapper

    @Provides
    fun provideGetPasskeyByCredentialId(
        repository: PasskeyRepository
    ): GetPasskeyByCredentialId = GetPasskeyByCredentialId(repository::getPasskey)

    @Provides
    fun provideGetCredentialResponseProcessor(
        processor: DefaultGetCredentialResponseProcessor
    ): GetCredentialResponseProcessor = processor

    @Provides
    fun provideGetAllPasskeysAsFlow(repository: PasskeyRepository): GetAllPasskeysAsFlow {
        return GetAllPasskeysAsFlow(repository::getAllPasskeysAsFlow)
    }

    @Provides
    fun provideRemovePasskeyByCredentialId(repository: PasskeyRepository): RemovePasskeyByCredentialId {
        return RemovePasskeyByCredentialId(repository::removePasskeyByCredentialId)
    }

    @Provides
    fun provideClearAllPasskeys(repository: PasskeyRepository): ClearAllPasskeys {
        return ClearAllPasskeys(repository::clearAllPasskeys)
    }

    @Provides
    fun provideSetPasskeyLastUsedTime(repository: PasskeyRepository): SetPasskeyLastUsedTime {
        return SetPasskeyLastUsedTime(repository::setPasskeyLastUsedTime)
    }

    @Provides
    fun provideDoesPasskeyExist(repository: PasskeyRepository): DoesPasskeyExist {
        return DoesPasskeyExist(repository::doesPasskeyExist)
    }
}
