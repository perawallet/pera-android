package com.algorand.android.module.account.core.component.polling.accountdetail.di

import com.algorand.android.module.account.core.component.polling.accountdetail.data.repository.AccountBlockPollingRepositoryImpl
import com.algorand.android.module.account.core.component.polling.accountdetail.domain.*
import com.algorand.android.module.account.core.component.polling.accountdetail.domain.repository.AccountBlockPollingRepository
import com.algorand.android.module.account.core.component.polling.accountdetail.domain.usecase.*
import com.algorand.android.module.account.core.component.polling.accountdetail.domain.usecase.implementation.*
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AccountPollingModule {

    @Provides
    @Singleton
    fun provideIsAccountCacheUpdateRequired(
        useCase: IsAccountCacheUpdateRequiredUseCase
    ): IsAccountCacheUpdateRequired = useCase

    @Provides
    @Singleton
    fun provideAccountBlockPollingRepository(
        repository: AccountBlockPollingRepositoryImpl
    ): AccountBlockPollingRepository = repository

    @Provides
    @Singleton
    fun provideUpdateAccountCache(
        useCase: UpdateAccountCacheUseCase
    ): UpdateAccountCache = useCase

    @Provides
    @Singleton
    fun provideAccountDetailCacheManager(
        manager: AccountDetailCacheManagerImpl
    ): AccountDetailCacheManager = manager
}
