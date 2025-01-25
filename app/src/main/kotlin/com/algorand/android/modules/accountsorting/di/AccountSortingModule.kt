package com.algorand.android.modules.accountsorting.di

import android.content.SharedPreferences
import com.algorand.android.modules.accountsorting.data.repository.AccountSortingRepositoryImpl
import com.algorand.android.modules.accountsorting.data.storage.AccountSortPreferencesLocalSource
import com.algorand.android.modules.accountsorting.domain.repository.AccountSortingRepository
import com.algorand.android.modules.accountsorting.domain.usecase.GetAccountSortingTypeIdentifier
import com.algorand.android.modules.accountsorting.domain.usecase.GetSortedLocalAccounts
import com.algorand.android.modules.accountsorting.domain.usecase.SaveAccountSortPreference
import com.algorand.android.modules.accountsorting.domain.usecase.implementation.GetSortedLocalAccountsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AccountSortingModule {

    @Provides
    @Singleton
    fun provideAccountSortingRepository(sharedPreferences: SharedPreferences): AccountSortingRepository {
        return AccountSortingRepositoryImpl(AccountSortPreferencesLocalSource(sharedPreferences))
    }

    @Provides
    @Singleton
    fun provideGetSortedLocalAccounts(useCase: GetSortedLocalAccountsUseCase): GetSortedLocalAccounts = useCase

    @Provides
    @Singleton
    fun provideGetAccountSortingTypeIdentifier(repository: AccountSortingRepository): GetAccountSortingTypeIdentifier {
        return GetAccountSortingTypeIdentifier(repository::getAccountSortPreference)
    }

    @Provides
    @Singleton
    fun provideSaveAccountSortPreference(repository: AccountSortingRepository): SaveAccountSortPreference {
        return SaveAccountSortPreference(repository::saveAccountSortPreference)
    }
}
