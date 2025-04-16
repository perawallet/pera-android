/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.swap.accountselection.di

import com.algorand.android.modules.swap.accountselection.ui.usecase.GetSwapAccountSelectedUpdatedPreview
import com.algorand.android.modules.swap.accountselection.ui.usecase.GetSwapAccountSelectedUpdatedPreviewUseCase
import com.algorand.android.modules.swap.accountselection.ui.usecase.GetSwapAccountSelectionAssetAddedPreview
import com.algorand.android.modules.swap.accountselection.ui.usecase.GetSwapAccountSelectionAssetAddedPreviewUseCase
import com.algorand.android.modules.swap.accountselection.ui.usecase.GetSwapAccountSelectionPreview
import com.algorand.android.modules.swap.accountselection.ui.usecase.GetSwapAccountSelectionPreviewUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object SwapAccountSelectionModule {

    @Provides
    fun provideGetSwapAccountSelectionPreview(
        useCase: GetSwapAccountSelectionPreviewUseCase
    ): GetSwapAccountSelectionPreview = useCase

    @Provides
    fun provideGetSwapAccountSelectedUpdatedPreview(
        useCase: GetSwapAccountSelectedUpdatedPreviewUseCase
    ): GetSwapAccountSelectedUpdatedPreview = useCase

    @Provides
    fun provideGetSwapAccountSelectionAssetAddedPreview(
        useCase: GetSwapAccountSelectionAssetAddedPreviewUseCase
    ): GetSwapAccountSelectionAssetAddedPreview = useCase
}
