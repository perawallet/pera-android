package com.algorand.android.module.account.core.component.collectible.di

import com.algorand.android.module.account.core.component.collectible.domain.mapper.BaseOwnedCollectibleDataFactory
import com.algorand.android.module.account.core.component.collectible.domain.mapper.BaseOwnedCollectibleDataFactoryImpl
import com.algorand.android.module.account.core.component.collectible.domain.mapper.OwnedCollectibleAudioDataMapper
import com.algorand.android.module.account.core.component.collectible.domain.mapper.OwnedCollectibleAudioDataMapperImpl
import com.algorand.android.module.account.core.component.collectible.domain.mapper.OwnedCollectibleImageDataMapper
import com.algorand.android.module.account.core.component.collectible.domain.mapper.OwnedCollectibleImageDataMapperImpl
import com.algorand.android.module.account.core.component.collectible.domain.mapper.OwnedCollectibleMixedDataMapper
import com.algorand.android.module.account.core.component.collectible.domain.mapper.OwnedCollectibleMixedDataMapperImpl
import com.algorand.android.module.account.core.component.collectible.domain.mapper.OwnedCollectibleNotSupportedDataMapper
import com.algorand.android.module.account.core.component.collectible.domain.mapper.OwnedCollectibleNotSupportedDataMapperImpl
import com.algorand.android.module.account.core.component.collectible.domain.mapper.OwnedCollectibleVideoDataMapper
import com.algorand.android.module.account.core.component.collectible.domain.mapper.OwnedCollectibleVideoDataMapperImpl
import com.algorand.android.module.account.core.component.collectible.domain.usecase.GetAccountCollectibleDetail
import com.algorand.android.module.account.core.component.collectible.domain.usecase.GetAccountCollectibleDetailUseCase
import com.algorand.android.module.account.core.component.collectible.domain.usecase.GetAllAccountsAllCollectibleDataFlow
import com.algorand.android.module.account.core.component.collectible.domain.usecase.GetAllAccountsAllCollectibleDataFlowUseCase
import com.algorand.android.module.account.core.component.domain.usecase.GetAccountCollectibleDataFlow
import com.algorand.android.module.account.core.component.domain.usecase.GetAccountCollectibleDataFlowUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object CoreCollectibleModule {

    @Provides
    fun provideOwnedCollectibleImageDataMapper(
        mapper: OwnedCollectibleImageDataMapperImpl
    ): OwnedCollectibleImageDataMapper = mapper

    @Provides
    fun provideOwnedCollectibleVideoDataMapper(
        mapper: OwnedCollectibleVideoDataMapperImpl
    ): OwnedCollectibleVideoDataMapper = mapper

    @Provides
    fun provideOwnedCollectibleAudioDataMapper(
        mapper: OwnedCollectibleAudioDataMapperImpl
    ): OwnedCollectibleAudioDataMapper = mapper

    @Provides
    fun provideOwnedCollectibleMixedDataMapper(
        mapper: OwnedCollectibleMixedDataMapperImpl
    ): OwnedCollectibleMixedDataMapper = mapper

    @Provides
    fun provideOwnedCollectibleNotSupportedDataMapper(
        mapper: OwnedCollectibleNotSupportedDataMapperImpl
    ): OwnedCollectibleNotSupportedDataMapper = mapper

    @Provides
    fun provideBaseOwnedCollectibleDataFactory(
        factory: BaseOwnedCollectibleDataFactoryImpl
    ): BaseOwnedCollectibleDataFactory = factory

    @Provides
    fun provideGetAccountCollectibleDetail(
        useCase: GetAccountCollectibleDetailUseCase
    ): GetAccountCollectibleDetail = useCase

    @Provides
    @Singleton
    fun provideGetAccountCollectibleDataFlow(
        useCase: GetAccountCollectibleDataFlowUseCase
    ): GetAccountCollectibleDataFlow = useCase

    @Provides
    @Singleton
    fun provideGetAllAccountsAllCollectibleDataFlow(
        useCase: GetAllAccountsAllCollectibleDataFlowUseCase
    ): GetAllAccountsAllCollectibleDataFlow = useCase
}
