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

package com.algorand.android.ui.asset.di

import com.algorand.android.ui.asset.detail.usecase.GetAssetLineChartData
import com.algorand.android.ui.asset.detail.usecase.GetAssetLineChartDataUseCase
import com.algorand.android.ui.asset.lite.mapper.AssetListItemBalanceMapper
import com.algorand.android.ui.asset.lite.mapper.AssetListItemMapper
import com.algorand.android.ui.asset.lite.mapper.DefaultAssetListItemBalanceMapper
import com.algorand.android.ui.asset.lite.mapper.DefaultAssetListItemMapper
import com.algorand.android.ui.asset.lite.usecase.GetPaginatedAssetListItems
import com.algorand.android.ui.asset.lite.usecase.GetPaginatedAssetListItemsUseCase
import com.algorand.android.ui.compose.widget.asset.icon.mapper.AssetIconDrawableMapper
import com.algorand.android.ui.compose.widget.asset.icon.mapper.DefaultAssetIconDrawableMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object AssetUiModule {

    @Provides
    fun provideGetAssetLineChartData(useCase: GetAssetLineChartDataUseCase): GetAssetLineChartData = useCase

    @Provides
    fun provideAssetIconDrawableMapper(mapper: DefaultAssetIconDrawableMapper): AssetIconDrawableMapper = mapper

    @Provides
    fun provideGetPaginatedAssetListItems(
        useCase: GetPaginatedAssetListItemsUseCase
    ): GetPaginatedAssetListItems = useCase

    @Provides
    fun provideAssetListItemMapper(mapper: DefaultAssetListItemMapper): AssetListItemMapper = mapper

    @Provides
    fun provideAssetListItemBalanceMapper(
        mapper: DefaultAssetListItemBalanceMapper
    ): AssetListItemBalanceMapper = mapper
}
