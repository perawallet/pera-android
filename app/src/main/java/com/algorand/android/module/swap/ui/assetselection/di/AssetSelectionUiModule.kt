/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.module.swap.ui.assetselection.di

import com.algorand.android.module.swap.ui.assetselection.fromasset.usecase.GetSwapFromAssetSelectionPreview
import com.algorand.android.module.swap.ui.assetselection.fromasset.usecase.GetSwapFromAssetSelectionPreviewUseCase
import com.algorand.android.module.swap.ui.assetselection.mapper.SwapAssetSelectionItemMapper
import com.algorand.android.module.swap.ui.assetselection.mapper.SwapAssetSelectionItemMapperImpl
import com.algorand.android.module.swap.ui.assetselection.toasset.usecase.CreateSwapToAssetSelectionItemList
import com.algorand.android.module.swap.ui.assetselection.toasset.usecase.CreateSwapToToAssetSelectionItemListUseCase
import com.algorand.android.module.swap.ui.assetselection.toasset.usecase.GetSwapToAssetSelectionPreview
import com.algorand.android.module.swap.ui.assetselection.toasset.usecase.GetSwapToAssetSelectionPreviewUseCase
import com.algorand.android.module.swap.ui.assetselection.toasset.usecase.GetUpdatedPreviewWithAssetSelection
import com.algorand.android.module.swap.ui.assetselection.toasset.usecase.GetUpdatedPreviewWithAssetSelectionUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AssetSelectionUiModule {

    @Provides
    @Singleton
    fun provideSwapAssetSelectionItemMapper(
        mapper: SwapAssetSelectionItemMapperImpl
    ): SwapAssetSelectionItemMapper = mapper

    @Provides
    @Singleton
    fun provideCreateSwapAssetSelectionItemList(
        useCase: CreateSwapToToAssetSelectionItemListUseCase
    ): CreateSwapToAssetSelectionItemList = useCase

    @Provides
    @Singleton
    fun provideGetSwapToAssetSelectionPreview(
        useCase: GetSwapToAssetSelectionPreviewUseCase
    ): GetSwapToAssetSelectionPreview = useCase

    @Provides
    @Singleton
    fun provideGetSwapFromAssetSelectionPreview(
        useCase: GetSwapFromAssetSelectionPreviewUseCase
    ): GetSwapFromAssetSelectionPreview = useCase

    @Provides
    @Singleton
    fun provideGetUpdatedPreviewWithAssetSelection(
        useCase: GetUpdatedPreviewWithAssetSelectionUseCase
    ): GetUpdatedPreviewWithAssetSelection = useCase
}
