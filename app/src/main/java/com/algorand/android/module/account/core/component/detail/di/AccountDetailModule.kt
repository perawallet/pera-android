package com.algorand.android.module.account.core.component.detail.di

import com.algorand.android.module.account.core.component.detail.domain.usecase.*
import com.algorand.android.module.account.core.component.detail.domain.usecase.implementation.*
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AccountDetailModule {

    @Provides
    @Singleton
    fun provideGetAccountDetail(useCase: GetAccountDetailUseCase): GetAccountDetail = useCase

    @Provides
    @Singleton
    fun provideGetAccountDetailFlow(useCase: GetAccountDetailFlowUseCase): GetAccountDetailFlow = useCase

    @Provides
    @Singleton
    fun provideGetAccountType(useCase: GetAccountTypeUseCase): GetAccountType = useCase

    @Provides
    @Singleton
    fun provideGetAccountRegistrationType(
        useCase: GetAccountRegistrationTypeUseCase
    ): GetAccountRegistrationType = useCase

    @Provides
    @Singleton
    fun provideGetAccountsDetail(useCase: GetAccountsDetailUseCase): GetAccountsDetail = useCase
}
