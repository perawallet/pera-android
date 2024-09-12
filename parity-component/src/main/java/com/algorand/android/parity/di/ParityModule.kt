package com.algorand.android.parity.di

import com.algorand.android.caching.SingleInMemoryLocalCache
import com.algorand.android.currency.domain.usecase.IsPrimaryCurrencyAlgo
import com.algorand.android.network_utils.exceptions.RetrofitErrorHandler
import com.algorand.android.parity.data.mapper.*
import com.algorand.android.parity.data.repository.ParityRepositoryImpl
import com.algorand.android.parity.data.service.ParityApiService
import com.algorand.android.parity.domain.model.*
import com.algorand.android.parity.domain.repository.ParityRepository
import com.algorand.android.parity.domain.usecase.*
import com.algorand.android.parity.domain.usecase.implementation.*
import com.algorand.android.parity.domain.usecase.primary.*
import com.algorand.android.parity.domain.usecase.primary.implementation.*
import com.algorand.android.parity.domain.usecase.secondary.*
import com.algorand.android.parity.domain.usecase.secondary.implementation.*
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.*
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
internal object ParityModule {

    @Provides
    fun provideSelectedCurrencyDetailMapper(
        mapper: SelectedCurrencyDetailMapperImpl
    ): SelectedCurrencyDetailMapper = mapper

    @Provides
    @Singleton
    fun provideParityApiService(
        @Named("mobileAlgorandRetrofitInterface") mobileAlgorandRetrofitInterface: Retrofit
    ): ParityApiService {
        return mobileAlgorandRetrofitInterface.create(ParityApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideParityRepository(
        parityApiService: ParityApiService,
        hipoApiErrorHandler: RetrofitErrorHandler,
        isPrimaryCurrencyAlgo: IsPrimaryCurrencyAlgo,
        selectedCurrencyDetailMapper: SelectedCurrencyDetailMapper
    ): ParityRepository {
        return ParityRepositoryImpl(
            parityApiService,
            hipoApiErrorHandler,
            SingleInMemoryLocalCache<SelectedCurrencyDetail>(),
            isPrimaryCurrencyAlgo,
            selectedCurrencyDetailMapper
        )
    }

    @Provides
    fun provideGetPrimaryCurrencyAssetParityValue(
        useCase: GetPrimaryCurrencyAssetParityValueUseCase
    ): GetPrimaryCurrencyAssetParityValue = useCase

    @Provides
    fun provideGetPrimaryCurrencySymbolOrEmpty(
        useCase: GetPrimaryCurrencySymbolUseCase
    ): GetPrimaryCurrencySymbol = useCase

    @Provides
    fun provideGetUsdToPrimaryCurrencyConversionRate(
        useCase: GetUsdToPrimaryCurrencyConversionRateUseCase
    ): GetUsdToPrimaryCurrencyConversionRate = useCase

    @Provides
    fun provideGetSecondaryCurrencyAssetParityValue(
        useCase: GetSecondaryCurrencyAssetParityValueUseCase
    ): GetSecondaryCurrencyAssetParityValue = useCase

    @Provides
    fun provideGetSecondaryCurrencySymbol(
        useCase: GetSecondaryCurrencySymbolUseCase
    ): GetSecondaryCurrencySymbol = useCase

    @Provides
    fun provideGetUsdToSecondaryCurrencyConversionRate(
        useCase: GetUsdToSecondaryCurrencyConversionRateUseCase
    ): GetUsdToSecondaryCurrencyConversionRate = useCase

    @Provides
    fun provideCalculateParityValue(
        useCase: CalculateParityValueUseCase
    ): CalculateParityValue = useCase

    @Provides
    fun provideGetUsdToAlgoConversionRate(
        useCase: GetUsdToAlgoConversionRateUseCase
    ): GetUsdToAlgoConversionRate = useCase

    @Provides
    fun provideFetchAndCacheParity(
        useCase: FetchAndCacheParityUseCase
    ): FetchAndCacheParity = useCase

    @Provides
    fun provideInitializeParityCache(
        useCase: InitializeParityCacheUseCase
    ): InitializeParityCache = useCase

    @Provides
    fun provideGetAlgoAmountValue(
        useCase: GetAlgoAmountValueUseCase
    ): GetAlgoAmountValue = useCase

    @Provides
    fun provideGetAlgoToUsdConversionRate(
        useCase: GetAlgoToUsdConversionRateUseCase
    ): GetAlgoToUsdConversionRate = useCase

    @Provides
    fun provideGetPrimaryAlgoParityValue(
        useCase: GetPrimaryAlgoParityValueUseCase
    ): GetPrimaryAlgoParityValue = useCase

    @Provides
    fun provideGetSecondaryAlgoParityValue(
        useCase: GetSecondaryAlgoParityValueUseCase
    ): GetSecondaryAlgoParityValue = useCase

    @Provides
    @Singleton
    fun provideGetSelectedCurrencyDetailFlow(
        parityRepository: ParityRepository
    ): GetSelectedCurrencyDetailFlow = GetSelectedCurrencyDetailFlow(parityRepository::getSelectedCurrencyDetailFlow)

    @Provides
    @Singleton
    fun provideGetSelectedCurrencyDetail(
        parityRepository: ParityRepository
    ): GetSelectedCurrencyDetail = GetSelectedCurrencyDetail(parityRepository::getSelectedCurrencyDetail)

    @Provides
    @Singleton
    fun provideClearSelectedCurrencyDetailCache(parityRepository: ParityRepository): ClearSelectedCurrencyDetailCache {
        return ClearSelectedCurrencyDetailCache(parityRepository::clearSelectedCurrencyDetailCache)
    }

    @Provides
    @Singleton
    fun provideGetPrimaryCurrencyName(
        useCase: GetPrimaryCurrencyNameUseCase
    ): GetPrimaryCurrencyName = useCase

    @Provides
    @Singleton
    fun provideGetPrimaryCurrencySymbolOrName(
        useCase: GetPrimaryCurrencySymbolOrNameUseCase
    ): GetPrimaryCurrencySymbolOrName = useCase

    @Provides
    @Singleton
    fun provideGetDisplayedCurrencySymbol(
        useCase: GetDisplayedCurrencySymbolUseCase
    ): GetDisplayedCurrencySymbol = useCase

    @Provides
    @Singleton
    fun provideGetUsdValuePerAsset(useCase: GetUsdValuePerAssetUseCase): GetUsdValuePerAsset = useCase

    @Provides
    @Singleton
    fun provideGetAssetExchangeParityValue(
        useCase: GetAssetExchangeParityValueUseCase
    ): GetAssetExchangeParityValue = useCase
}
