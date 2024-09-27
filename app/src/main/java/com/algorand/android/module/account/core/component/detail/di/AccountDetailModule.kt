package com.algorand.android.module.account.core.component.detail.di

import com.algorand.android.module.account.core.component.detail.domain.usecase.GetAccountDetail
import com.algorand.android.module.account.core.component.detail.domain.usecase.GetAccountDetailFlow
import com.algorand.android.module.account.core.component.detail.domain.usecase.GetAccountRegistrationType
import com.algorand.android.module.account.core.component.detail.domain.usecase.GetAccountType
import com.algorand.android.module.account.core.component.detail.domain.usecase.GetAccountsDetail
import com.algorand.android.module.account.core.component.detail.domain.usecase.implementation.GetAccountDetailFlowUseCase
import com.algorand.android.module.account.core.component.detail.domain.usecase.implementation.GetAccountDetailUseCase
import com.algorand.android.module.account.core.component.detail.domain.usecase.implementation.GetAccountRegistrationTypeUseCase
import com.algorand.android.module.account.core.component.detail.domain.usecase.implementation.GetAccountTypeUseCase
import com.algorand.android.module.account.core.component.detail.domain.usecase.implementation.GetAccountsDetailUseCase
import dagger.Module
import dagger.Provides
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
