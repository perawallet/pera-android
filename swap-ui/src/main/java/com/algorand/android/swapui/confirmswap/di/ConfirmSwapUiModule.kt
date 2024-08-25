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

package com.algorand.android.swapui.confirmswap.di

import com.algorand.android.swapui.confirmswap.SignSwapTransactionManager
import com.algorand.android.swapui.confirmswap.SignSwapTransactionManagerImpl
import com.algorand.android.swapui.confirmswap.mapper.ConfirmSwapPriceImpactWarningStatusMapper
import com.algorand.android.swapui.confirmswap.mapper.ConfirmSwapPriceImpactWarningStatusMapperImpl
import com.algorand.android.swapui.confirmswap.mapper.SignSwapTransactionResultMapper
import com.algorand.android.swapui.confirmswap.mapper.SignSwapTransactionResultMapperImpl
import com.algorand.android.swapui.confirmswap.mapper.SwapAssetDetailMapper
import com.algorand.android.swapui.confirmswap.mapper.SwapAssetDetailMapperImpl
import com.algorand.android.swapui.confirmswap.mapper.SwapPriceRatioProviderMapper
import com.algorand.android.swapui.confirmswap.mapper.SwapPriceRatioProviderMapperImpl
import com.algorand.android.swapui.confirmswap.usecase.GetConfirmSwapPreview
import com.algorand.android.swapui.confirmswap.usecase.GetConfirmSwapPreviewUseCase
import com.algorand.android.swapui.confirmswap.usecase.GetSwapTransactionStatusNavArgs
import com.algorand.android.swapui.confirmswap.usecase.GetSwapTransactionStatusNavArgsUseCase
import com.algorand.android.swapui.confirmswap.usecase.UpdateSlippageTolerance
import com.algorand.android.swapui.confirmswap.usecase.UpdateSlippageToleranceUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object ConfirmSwapUiModule {

    @Provides
    @Singleton
    fun provideConfirmSwapPriceImpactWarningStatusMapper(
        impl: ConfirmSwapPriceImpactWarningStatusMapperImpl
    ): ConfirmSwapPriceImpactWarningStatusMapper = impl

    @Provides
    @Singleton
    fun provideSwapAssetDetailMapper(impl: SwapAssetDetailMapperImpl): SwapAssetDetailMapper = impl

    @Provides
    @Singleton
    fun provideSwapPriceRatioProviderMapper(
        mapper: SwapPriceRatioProviderMapperImpl
    ): SwapPriceRatioProviderMapper = mapper

    @Provides
    @Singleton
    fun provideGetConfirmSwapPreview(useCase: GetConfirmSwapPreviewUseCase): GetConfirmSwapPreview = useCase

    @Provides
    @Singleton
    fun provideUpdateSlippageTolerance(useCase: UpdateSlippageToleranceUseCase): UpdateSlippageTolerance = useCase

    @Provides
    @Singleton
    fun provideGetSwapTransactionStatusNavArgs(
        useCase: GetSwapTransactionStatusNavArgsUseCase
    ): GetSwapTransactionStatusNavArgs = useCase

    @Provides
    @Singleton
    fun provideSignSwapTransactionResultMapper(
        impl: SignSwapTransactionResultMapperImpl
    ): SignSwapTransactionResultMapper = impl

    @Provides
    fun provideSignSwapTransactionManager(impl: SignSwapTransactionManagerImpl): SignSwapTransactionManager = impl
}
