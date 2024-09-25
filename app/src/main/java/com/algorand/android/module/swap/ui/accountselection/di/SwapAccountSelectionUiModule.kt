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

package com.algorand.android.module.swap.ui.accountselection.di

import com.algorand.android.module.swap.ui.accountselection.usecase.GetSwapAccountSelectedUpdatedPreview
import com.algorand.android.module.swap.ui.accountselection.usecase.GetSwapAccountSelectedUpdatedPreviewUseCase
import com.algorand.android.module.swap.ui.accountselection.usecase.GetSwapAccountSelectionAssetAddedPreview
import com.algorand.android.module.swap.ui.accountselection.usecase.GetSwapAccountSelectionAssetAddedPreviewUseCase
import com.algorand.android.module.swap.ui.accountselection.usecase.GetSwapAccountSelectionInitialPreview
import com.algorand.android.module.swap.ui.accountselection.usecase.GetSwapAccountSelectionInitialPreviewUseCase
import com.algorand.android.module.swap.ui.accountselection.usecase.GetSwapAccountSelectionPreview
import com.algorand.android.module.swap.ui.accountselection.usecase.GetSwapAccountSelectionPreviewUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object SwapAccountSelectionUiModule {

    @Provides
    @Singleton
    fun provideGetSwapAccountSelectionInitialPreview(
        useCase: GetSwapAccountSelectionInitialPreviewUseCase
    ): GetSwapAccountSelectionInitialPreview = useCase

    @Provides
    @Singleton
    fun provideGetSwapAccountSelectedUpdatedPreview(
        useCase: GetSwapAccountSelectedUpdatedPreviewUseCase
    ): GetSwapAccountSelectedUpdatedPreview = useCase

    @Provides
    @Singleton
    fun provideGetSwapAccountSelectionAssetAddedPreview(
        useCase: GetSwapAccountSelectionAssetAddedPreviewUseCase
    ): GetSwapAccountSelectionAssetAddedPreview = useCase

    @Provides
    @Singleton
    fun provideGetSwapAccountSelectionPreview(
        useCase: GetSwapAccountSelectionPreviewUseCase
    ): GetSwapAccountSelectionPreview = useCase
}
