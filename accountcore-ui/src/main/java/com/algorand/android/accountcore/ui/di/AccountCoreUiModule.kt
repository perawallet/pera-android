package com.algorand.android.accountcore.ui.di

import android.content.Context
import com.algorand.android.accountcore.ui.mapper.*
import com.algorand.android.accountcore.ui.usecase.*
import com.algorand.android.accountcore.ui.usecase.implementation.*
import com.algorand.android.core.component.detail.domain.usecase.GetAccountDetail
import com.algorand.android.custominfo.component.domain.usecase.GetCustomInfoOrNull
import com.algorand.android.nameservice.domain.usecase.GetAccountNameService
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AccountCoreUiModule {

    @Provides
    @Singleton
    fun provideGetAssetName(@ApplicationContext context: Context): GetAssetName {
        return GetAssetNameUseCase(context.resources)
    }

    @Provides
    @Singleton
    fun provideAccountItemConfigurationMapper(
        mapper: AccountItemConfigurationMapperImpl
    ): AccountItemConfigurationMapper = mapper

    @Provides
    @Singleton
    fun provideGetAccountIconResourceByAccountType(
        useCase: GetAccountIconResourceByAccountTypeUseCase
    ): GetAccountIconResourceByAccountType = useCase

    @Provides
    @Singleton
    fun provideGetAccountDisplayName(
        getCustomInfoOrNull: GetCustomInfoOrNull,
        getAccountDetail: GetAccountDetail,
        @ApplicationContext context: Context,
        getAccountNameService: GetAccountNameService
    ): GetAccountDisplayName {
        return GetAccountDisplayNameUseCase(
            getCustomInfoOrNull,
            getAccountDetail,
            context.resources,
            getAccountNameService
        )
    }

    @Provides
    @Singleton
    fun provideVerificationTierConfigurationMapper(
        mapper: VerificationTierConfigurationMapperImpl
    ): VerificationTierConfigurationMapper = mapper

    @Provides
    @Singleton
    fun provideGetAccountIconDrawablePreview(
        useCase: GetAccountIconDrawablePreviewUseCase
    ): GetAccountIconDrawablePreview = useCase

    @Provides
    @Singleton
    fun provideGetAccountOriginalStateIconDrawablePreview(
        useCase: GetAccountOriginalStateIconDrawablePreviewUseCase
    ): GetAccountOriginalStateIconDrawablePreview = useCase

    @Provides
    @Singleton
    fun provideGetAlgoAssetName(
        useCase: GetAlgoAssetNameUseCase
    ): GetAlgoAssetName = useCase
}
