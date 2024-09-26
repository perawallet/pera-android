package com.algorand.android.module.asset.detail.component.asset.di

import com.algorand.android.module.asset.detail.component.asset.data.mapper.entity.AssetDetailEntityMapper
import com.algorand.android.module.asset.detail.component.asset.data.mapper.entity.AssetDetailEntityMapperImpl
import com.algorand.android.module.asset.detail.component.asset.data.mapper.entity.CollectibleEntityMapper
import com.algorand.android.module.asset.detail.component.asset.data.mapper.entity.CollectibleEntityMapperImpl
import com.algorand.android.module.asset.detail.component.asset.data.mapper.entity.CollectibleMediaEntityMapper
import com.algorand.android.module.asset.detail.component.asset.data.mapper.entity.CollectibleMediaEntityMapperImpl
import com.algorand.android.module.asset.detail.component.asset.data.mapper.entity.CollectibleMediaTypeEntityMapper
import com.algorand.android.module.asset.detail.component.asset.data.mapper.entity.CollectibleMediaTypeEntityMapperImpl
import com.algorand.android.module.asset.detail.component.asset.data.mapper.entity.CollectibleMediaTypeExtensionEntityMapper
import com.algorand.android.module.asset.detail.component.asset.data.mapper.entity.CollectibleMediaTypeExtensionEntityMapperImpl
import com.algorand.android.module.asset.detail.component.asset.data.mapper.entity.CollectibleStandardTypeEntityMapper
import com.algorand.android.module.asset.detail.component.asset.data.mapper.entity.CollectibleStandardTypeEntityMapperImpl
import com.algorand.android.module.asset.detail.component.asset.data.mapper.entity.CollectibleTraitEntityMapper
import com.algorand.android.module.asset.detail.component.asset.data.mapper.entity.CollectibleTraitEntityMapperImpl
import com.algorand.android.module.asset.detail.component.asset.data.mapper.entity.VerificationTierEntityMapper
import com.algorand.android.module.asset.detail.component.asset.data.mapper.entity.VerificationTierEntityMapperImpl
import com.algorand.android.module.asset.detail.component.asset.data.mapper.model.AlgoAssetDetailMapper
import com.algorand.android.module.asset.detail.component.asset.data.mapper.model.AlgoAssetDetailMapperImpl
import com.algorand.android.module.asset.detail.component.asset.data.mapper.model.AssetCreatorMapper
import com.algorand.android.module.asset.detail.component.asset.data.mapper.model.AssetCreatorMapperImpl
import com.algorand.android.module.asset.detail.component.asset.data.mapper.model.AssetDetailMapper
import com.algorand.android.module.asset.detail.component.asset.data.mapper.model.AssetDetailMapperImpl
import com.algorand.android.module.asset.detail.component.asset.data.mapper.model.AssetMapper
import com.algorand.android.module.asset.detail.component.asset.data.mapper.model.AssetMapperImpl
import com.algorand.android.module.asset.detail.component.asset.data.mapper.model.VerificationTierMapper
import com.algorand.android.module.asset.detail.component.asset.data.mapper.model.VerificationTierMapperImpl
import com.algorand.android.module.asset.detail.component.asset.data.mapper.model.collectible.CollectibleMapper
import com.algorand.android.module.asset.detail.component.asset.data.mapper.model.collectible.CollectibleMapperImpl
import com.algorand.android.module.asset.detail.component.asset.data.mapper.model.collectible.CollectibleMediaMapper
import com.algorand.android.module.asset.detail.component.asset.data.mapper.model.collectible.CollectibleMediaMapperImpl
import com.algorand.android.module.asset.detail.component.asset.data.mapper.model.collectible.CollectibleMediaTypeExtensionMapper
import com.algorand.android.module.asset.detail.component.asset.data.mapper.model.collectible.CollectibleMediaTypeExtensionMapperImpl
import com.algorand.android.module.asset.detail.component.asset.data.mapper.model.collectible.CollectibleMediaTypeMapper
import com.algorand.android.module.asset.detail.component.asset.data.mapper.model.collectible.CollectibleMediaTypeMapperImpl
import com.algorand.android.module.asset.detail.component.asset.data.mapper.model.collectible.CollectibleSearchMapper
import com.algorand.android.module.asset.detail.component.asset.data.mapper.model.collectible.CollectibleSearchMapperImpl
import com.algorand.android.module.asset.detail.component.asset.data.mapper.model.collectible.CollectibleStandardTypeMapper
import com.algorand.android.module.asset.detail.component.asset.data.mapper.model.collectible.CollectibleStandardTypeMapperImpl
import com.algorand.android.module.asset.detail.component.asset.data.mapper.model.collectible.CollectibleTraitMapper
import com.algorand.android.module.asset.detail.component.asset.data.mapper.model.collectible.CollectibleTraitMapperImpl
import com.algorand.android.module.asset.detail.component.asset.data.mapper.model.collectible.CollectionMapper
import com.algorand.android.module.asset.detail.component.asset.data.mapper.model.collectible.CollectionMapperImpl
import com.algorand.android.module.asset.detail.component.asset.data.repository.AssetDetailCacheHelper
import com.algorand.android.module.asset.detail.component.asset.data.repository.AssetDetailCacheHelperImpl
import com.algorand.android.module.asset.detail.component.asset.data.repository.AssetRepositoryImpl
import com.algorand.android.module.asset.detail.component.asset.data.service.AssetDetailApi
import com.algorand.android.module.asset.detail.component.asset.data.service.AssetDetailNodeApi
import com.algorand.android.module.asset.detail.component.asset.domain.repository.AssetRepository
import com.algorand.android.module.asset.detail.component.asset.domain.usecase.ClearAssetCache
import com.algorand.android.module.asset.detail.component.asset.domain.usecase.FetchAndCacheAssets
import com.algorand.android.module.asset.detail.component.asset.domain.usecase.FetchAsset
import com.algorand.android.module.asset.detail.component.asset.domain.usecase.FetchAssetDetailFromNode
import com.algorand.android.module.asset.detail.component.asset.domain.usecase.FetchAssets
import com.algorand.android.module.asset.detail.component.asset.domain.usecase.GetAsset
import com.algorand.android.module.asset.detail.component.asset.domain.usecase.GetAssetDetail
import com.algorand.android.module.asset.detail.component.asset.domain.usecase.GetCollectibleDetail
import com.algorand.android.module.asset.detail.component.asset.domain.usecase.GetCollectiblesDetail
import com.algorand.android.module.asset.detail.component.asset.domain.usecase.InitializeAssetDetailsUseCase
import com.algorand.android.module.asset.detail.component.asset.domain.usecase.InitializeAssets
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
internal object AssetDetailsModule {

    @Provides
    @Singleton
    fun provideInitializeAssetDetails(useCase: InitializeAssetDetailsUseCase): InitializeAssets = useCase

    @Provides
    @Singleton
    fun provideAssetDetailRepository(repository: AssetRepositoryImpl): AssetRepository = repository

    @Provides
    @Singleton
    fun provideAssetCreatorMapper(mapper: AssetCreatorMapperImpl): AssetCreatorMapper = mapper

    @Provides
    @Singleton
    fun provideAssetMapper(mapper: AssetMapperImpl): AssetMapper = mapper

    @Provides
    @Singleton
    fun provideVerificationTierMapper(mapper: VerificationTierMapperImpl): VerificationTierMapper = mapper

    @Provides
    @Singleton
    fun provideMobileAlgorandApi(
        @Named("mobileAlgorandRetrofitInterface") retrofit: Retrofit
    ): AssetDetailApi {
        return retrofit.create(AssetDetailApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAssetDetailNodeApi(
        @Named("algodRetrofitInterface") retrofit: Retrofit
    ): AssetDetailNodeApi {
        return retrofit.create(AssetDetailNodeApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAssetDetailCacheHelper(helper: AssetDetailCacheHelperImpl): AssetDetailCacheHelper = helper

    @Provides
    @Singleton
    fun provideCollectibleMapper(mapper: CollectibleMapperImpl): CollectibleMapper = mapper

    @Provides
    @Singleton
    fun provideCollectibleMediaMapper(mapper: CollectibleMediaMapperImpl): CollectibleMediaMapper = mapper

    @Provides
    @Singleton
    fun provideCollectibleMediaTypeExtensionMapper(
        mapper: CollectibleMediaTypeExtensionMapperImpl
    ): CollectibleMediaTypeExtensionMapper = mapper

    @Provides
    @Singleton
    fun provideCollectibleMediaTypeMapper(mapper: CollectibleMediaTypeMapperImpl): CollectibleMediaTypeMapper = mapper

    @Provides
    @Singleton
    fun provideCollectibleSearchMapper(mapper: CollectibleSearchMapperImpl): CollectibleSearchMapper = mapper

    @Provides
    @Singleton
    fun provideCollectibleStandardTypeMapper(
        mapper: CollectibleStandardTypeMapperImpl
    ): CollectibleStandardTypeMapper = mapper

    @Provides
    @Singleton
    fun provideCollectibleTraitMapper(mapper: CollectibleTraitMapperImpl): CollectibleTraitMapper = mapper

    @Provides
    @Singleton
    fun provideCollectionMapper(mapper: CollectionMapperImpl): CollectionMapper = mapper

    @Provides
    @Singleton
    fun provideAssetDetailEntityMapper(mapper: AssetDetailEntityMapperImpl): AssetDetailEntityMapper = mapper

    @Provides
    @Singleton
    fun provideCollectibleEntityMapper(mapper: CollectibleEntityMapperImpl): CollectibleEntityMapper =
        mapper

    @Provides
    @Singleton
    fun provideCollectibleMediaEntityMapper(
        mapper: CollectibleMediaEntityMapperImpl
    ): CollectibleMediaEntityMapper = mapper

    @Provides
    @Singleton
    fun provideCollectibleTraitEntityMapper(
        mapper: CollectibleTraitEntityMapperImpl
    ): CollectibleTraitEntityMapper = mapper

    @Provides
    @Singleton
    fun provideVerificationTierEntityMapper(
        mapper: VerificationTierEntityMapperImpl
    ): VerificationTierEntityMapper = mapper

    @Provides
    @Singleton
    fun provideCollectibleStandardTypeEntityMapper(
        mapper: CollectibleStandardTypeEntityMapperImpl
    ): CollectibleStandardTypeEntityMapper = mapper

    @Provides
    @Singleton
    fun provideCollectibleMediaTypeEntityMapper(
        mapper: CollectibleMediaTypeEntityMapperImpl
    ): CollectibleMediaTypeEntityMapper = mapper

    @Provides
    @Singleton
    fun provideCollectibleMediaTypeExtensionEntityMapper(
        mapper: CollectibleMediaTypeExtensionEntityMapperImpl
    ): CollectibleMediaTypeExtensionEntityMapper = mapper

    @Provides
    @Singleton
    fun provideFetchAndCacheAssetDetails(
        assetRepository: AssetRepository
    ): FetchAndCacheAssets = FetchAndCacheAssets(assetRepository::fetchAndCacheAssets)

    @Provides
    @Singleton
    fun provideGetAssetDetail(
        repository: AssetRepository
    ): GetAssetDetail = GetAssetDetail(repository::getAssetDetail)

    @Provides
    @Singleton
    fun provideGetCollectibleDetail(
        repository: AssetRepository
    ): GetCollectibleDetail = GetCollectibleDetail(repository::getCollectibleDetail)

    @Provides
    @Singleton
    fun provideGetAsset(repository: AssetRepository): GetAsset = GetAsset(repository::getAsset)

    @Provides
    @Singleton
    fun provideAlgoAssetDetailMapper(
        mapper: AlgoAssetDetailMapperImpl
    ): AlgoAssetDetailMapper = mapper

    @Provides
    @Singleton
    fun provideCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    fun provideFetchAssetDetail(
        assetRepository: AssetRepository
    ): FetchAsset = FetchAsset(assetRepository::fetchAsset)

    @Provides
    @Singleton
    fun provideFetchAssetDetailFromNode(
        assetRepository: AssetRepository
    ): FetchAssetDetailFromNode = FetchAssetDetailFromNode(assetRepository::fetchAssetDetailFromNode)

    @Provides
    @Singleton
    fun provideFetchAssetsDetail(
        assetRepository: AssetRepository
    ): FetchAssets = FetchAssets(assetRepository::fetchAssets)

    @Provides
    @Singleton
    fun provideClearAssetDetailCache(
        assetRepository: AssetRepository
    ): ClearAssetCache = ClearAssetCache(assetRepository::clearCache)

    @Provides
    @Singleton
    fun provideGetCollectiblesDetail(
        assetRepository: AssetRepository
    ): GetCollectiblesDetail = GetCollectiblesDetail(assetRepository::getCollectiblesDetail)

    @Provides
    @Singleton
    fun provideAssetDetailMapper(impl: AssetDetailMapperImpl): AssetDetailMapper = impl
}
