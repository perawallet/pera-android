/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.collectibles.common.di

import com.algorand.android.modules.accountcore.domain.usecase.GetAccountCollectibleDataFlow
import com.algorand.android.modules.accountcore.domain.usecase.GetAccountCollectibleDataFlowUseCase
import com.algorand.android.modules.collectibles.common.mapper.BaseOwnedCollectibleDataFactory
import com.algorand.android.modules.collectibles.common.mapper.BaseOwnedCollectibleDataFactoryImpl
import com.algorand.android.modules.collectibles.common.mapper.OwnedCollectibleAudioDataMapper
import com.algorand.android.modules.collectibles.common.mapper.OwnedCollectibleAudioDataMapperImpl
import com.algorand.android.modules.collectibles.common.mapper.OwnedCollectibleImageDataMapper
import com.algorand.android.modules.collectibles.common.mapper.OwnedCollectibleImageDataMapperImpl
import com.algorand.android.modules.collectibles.common.mapper.OwnedCollectibleMixedDataMapper
import com.algorand.android.modules.collectibles.common.mapper.OwnedCollectibleMixedDataMapperImpl
import com.algorand.android.modules.collectibles.common.mapper.OwnedCollectibleNotSupportedDataMapper
import com.algorand.android.modules.collectibles.common.mapper.OwnedCollectibleNotSupportedDataMapperImpl
import com.algorand.android.modules.collectibles.common.mapper.OwnedCollectibleVideoDataMapper
import com.algorand.android.modules.collectibles.common.mapper.OwnedCollectibleVideoDataMapperImpl
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
    @Singleton
    fun provideGetAccountCollectibleDataFlow(
        useCase: GetAccountCollectibleDataFlowUseCase
    ): GetAccountCollectibleDataFlow = useCase
}
