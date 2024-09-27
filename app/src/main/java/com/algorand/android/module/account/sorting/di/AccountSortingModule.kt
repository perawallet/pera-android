package com.algorand.android.module.account.sorting.di

import android.content.SharedPreferences
import com.algorand.android.module.encryption.EncryptionManager
import com.algorand.android.module.encryption.di.DETERMINISTIC_ENCRYPTION_MANAGER
import com.algorand.android.module.account.sorting.data.repository.AccountSortingRepositoryImpl
import com.algorand.android.module.account.sorting.data.storage.AccountSortPreferencesLocalSource
import com.algorand.android.module.account.sorting.domain.repository.AccountSortingRepository
import com.algorand.android.module.account.sorting.domain.usecase.GetAccountSortingTypeIdentifier
import com.algorand.android.module.account.sorting.domain.usecase.GetSortedLocalAccounts
import com.algorand.android.module.account.sorting.domain.usecase.RemoveAccountOrderIndex
import com.algorand.android.module.account.sorting.domain.usecase.SaveAccountSortPreference
import com.algorand.android.module.account.sorting.domain.usecase.SetAccountOrderIndex
import com.algorand.android.module.account.sorting.domain.usecase.implementation.GetSortedLocalAccountsUseCase
import com.algorand.android.module.shareddb.accountsorting.dao.AccountIndexDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

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
