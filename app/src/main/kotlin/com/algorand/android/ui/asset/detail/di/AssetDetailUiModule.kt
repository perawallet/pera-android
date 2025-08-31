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

package com.algorand.android.ui.asset.detail.di

import com.algorand.android.ui.asset.detail.mapper.AssetMarketsDetailBadgeDescriptionMapper
import com.algorand.android.ui.asset.detail.mapper.DefaultAssetMarketsDetailBadgeDescriptionMapper
import com.algorand.android.ui.asset.detail.usecase.GetAssetDetailQuickActionItems
import com.algorand.android.ui.asset.detail.usecase.GetAssetDetailQuickActionItemsUseCase
import com.algorand.android.ui.asset.detail.usecase.GetAssetMarketsDetail
import com.algorand.android.ui.asset.detail.usecase.GetAssetMarketsDetailUseCase
import com.algorand.android.ui.asset.detail.usecase.GetAssetPriceLineChartData
import com.algorand.android.ui.asset.detail.usecase.GetAssetPriceLineChartDataUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object AssetDetailUiModule {

    @Provides
    fun provideGetAssetDetailQuickActionItems(
        useCase: GetAssetDetailQuickActionItemsUseCase
    ): GetAssetDetailQuickActionItems = useCase

    @Provides
    fun provideGetAssetMarketsDetail(useCase: GetAssetMarketsDetailUseCase): GetAssetMarketsDetail = useCase

    @Provides
    fun provideGetAssetPriceLineChartData(
        useCase: GetAssetPriceLineChartDataUseCase
    ): GetAssetPriceLineChartData = useCase

    @Provides
    fun provideAssetMarketsDetailBadgeDescriptionMapper(
        mapper: DefaultAssetMarketsDetailBadgeDescriptionMapper
    ): AssetMarketsDetailBadgeDescriptionMapper = mapper
}
