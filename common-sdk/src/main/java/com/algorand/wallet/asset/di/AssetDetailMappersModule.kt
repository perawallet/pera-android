/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.wallet.asset.di

import com.algorand.wallet.asset.data.mapper.entity.AssetDetailEntityMapper
import com.algorand.wallet.asset.data.mapper.entity.AssetDetailEntityMapperImpl
import com.algorand.wallet.asset.data.mapper.entity.CollectibleEntityMapper
import com.algorand.wallet.asset.data.mapper.entity.CollectibleEntityMapperImpl
import com.algorand.wallet.asset.data.mapper.entity.CollectibleMediaEntityMapper
import com.algorand.wallet.asset.data.mapper.entity.CollectibleMediaEntityMapperImpl
import com.algorand.wallet.asset.data.mapper.entity.CollectibleMediaTypeEntityMapper
import com.algorand.wallet.asset.data.mapper.entity.CollectibleMediaTypeEntityMapperImpl
import com.algorand.wallet.asset.data.mapper.entity.CollectibleMediaTypeExtensionEntityMapper
import com.algorand.wallet.asset.data.mapper.entity.CollectibleMediaTypeExtensionEntityMapperImpl
import com.algorand.wallet.asset.data.mapper.entity.CollectibleStandardTypeEntityMapper
import com.algorand.wallet.asset.data.mapper.entity.CollectibleStandardTypeEntityMapperImpl
import com.algorand.wallet.asset.data.mapper.entity.CollectibleTraitEntityMapper
import com.algorand.wallet.asset.data.mapper.entity.CollectibleTraitEntityMapperImpl
import com.algorand.wallet.asset.data.mapper.entity.VerificationTierEntityMapper
import com.algorand.wallet.asset.data.mapper.entity.VerificationTierEntityMapperImpl
import com.algorand.wallet.asset.data.mapper.model.AlgoAssetDetailMapper
import com.algorand.wallet.asset.data.mapper.model.AlgoAssetDetailMapperImpl
import com.algorand.wallet.asset.data.mapper.model.AssetCreatorMapper
import com.algorand.wallet.asset.data.mapper.model.AssetCreatorMapperImpl
import com.algorand.wallet.asset.data.mapper.model.AssetDetailMapper
import com.algorand.wallet.asset.data.mapper.model.AssetDetailMapperImpl
import com.algorand.wallet.asset.data.mapper.model.AssetInfoMapper
import com.algorand.wallet.asset.data.mapper.model.AssetInfoMapperImpl
import com.algorand.wallet.asset.data.mapper.model.AssetMapper
import com.algorand.wallet.asset.data.mapper.model.AssetMapperImpl
import com.algorand.wallet.asset.data.mapper.model.CollectibleInfoMapper
import com.algorand.wallet.asset.data.mapper.model.CollectibleInfoMapperImpl
import com.algorand.wallet.asset.data.mapper.model.VerificationTierMapper
import com.algorand.wallet.asset.data.mapper.model.VerificationTierMapperImpl
import com.algorand.wallet.asset.data.mapper.model.collectible.AudioCollectibleDetailMapper
import com.algorand.wallet.asset.data.mapper.model.collectible.AudioCollectibleDetailMapperImpl
import com.algorand.wallet.asset.data.mapper.model.collectible.CollectibleDetailMapper
import com.algorand.wallet.asset.data.mapper.model.collectible.CollectibleDetailMapperImpl
import com.algorand.wallet.asset.data.mapper.model.collectible.CollectibleMapper
import com.algorand.wallet.asset.data.mapper.model.collectible.CollectibleMapperImpl
import com.algorand.wallet.asset.data.mapper.model.collectible.CollectibleMediaMapper
import com.algorand.wallet.asset.data.mapper.model.collectible.CollectibleMediaMapperImpl
import com.algorand.wallet.asset.data.mapper.model.collectible.CollectibleMediaTypeExtensionMapper
import com.algorand.wallet.asset.data.mapper.model.collectible.CollectibleMediaTypeExtensionMapperImpl
import com.algorand.wallet.asset.data.mapper.model.collectible.CollectibleMediaTypeMapper
import com.algorand.wallet.asset.data.mapper.model.collectible.CollectibleMediaTypeMapperImpl
import com.algorand.wallet.asset.data.mapper.model.collectible.CollectibleSearchMapper
import com.algorand.wallet.asset.data.mapper.model.collectible.CollectibleSearchMapperImpl
import com.algorand.wallet.asset.data.mapper.model.collectible.CollectibleStandardTypeMapper
import com.algorand.wallet.asset.data.mapper.model.collectible.CollectibleStandardTypeMapperImpl
import com.algorand.wallet.asset.data.mapper.model.collectible.CollectibleTraitMapper
import com.algorand.wallet.asset.data.mapper.model.collectible.CollectibleTraitMapperImpl
import com.algorand.wallet.asset.data.mapper.model.collectible.CollectionMapper
import com.algorand.wallet.asset.data.mapper.model.collectible.CollectionMapperImpl
import com.algorand.wallet.asset.data.mapper.model.collectible.ImageCollectibleDetailMapper
import com.algorand.wallet.asset.data.mapper.model.collectible.ImageCollectibleDetailMapperImpl
import com.algorand.wallet.asset.data.mapper.model.collectible.MixedCollectibleDetailMapper
import com.algorand.wallet.asset.data.mapper.model.collectible.MixedCollectibleDetailMapperImpl
import com.algorand.wallet.asset.data.mapper.model.collectible.UnsupportedCollectibleDetailMapper
import com.algorand.wallet.asset.data.mapper.model.collectible.UnsupportedCollectibleDetailMapperImpl
import com.algorand.wallet.asset.data.mapper.model.collectible.VideoCollectibleDetailMapper
import com.algorand.wallet.asset.data.mapper.model.collectible.VideoCollectibleDetailMapperImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object AssetDetailMappersModule {

    @Provides
    fun provideAlgoAssetDetailMapper(impl: AlgoAssetDetailMapperImpl): AlgoAssetDetailMapper = impl

    @Provides
    fun provideAssetCreatorMapper(impl: AssetCreatorMapperImpl): AssetCreatorMapper = impl

    @Provides
    fun provideAssetDetailEntityMapper(impl: AssetDetailEntityMapperImpl): AssetDetailEntityMapper = impl

    @Provides
    fun provideAssetDetailMapper(impl: AssetDetailMapperImpl): AssetDetailMapper = impl

    @Provides
    fun provideAssetInfoMapper(impl: AssetInfoMapperImpl): AssetInfoMapper = impl

    @Provides
    fun provideAssetMapper(impl: AssetMapperImpl): AssetMapper = impl

    @Provides
    fun provideAudioCollectibleDetailMapper(impl: AudioCollectibleDetailMapperImpl): AudioCollectibleDetailMapper = impl

    @Provides
    fun provideCollectibleDetailMapper(impl: CollectibleDetailMapperImpl): CollectibleDetailMapper = impl

    @Provides
    fun provideCollectibleEntityMapper(impl: CollectibleEntityMapperImpl): CollectibleEntityMapper = impl

    @Provides
    fun provideCollectibleInfoMapper(impl: CollectibleInfoMapperImpl): CollectibleInfoMapper = impl

    @Provides
    fun provideCollectibleMapper(impl: CollectibleMapperImpl): CollectibleMapper = impl

    @Provides
    fun provideCollectibleMediaEntityMapper(impl: CollectibleMediaEntityMapperImpl): CollectibleMediaEntityMapper = impl

    @Provides
    fun provideCollectibleMediaMapper(impl: CollectibleMediaMapperImpl): CollectibleMediaMapper = impl

    @Provides
    fun provideCollectibleMediaTypeEntityMapper(
        impl: CollectibleMediaTypeEntityMapperImpl
    ): CollectibleMediaTypeEntityMapper = impl

    @Provides
    fun provideCollectibleMediaTypeExtensionEntityMapper(
        impl: CollectibleMediaTypeExtensionEntityMapperImpl
    ): CollectibleMediaTypeExtensionEntityMapper = impl

    @Provides
    fun provideCollectibleMediaTypeExtensionMapper(
        impl: CollectibleMediaTypeExtensionMapperImpl
    ): CollectibleMediaTypeExtensionMapper = impl

    @Provides
    fun provideCollectibleMediaTypeMapper(impl: CollectibleMediaTypeMapperImpl): CollectibleMediaTypeMapper = impl

    @Provides
    fun provideCollectibleSearchMapper(impl: CollectibleSearchMapperImpl): CollectibleSearchMapper = impl

    @Provides
    fun provideCollectibleStandardTypeEntityMapper(
        impl: CollectibleStandardTypeEntityMapperImpl
    ): CollectibleStandardTypeEntityMapper = impl

    @Provides
    fun provideCollectibleStandardTypeMapper(
        impl: CollectibleStandardTypeMapperImpl
    ): CollectibleStandardTypeMapper = impl

    @Provides
    fun provideCollectibleTraitEntityMapper(impl: CollectibleTraitEntityMapperImpl): CollectibleTraitEntityMapper = impl

    @Provides
    fun provideCollectibleTraitMapper(impl: CollectibleTraitMapperImpl): CollectibleTraitMapper = impl

    @Provides
    fun provideCollectionMapper(impl: CollectionMapperImpl): CollectionMapper = impl

    @Provides
    fun provideImageCollectibleDetailMapper(impl: ImageCollectibleDetailMapperImpl): ImageCollectibleDetailMapper = impl

    @Provides
    fun provideMixedCollectibleDetailMapper(impl: MixedCollectibleDetailMapperImpl): MixedCollectibleDetailMapper = impl

    @Provides
    fun provideUnsupportedCollectibleDetailMapper(
        impl: UnsupportedCollectibleDetailMapperImpl
    ): UnsupportedCollectibleDetailMapper = impl

    @Provides
    fun provideVerificationTierEntityMapper(impl: VerificationTierEntityMapperImpl): VerificationTierEntityMapper = impl

    @Provides
    fun provideVerificationTierMapper(impl: VerificationTierMapperImpl): VerificationTierMapper = impl

    @Provides
    fun provideVideoCollectibleDetailMapper(impl: VideoCollectibleDetailMapperImpl): VideoCollectibleDetailMapper = impl
}
