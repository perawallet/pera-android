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

package com.algorand.common.asset.di

import com.algorand.common.asset.data.mapper.entity.AssetDetailEntityMapper
import com.algorand.common.asset.data.mapper.entity.AssetDetailEntityMapperImpl
import com.algorand.common.asset.data.mapper.entity.CollectibleEntityMapper
import com.algorand.common.asset.data.mapper.entity.CollectibleEntityMapperImpl
import com.algorand.common.asset.data.mapper.entity.CollectibleMediaEntityMapper
import com.algorand.common.asset.data.mapper.entity.CollectibleMediaEntityMapperImpl
import com.algorand.common.asset.data.mapper.entity.CollectibleMediaTypeEntityMapper
import com.algorand.common.asset.data.mapper.entity.CollectibleMediaTypeEntityMapperImpl
import com.algorand.common.asset.data.mapper.entity.CollectibleMediaTypeExtensionEntityMapper
import com.algorand.common.asset.data.mapper.entity.CollectibleMediaTypeExtensionEntityMapperImpl
import com.algorand.common.asset.data.mapper.entity.CollectibleStandardTypeEntityMapper
import com.algorand.common.asset.data.mapper.entity.CollectibleStandardTypeEntityMapperImpl
import com.algorand.common.asset.data.mapper.entity.CollectibleTraitEntityMapper
import com.algorand.common.asset.data.mapper.entity.CollectibleTraitEntityMapperImpl
import com.algorand.common.asset.data.mapper.entity.VerificationTierEntityMapper
import com.algorand.common.asset.data.mapper.entity.VerificationTierEntityMapperImpl
import com.algorand.common.asset.data.mapper.model.AlgoAssetDetailMapper
import com.algorand.common.asset.data.mapper.model.AlgoAssetDetailMapperImpl
import com.algorand.common.asset.data.mapper.model.AssetCreatorMapper
import com.algorand.common.asset.data.mapper.model.AssetCreatorMapperImpl
import com.algorand.common.asset.data.mapper.model.AssetDetailMapper
import com.algorand.common.asset.data.mapper.model.AssetDetailMapperImpl
import com.algorand.common.asset.data.mapper.model.AssetInfoMapper
import com.algorand.common.asset.data.mapper.model.AssetInfoMapperImpl
import com.algorand.common.asset.data.mapper.model.AssetMapper
import com.algorand.common.asset.data.mapper.model.AssetMapperImpl
import com.algorand.common.asset.data.mapper.model.CollectibleInfoMapper
import com.algorand.common.asset.data.mapper.model.CollectibleInfoMapperImpl
import com.algorand.common.asset.data.mapper.model.VerificationTierMapper
import com.algorand.common.asset.data.mapper.model.VerificationTierMapperImpl
import com.algorand.common.asset.data.mapper.model.collectible.AudioCollectibleDetailMapper
import com.algorand.common.asset.data.mapper.model.collectible.AudioCollectibleDetailMapperImpl
import com.algorand.common.asset.data.mapper.model.collectible.CollectibleDetailMapper
import com.algorand.common.asset.data.mapper.model.collectible.CollectibleDetailMapperImpl
import com.algorand.common.asset.data.mapper.model.collectible.CollectibleMapper
import com.algorand.common.asset.data.mapper.model.collectible.CollectibleMapperImpl
import com.algorand.common.asset.data.mapper.model.collectible.CollectibleMediaMapper
import com.algorand.common.asset.data.mapper.model.collectible.CollectibleMediaMapperImpl
import com.algorand.common.asset.data.mapper.model.collectible.CollectibleMediaTypeExtensionMapper
import com.algorand.common.asset.data.mapper.model.collectible.CollectibleMediaTypeExtensionMapperImpl
import com.algorand.common.asset.data.mapper.model.collectible.CollectibleMediaTypeMapper
import com.algorand.common.asset.data.mapper.model.collectible.CollectibleMediaTypeMapperImpl
import com.algorand.common.asset.data.mapper.model.collectible.CollectibleSearchMapper
import com.algorand.common.asset.data.mapper.model.collectible.CollectibleSearchMapperImpl
import com.algorand.common.asset.data.mapper.model.collectible.CollectibleStandardTypeMapper
import com.algorand.common.asset.data.mapper.model.collectible.CollectibleStandardTypeMapperImpl
import com.algorand.common.asset.data.mapper.model.collectible.CollectibleTraitMapper
import com.algorand.common.asset.data.mapper.model.collectible.CollectibleTraitMapperImpl
import com.algorand.common.asset.data.mapper.model.collectible.CollectionMapper
import com.algorand.common.asset.data.mapper.model.collectible.CollectionMapperImpl
import com.algorand.common.asset.data.mapper.model.collectible.ImageCollectibleDetailMapper
import com.algorand.common.asset.data.mapper.model.collectible.ImageCollectibleDetailMapperImpl
import com.algorand.common.asset.data.mapper.model.collectible.MixedCollectibleDetailMapper
import com.algorand.common.asset.data.mapper.model.collectible.MixedCollectibleDetailMapperImpl
import com.algorand.common.asset.data.mapper.model.collectible.UnsupportedCollectibleDetailMapper
import com.algorand.common.asset.data.mapper.model.collectible.UnsupportedCollectibleDetailMapperImpl
import com.algorand.common.asset.data.mapper.model.collectible.VideoCollectibleDetailMapper
import com.algorand.common.asset.data.mapper.model.collectible.VideoCollectibleDetailMapperImpl
import org.koin.dsl.module

internal val assetDetailMappersModule = module {
    factory<AlgoAssetDetailMapper> { AlgoAssetDetailMapperImpl() }
    factory<AssetCreatorMapper> { AssetCreatorMapperImpl() }
    factory<AssetDetailEntityMapper> { AssetDetailEntityMapperImpl(get()) }
    factory<AssetDetailMapper> { AssetDetailMapperImpl(get(), get()) }
    factory<AssetInfoMapper> { AssetInfoMapperImpl() }
    factory<AssetMapper> { AssetMapperImpl(get(), get()) }
    factory<AudioCollectibleDetailMapper> { AudioCollectibleDetailMapperImpl(get(), get(), get()) }
    factory<CollectibleDetailMapper> { CollectibleDetailMapperImpl(get(), get(), get(), get(), get()) }
    factory<CollectibleEntityMapper> { CollectibleEntityMapperImpl(get(), get()) }
    factory<CollectibleInfoMapper> { CollectibleInfoMapperImpl(get()) }
    factory<CollectibleMapper> { CollectibleMapperImpl(get(), get(), get(), get(), get()) }
    factory<CollectibleMediaEntityMapper> { CollectibleMediaEntityMapperImpl(get(), get()) }
    factory<CollectibleMediaMapper> { CollectibleMediaMapperImpl(get(), get()) }
    factory<CollectibleMediaTypeEntityMapper> { CollectibleMediaTypeEntityMapperImpl() }
    factory<CollectibleMediaTypeExtensionEntityMapper> { CollectibleMediaTypeExtensionEntityMapperImpl() }
    factory<CollectibleMediaTypeExtensionMapper> { CollectibleMediaTypeExtensionMapperImpl() }
    factory<CollectibleMediaTypeMapper> { CollectibleMediaTypeMapperImpl() }
    factory<CollectibleSearchMapper> { CollectibleSearchMapperImpl() }
    factory<CollectibleStandardTypeEntityMapper> { CollectibleStandardTypeEntityMapperImpl() }
    factory<CollectibleStandardTypeMapper> { CollectibleStandardTypeMapperImpl() }
    factory<CollectibleTraitEntityMapper> { CollectibleTraitEntityMapperImpl() }
    factory<CollectibleTraitMapper> { CollectibleTraitMapperImpl() }
    factory<CollectionMapper> { CollectionMapperImpl() }
    factory<ImageCollectibleDetailMapper> { ImageCollectibleDetailMapperImpl(get(), get(), get()) }
    factory<MixedCollectibleDetailMapper> { MixedCollectibleDetailMapperImpl(get(), get(), get()) }
    factory<UnsupportedCollectibleDetailMapper> { UnsupportedCollectibleDetailMapperImpl(get(), get(), get()) }
    factory<VerificationTierEntityMapper> { VerificationTierEntityMapperImpl() }
    factory<VerificationTierMapper> { VerificationTierMapperImpl() }
    factory<VideoCollectibleDetailMapper> { VideoCollectibleDetailMapperImpl(get(), get(), get()) }
}
