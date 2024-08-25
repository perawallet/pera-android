package com.algorand.android.accountsorting.component.di

import android.content.SharedPreferences
import com.algorand.android.accountsorting.component.data.repository.AccountSortingRepositoryImpl
import com.algorand.android.accountsorting.component.data.storage.AccountSortPreferencesLocalSource
import com.algorand.android.accountsorting.component.domain.repository.AccountSortingRepository
import com.algorand.android.accountsorting.component.domain.usecase.*
import com.algorand.android.accountsorting.component.domain.usecase.implementation.GetSortedLocalAccountsUseCase
import com.algorand.android.encryption.EncryptionManager
import com.algorand.android.encryption.di.DETERMINISTIC_ENCRYPTION_MANAGER
import com.algorand.android.shared_db.accountsorting.dao.AccountIndexDao
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.*

@Module
@InstallIn(SingletonComponent::class)
internal object AccountSortingModule {

    @Provides
    @Singleton
    fun provideAccountSortingRepository(
        @Named(DETERMINISTIC_ENCRYPTION_MANAGER) encryptionManager: EncryptionManager,
        accountIndexDao: AccountIndexDao,
        sharedPreferences: SharedPreferences
    ): AccountSortingRepository {
        return AccountSortingRepositoryImpl(
            accountIndexDao,
            encryptionManager,
            AccountSortPreferencesLocalSource(sharedPreferences)
        )
    }

    @Provides
    @Singleton
    fun provideGetSortedLocalAccounts(
        useCase: GetSortedLocalAccountsUseCase
    ): GetSortedLocalAccounts = useCase

    @Provides
    @Singleton
    fun provideSetAccountOrderIndex(
        accountSortingRepository: AccountSortingRepository
    ): SetAccountOrderIndex = SetAccountOrderIndex(accountSortingRepository::setAccountOrderIndex)

    @Provides
    @Singleton
    fun provideRemoveAccountOrderIndex(
        accountSortingRepository: AccountSortingRepository
    ): RemoveAccountOrderIndex = RemoveAccountOrderIndex(accountSortingRepository::removeAccountOrderIndex)

    @Provides
    @Singleton
    fun provideGetAccountSortingTypeIdentifier(
        accountSortingRepository: AccountSortingRepository
    ): GetAccountSortingTypeIdentifier =
        GetAccountSortingTypeIdentifier(accountSortingRepository::getAccountSortPreference)

    @Provides
    @Singleton
    fun provideSaveAccountSortPreference(
        repository: AccountSortingRepository
    ): SaveAccountSortPreference = SaveAccountSortPreference(repository::saveAccountSortPreference)
}
