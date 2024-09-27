package com.algorand.android.module.account.core.ui.di

import android.content.Context
import com.algorand.android.module.account.core.component.detail.domain.usecase.GetAccountDetail
import com.algorand.android.module.account.core.ui.mapper.AccountItemConfigurationMapper
import com.algorand.android.module.account.core.ui.mapper.AccountItemConfigurationMapperImpl
import com.algorand.android.module.account.core.ui.mapper.VerificationTierConfigurationMapper
import com.algorand.android.module.account.core.ui.mapper.VerificationTierConfigurationMapperImpl
import com.algorand.android.module.account.core.ui.usecase.GetAccountDisplayName
import com.algorand.android.module.account.core.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.module.account.core.ui.usecase.GetAccountIconResourceByAccountType
import com.algorand.android.module.account.core.ui.usecase.GetAccountOriginalStateIconDrawablePreview
import com.algorand.android.module.account.core.ui.usecase.GetAlgoAssetName
import com.algorand.android.module.account.core.ui.usecase.GetAssetName
import com.algorand.android.module.account.core.ui.usecase.implementation.GetAccountDisplayNameUseCase
import com.algorand.android.module.account.core.ui.usecase.implementation.GetAccountIconDrawablePreviewUseCase
import com.algorand.android.module.account.core.ui.usecase.implementation.GetAccountIconResourceByAccountTypeUseCase
import com.algorand.android.module.account.core.ui.usecase.implementation.GetAccountOriginalStateIconDrawablePreviewUseCase
import com.algorand.android.module.account.core.ui.usecase.implementation.GetAlgoAssetNameUseCase
import com.algorand.android.module.account.core.ui.usecase.implementation.GetAssetNameUseCase
import com.algorand.android.module.custominfo.domain.usecase.GetCustomInfoOrNull
import com.algorand.android.module.nameservice.domain.usecase.GetAccountNameService
import dagger.Module
import dagger.Provides
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
