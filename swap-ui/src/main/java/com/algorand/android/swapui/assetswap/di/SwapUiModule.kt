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

package com.algorand.android.swapui.assetswap.di

import android.content.Context
import com.algorand.android.core.component.assetdata.usecase.GetAccountOwnedAssetData
import com.algorand.android.core.component.domain.usecase.GetAccountMinBalance
import com.algorand.android.swapui.GetSwapError
import com.algorand.android.swapui.GetSwapErrorImpl
import com.algorand.android.swapui.assetswap.mapper.GetSwapQuoteUpdatedPreviewPayloadMapper
import com.algorand.android.swapui.assetswap.mapper.GetSwapQuoteUpdatedPreviewPayloadMapperImpl
import com.algorand.android.swapui.assetswap.usecase.GetAssetSwapSwitchButtonStatus
import com.algorand.android.swapui.assetswap.usecase.GetAssetSwapSwitchButtonStatusUseCase
import com.algorand.android.swapui.assetswap.usecase.GetAssetsSwitchedUpdatedPreviewUseCase
import com.algorand.android.swapui.assetswap.usecase.GetFromSelectedAssetAmountDetail
import com.algorand.android.swapui.assetswap.usecase.GetFromSelectedAssetAmountDetailUseCase
import com.algorand.android.swapui.assetswap.usecase.GetFromSelectedAssetDetail
import com.algorand.android.swapui.assetswap.usecase.GetFromSelectedAssetDetailUseCase
import com.algorand.android.swapui.assetswap.usecase.GetSelectedAssetAmountDetail
import com.algorand.android.swapui.assetswap.usecase.GetSelectedAssetAmountDetailUseCase
import com.algorand.android.swapui.assetswap.usecase.GetSelectedAssetDetail
import com.algorand.android.swapui.assetswap.usecase.GetSelectedAssetDetailUseCase
import com.algorand.android.swapui.assetswap.usecase.GetSwapAmountUpdatedPreviewUseCase
import com.algorand.android.swapui.assetswap.usecase.GetSwapBalanceError
import com.algorand.android.swapui.assetswap.usecase.GetSwapBalanceErrorUseCase
import com.algorand.android.swapui.assetswap.usecase.GetSwapInitialPreviewUseCase
import com.algorand.android.swapui.assetswap.usecase.GetSwapQuoteUpdatedPreview
import com.algorand.android.swapui.assetswap.usecase.GetSwapQuoteUpdatedPreviewUseCase
import com.algorand.android.swapui.assetswap.usecase.GetToAssetUpdatedPreviewUseCase
import com.algorand.android.swapui.assetswap.usecase.GetToSelectedAssetAmountDetail
import com.algorand.android.swapui.assetswap.usecase.GetToSelectedAssetAmountDetailUseCase
import com.algorand.android.swapui.assetswap.usecase.GetToSelectedAssetDetail
import com.algorand.android.swapui.assetswap.usecase.GetToSelectedAssetDetailUseCase
import com.algorand.android.swapui.assetswap.usecase.main.AssetSwapPreviewProcessor
import com.algorand.android.swapui.assetswap.usecase.main.AssetSwapPreviewProcessorImpl
import com.algorand.android.swapui.assetswap.usecase.main.GetAssetsSwitchedUpdatedPreview
import com.algorand.android.swapui.assetswap.usecase.main.GetFromAssetUpdatedPreview
import com.algorand.android.swapui.assetswap.usecase.main.GetFromAssetUpdatedPreviewUseCase
import com.algorand.android.swapui.assetswap.usecase.main.GetSwapAmountUpdatedPreview
import com.algorand.android.swapui.assetswap.usecase.main.GetSwapInitialPreview
import com.algorand.android.swapui.assetswap.usecase.main.GetToAssetUpdatedPreview
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object SwapUiModule {

    @Provides
    @Singleton
    fun provideGetSwapQuoteUpdatedPreview(
        useCase: GetSwapQuoteUpdatedPreviewUseCase
    ): GetSwapQuoteUpdatedPreview = useCase

    @Provides
    @Singleton
    fun provideGetSwapInitialPreview(
        useCase: GetSwapInitialPreviewUseCase
    ): GetSwapInitialPreview = useCase

    @Provides
    @Singleton
    fun provideGetToAssetUpdatedPreview(
        useCase: GetToAssetUpdatedPreviewUseCase
    ): GetToAssetUpdatedPreview = useCase

    @Provides
    @Singleton
    fun provideGetToSelectedAssetDetail(
        useCase: GetToSelectedAssetDetailUseCase
    ): GetToSelectedAssetDetail = useCase

    @Provides
    @Singleton
    fun provideAssetSwapPreviewProcessor(
        impl: AssetSwapPreviewProcessorImpl
    ): AssetSwapPreviewProcessor = impl

    @Provides
    @Singleton
    fun provideGetAssetsSwitchedUpdatedPreview(
        useCase: GetAssetsSwitchedUpdatedPreviewUseCase
    ): GetAssetsSwitchedUpdatedPreview = useCase

    @Provides
    @Singleton
    fun provideGetFromAssetUpdatedPreview(
        useCase: GetFromAssetUpdatedPreviewUseCase
    ): GetFromAssetUpdatedPreview = useCase

    @Provides
    @Singleton
    fun provideGetSwapError(@ApplicationContext context: Context): GetSwapError = GetSwapErrorImpl(context)

    @Provides
    @Singleton
    fun provideGetSelectedAssetAmountDetail(
        useCase: GetSelectedAssetAmountDetailUseCase
    ): GetSelectedAssetAmountDetail = useCase

    @Provides
    @Singleton
    fun provideGetAssetSwapSwitchButtonStatus(
        useCase: GetAssetSwapSwitchButtonStatusUseCase
    ): GetAssetSwapSwitchButtonStatus = useCase

    @Provides
    @Singleton
    fun provideGetSwapQuoteUpdatedPreviewPayloadMapper(
        mapper: GetSwapQuoteUpdatedPreviewPayloadMapperImpl
    ): GetSwapQuoteUpdatedPreviewPayloadMapper = mapper

    @Provides
    @Singleton
    fun provideGetFromSelectedAssetAmountDetail(
        useCase: GetFromSelectedAssetAmountDetailUseCase
    ): GetFromSelectedAssetAmountDetail = useCase

    @Provides
    @Singleton
    fun provideGetToSelectedAssetAmountDetail(
        useCase: GetToSelectedAssetAmountDetailUseCase
    ): GetToSelectedAssetAmountDetail = useCase

    @Provides
    @Singleton
    fun provideGetSelectedAssetDetail(
        useCase: GetSelectedAssetDetailUseCase
    ): GetSelectedAssetDetail = useCase

    @Provides
    @Singleton
    fun provideGetFromSelectedAssetDetail(
        useCase: GetFromSelectedAssetDetailUseCase
    ): GetFromSelectedAssetDetail = useCase

    @Provides
    @Singleton
    fun provideGetSwapAmountUpdatedPreview(
        useCase: GetSwapAmountUpdatedPreviewUseCase
    ): GetSwapAmountUpdatedPreview = useCase

    @Provides
    @Singleton
    fun provideGetSwapBalanceError(
        @ApplicationContext context: Context,
        getAccountOwnedAssetData: GetAccountOwnedAssetData,
        getAccountMinBalance: GetAccountMinBalance
    ): GetSwapBalanceError {
        return GetSwapBalanceErrorUseCase(context, getAccountOwnedAssetData, getAccountMinBalance)
    }
}
