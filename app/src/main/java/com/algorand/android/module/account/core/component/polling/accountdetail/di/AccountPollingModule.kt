package com.algorand.android.module.account.core.component.polling.accountdetail.di

import com.algorand.android.module.account.core.component.polling.accountdetail.data.repository.AccountBlockPollingRepositoryImpl
import com.algorand.android.module.account.core.component.polling.accountdetail.domain.AccountDetailCacheManager
import com.algorand.android.module.account.core.component.polling.accountdetail.domain.AccountDetailCacheManagerImpl
import com.algorand.android.module.account.core.component.polling.accountdetail.domain.repository.AccountBlockPollingRepository
import com.algorand.android.module.account.core.component.polling.accountdetail.domain.usecase.IsAccountCacheUpdateRequired
import com.algorand.android.module.account.core.component.polling.accountdetail.domain.usecase.UpdateAccountCache
import com.algorand.android.module.account.core.component.polling.accountdetail.domain.usecase.implementation.IsAccountCacheUpdateRequiredUseCase
import com.algorand.android.module.account.core.component.polling.accountdetail.domain.usecase.implementation.UpdateAccountCacheUseCase
import dagger.Module
import dagger.Provides
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
