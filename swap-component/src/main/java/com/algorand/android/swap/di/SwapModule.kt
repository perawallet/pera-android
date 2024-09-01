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

package com.algorand.android.swap.di

import com.algorand.android.swap.data.mapper.AvailableSwapAssetsMapper
import com.algorand.android.swap.data.mapper.AvailableSwapAssetsMapperImpl
import com.algorand.android.swap.data.mapper.DisplayedCurrencyParityValueMapper
import com.algorand.android.swap.data.mapper.DisplayedCurrencyParityValueMapperImpl
import com.algorand.android.swap.data.mapper.PeraSwapFeeMapper
import com.algorand.android.swap.data.mapper.PeraSwapFeeMapperImpl
import com.algorand.android.swap.data.mapper.SwapQuoteAssetDetailMapper
import com.algorand.android.swap.data.mapper.SwapQuoteAssetDetailMapperImpl
import com.algorand.android.swap.data.mapper.SwapQuoteMapper
import com.algorand.android.swap.data.mapper.SwapQuoteMapperImpl
import com.algorand.android.swap.data.mapper.SwapQuoteProviderMapper
import com.algorand.android.swap.data.mapper.SwapQuoteProviderMapperImpl
import com.algorand.android.swap.data.mapper.SwapQuoteRequestBodyMapper
import com.algorand.android.swap.data.mapper.SwapQuoteRequestBodyMapperImpl
import com.algorand.android.swap.data.mapper.SwapQuoteTransactionMapper
import com.algorand.android.swap.data.mapper.SwapQuoteTransactionMapperImpl
import com.algorand.android.swap.data.mapper.SwapTypeMapper
import com.algorand.android.swap.data.mapper.SwapTypeMapperImpl
import com.algorand.android.swap.data.repository.AssetSwapRepositoryImpl
import com.algorand.android.swap.data.service.AssetSwapApi
import com.algorand.android.swap.domain.factory.SwapTransactionItemFactory
import com.algorand.android.swap.domain.factory.SwapTransactionItemFactoryImpl
import com.algorand.android.swap.domain.mapper.GetSwapQuoteRequestPayloadMapper
import com.algorand.android.swap.domain.mapper.GetSwapQuoteRequestPayloadMapperImpl
import com.algorand.android.swap.domain.repository.AssetSwapRepository
import com.algorand.android.swap.domain.tracking.SwapTrackingHelper
import com.algorand.android.swap.domain.tracking.SwapTrackingHelperImpl
import com.algorand.android.swap.domain.tracking.mapper.SwapFailedEventPayloadMapper
import com.algorand.android.swap.domain.tracking.mapper.SwapFailedEventPayloadMapperImpl
import com.algorand.android.swap.domain.tracking.mapper.SwapSuccessEventPayloadMapper
import com.algorand.android.swap.domain.tracking.mapper.SwapSuccessEventPayloadMapperImpl
import com.algorand.android.swap.domain.tracking.usecase.LogSwapTransactionFailureEvent
import com.algorand.android.swap.domain.tracking.usecase.LogSwapTransactionFailureEventUseCase
import com.algorand.android.swap.domain.tracking.usecase.LogSwapTransactionSuccessEvent
import com.algorand.android.swap.domain.tracking.usecase.LogSwapTransactionSuccessEventUseCase
import com.algorand.android.swap.domain.usecase.CreateSwapQuoteTransactions
import com.algorand.android.swap.domain.usecase.CreateSwapQuoteTransactionsUseCase
import com.algorand.android.swap.domain.usecase.GetAvailableTargetSwapAssets
import com.algorand.android.swap.domain.usecase.GetBalancePercentageForAlgo
import com.algorand.android.swap.domain.usecase.GetBalancePercentageForAlgoUseCase
import com.algorand.android.swap.domain.usecase.GetBalancePercentageForAsset
import com.algorand.android.swap.domain.usecase.GetBalancePercentageForAssetUseCase
import com.algorand.android.swap.domain.usecase.GetPercentageCalculatedBalanceForSwap
import com.algorand.android.swap.domain.usecase.GetPercentageCalculatedBalanceForSwapUseCase
import com.algorand.android.swap.domain.usecase.GetSwapAlgorandTransactionFee
import com.algorand.android.swap.domain.usecase.GetSwapAlgorandTransactionFeeUseCase
import com.algorand.android.swap.domain.usecase.GetSwapOptInTransactionFee
import com.algorand.android.swap.domain.usecase.GetSwapOptInTransactionFeeUseCase
import com.algorand.android.swap.domain.usecase.GetSwapPeraFee
import com.algorand.android.swap.domain.usecase.GetSwapPeraFeeUseCase
import com.algorand.android.swap.domain.usecase.GetSwapQuote
import com.algorand.android.swap.domain.usecase.GetSwapQuoteUseCase
import com.algorand.android.swap.domain.usecase.RecordSwapQuoteException
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
internal object SwapModule {

    @Provides
    @Singleton
    fun providePeraSwapFeeMapper(mapper: PeraSwapFeeMapperImpl): PeraSwapFeeMapper = mapper

    @Provides
    @Singleton
    fun provideGetBalancePercentageForAlgo(
        useCase: GetBalancePercentageForAlgoUseCase
    ): GetBalancePercentageForAlgo = useCase

    @Provides
    @Singleton
    fun provideGetSwapPeraFee(useCase: GetSwapPeraFeeUseCase): GetSwapPeraFee = useCase

    @Provides
    @Singleton
    fun provideGetPercentageCalculatedBalanceForSwap(
        useCase: GetPercentageCalculatedBalanceForSwapUseCase
    ): GetPercentageCalculatedBalanceForSwap = useCase

    @Provides
    @Singleton
    fun provideGetBalancePercentageForAsset(
        useCase: GetBalancePercentageForAssetUseCase
    ): GetBalancePercentageForAsset = useCase

    @Provides
    @Singleton
    fun provideAssetSwapRepository(repository: AssetSwapRepositoryImpl): AssetSwapRepository = repository

    @Provides
    @Singleton
    fun provideAssetSwapApi(
        @Named("mobileAlgorandRetrofitInterface") retrofit: Retrofit
    ): AssetSwapApi {
        return retrofit.create(AssetSwapApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSwapQuoteTransactionMapper(mapper: SwapQuoteTransactionMapperImpl): SwapQuoteTransactionMapper = mapper

    @Provides
    @Singleton
    fun provideGetSwapQuote(useCase: GetSwapQuoteUseCase): GetSwapQuote = useCase

    @Provides
    @Singleton
    fun provideGetSwapQuoteRequestPayloadMapper(
        impl: GetSwapQuoteRequestPayloadMapperImpl
    ): GetSwapQuoteRequestPayloadMapper = impl

    @Provides
    @Singleton
    fun provideSwapTypeMapper(impl: SwapTypeMapperImpl): SwapTypeMapper = impl

    @Provides
    @Singleton
    fun provideSwapQuoteProviderMapper(impl: SwapQuoteProviderMapperImpl): SwapQuoteProviderMapper = impl

    @Provides
    @Singleton
    fun provideGetDisplayedCurrencyParityValueMapper(
        impl: DisplayedCurrencyParityValueMapperImpl
    ): DisplayedCurrencyParityValueMapper = impl

    @Provides
    @Singleton
    fun provideSwapQuoteMapper(impl: SwapQuoteMapperImpl): SwapQuoteMapper = impl

    @Provides
    @Singleton
    fun provideSwapQuoteRequestBodyMapper(impl: SwapQuoteRequestBodyMapperImpl): SwapQuoteRequestBodyMapper = impl

    @Provides
    @Singleton
    fun provideSwapQuoteAssetDetailMapper(impl: SwapQuoteAssetDetailMapperImpl): SwapQuoteAssetDetailMapper = impl

    @Provides
    @Singleton
    fun provideGetAvailableTargetSwapAssets(
        repository: AssetSwapRepository
    ): GetAvailableTargetSwapAssets = GetAvailableTargetSwapAssets(repository::getAvailableTargetSwapAssets)

    @Provides
    @Singleton
    fun provideAvailableSwapAssetsMapper(
        impl: AvailableSwapAssetsMapperImpl
    ): AvailableSwapAssetsMapper = impl

    @Provides
    @Singleton
    fun provideRecordSwapQuoteException(
        repository: AssetSwapRepository
    ): RecordSwapQuoteException = RecordSwapQuoteException(repository::recordSwapQuoteException)

    @Provides
    @Singleton
    fun provideCreateQuoteTransactions(
        useCase: CreateSwapQuoteTransactionsUseCase
    ): CreateSwapQuoteTransactions = useCase

    @Provides
    @Singleton
    fun provideSwapTransactionItemFactory(
        impl: SwapTransactionItemFactoryImpl
    ): SwapTransactionItemFactory = impl

    @Provides
    @Singleton
    fun provideGetSwapAlgorandTransactionFee(
        useCase: GetSwapAlgorandTransactionFeeUseCase
    ): GetSwapAlgorandTransactionFee = useCase

    @Provides
    @Singleton
    fun provideGetSwapOptInTransactionFee(
        useCase: GetSwapOptInTransactionFeeUseCase
    ): GetSwapOptInTransactionFee = useCase

    @Provides
    @Singleton
    fun provideSwapSuccessEventPayloadMapper(
        impl: SwapSuccessEventPayloadMapperImpl
    ): SwapSuccessEventPayloadMapper = impl

    @Provides
    @Singleton
    fun provideLogSwapTransactionSuccessEvent(
        useCase: LogSwapTransactionSuccessEventUseCase
    ): LogSwapTransactionSuccessEvent = useCase

    @Provides
    @Singleton
    fun provideSwapFailedEventPayloadMapper(
        impl: SwapFailedEventPayloadMapperImpl
    ): SwapFailedEventPayloadMapper = impl

    @Provides
    @Singleton
    fun provideLogSwapTransactionFailureEvent(
        useCase: LogSwapTransactionFailureEventUseCase
    ): LogSwapTransactionFailureEvent = useCase

    @Provides
    @Singleton
    fun provideSwapTrackingHelper(impl: SwapTrackingHelperImpl): SwapTrackingHelper = impl
}