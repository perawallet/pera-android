package com.algorand.android.module.asset.detail.component.collectible.di

import com.algorand.android.module.asset.detail.component.collectible.data.mapper.AudioCollectibleDetailMapper
import com.algorand.android.module.asset.detail.component.collectible.data.mapper.AudioCollectibleDetailMapperImpl
import com.algorand.android.module.asset.detail.component.collectible.data.mapper.CollectibleDetailMapper
import com.algorand.android.module.asset.detail.component.collectible.data.mapper.CollectibleDetailMapperImpl
import com.algorand.android.module.asset.detail.component.collectible.data.mapper.CollectibleMediaMapper
import com.algorand.android.module.asset.detail.component.collectible.data.mapper.CollectibleMediaMapperImpl
import com.algorand.android.module.asset.detail.component.collectible.data.mapper.ImageCollectibleDetailMapper
import com.algorand.android.module.asset.detail.component.collectible.data.mapper.ImageCollectibleDetailMapperImpl
import com.algorand.android.module.asset.detail.component.collectible.data.mapper.MixedCollectibleDetailMapper
import com.algorand.android.module.asset.detail.component.collectible.data.mapper.MixedCollectibleDetailMapperImpl
import com.algorand.android.module.asset.detail.component.collectible.data.mapper.UnsupportedCollectibleDetailMapper
import com.algorand.android.module.asset.detail.component.collectible.data.mapper.UnsupportedCollectibleDetailMapperImpl
import com.algorand.android.module.asset.detail.component.collectible.data.mapper.VideoCollectibleDetailMapper
import com.algorand.android.module.asset.detail.component.collectible.data.mapper.VideoCollectibleDetailMapperImpl
import com.algorand.android.module.asset.detail.component.collectible.data.repository.CollectibleDetailRepositoryImpl
import com.algorand.android.module.asset.detail.component.collectible.domain.repository.CollectibleDetailRepository
import com.algorand.android.module.asset.detail.component.collectible.domain.usecase.FetchCollectibleDetail
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object CollectibleModule {

    @Provides
    fun provideCollectibleDetailMapper(mapper: CollectibleDetailMapperImpl): CollectibleDetailMapper = mapper

    @Provides
    fun provideCollectibleMediaMapper(mapper: CollectibleMediaMapperImpl): CollectibleMediaMapper = mapper

    @Provides
    fun provideCollectibleDetailRepository(
        repository: CollectibleDetailRepositoryImpl
    ): CollectibleDetailRepository = repository

    @Provides
    fun provideGetCollectibleDetail(
        repository: CollectibleDetailRepository
    ): FetchCollectibleDetail = FetchCollectibleDetail(repository::fetchCollectibleDetail)

    @Provides
    @Singleton
    fun provideImageCollectibleDetailMapper(
        mapper: ImageCollectibleDetailMapperImpl
    ): ImageCollectibleDetailMapper = mapper

    @Provides
    @Singleton
    fun provideMixedCollectibleDetailMapper(
        mapper: MixedCollectibleDetailMapperImpl
    ): MixedCollectibleDetailMapper = mapper

    @Provides
    @Singleton
    fun provideVideoCollectibleDetailMapper(
        mapper: VideoCollectibleDetailMapperImpl
    ): VideoCollectibleDetailMapper = mapper

    @Provides
    @Singleton
    fun provideAudioCollectibleDetailMapper(
        mapper: AudioCollectibleDetailMapperImpl
    ): AudioCollectibleDetailMapper = mapper

    @Provides
    @Singleton
    fun provideUnsupportedCollectibleDetailMapper(
        mapper: UnsupportedCollectibleDetailMapperImpl
    ): UnsupportedCollectibleDetailMapper = mapper
}
