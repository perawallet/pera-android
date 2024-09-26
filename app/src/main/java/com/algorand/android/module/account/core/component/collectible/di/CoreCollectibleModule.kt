package com.algorand.android.module.account.core.component.collectible.di

import com.algorand.android.module.account.core.component.collectible.domain.mapper.*
import com.algorand.android.module.account.core.component.collectible.domain.usecase.*
import com.algorand.android.module.account.core.component.domain.usecase.*
import dagger.*
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
