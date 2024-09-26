package com.algorand.android.module.parity.di

import com.algorand.android.module.caching.SingleInMemoryLocalCache
import com.algorand.android.module.currency.domain.usecase.IsPrimaryCurrencyAlgo
import com.algorand.android.module.parity.data.mapper.SelectedCurrencyDetailMapper
import com.algorand.android.module.parity.data.mapper.SelectedCurrencyDetailMapperImpl
import com.algorand.android.module.parity.data.repository.ParityRepositoryImpl
import com.algorand.android.module.parity.data.service.ParityApiService
import com.algorand.android.module.parity.domain.model.SelectedCurrencyDetail
import com.algorand.android.module.parity.domain.repository.ParityRepository
import com.algorand.android.module.parity.domain.usecase.CalculateParityValue
import com.algorand.android.module.parity.domain.usecase.ClearSelectedCurrencyDetailCache
import com.algorand.android.module.parity.domain.usecase.FetchAndCacheParity
import com.algorand.android.module.parity.domain.usecase.GetAlgoAmountValue
import com.algorand.android.module.parity.domain.usecase.GetAlgoToUsdConversionRate
import com.algorand.android.module.parity.domain.usecase.GetAssetExchangeParityValue
import com.algorand.android.module.parity.domain.usecase.GetDisplayedCurrencySymbol
import com.algorand.android.module.parity.domain.usecase.GetSelectedCurrencyDetail
import com.algorand.android.module.parity.domain.usecase.GetSelectedCurrencyDetailFlow
import com.algorand.android.module.parity.domain.usecase.GetUsdToAlgoConversionRate
import com.algorand.android.module.parity.domain.usecase.GetUsdValuePerAsset
import com.algorand.android.module.parity.domain.usecase.InitializeParityCache
import com.algorand.android.module.parity.domain.usecase.implementation.CalculateParityValueUseCase
import com.algorand.android.module.parity.domain.usecase.implementation.FetchAndCacheParityUseCase
import com.algorand.android.module.parity.domain.usecase.implementation.GetAlgoAmountValueUseCase
import com.algorand.android.module.parity.domain.usecase.implementation.GetAlgoToUsdConversionRateUseCase
import com.algorand.android.module.parity.domain.usecase.implementation.GetAssetExchangeParityValueUseCase
import com.algorand.android.module.parity.domain.usecase.implementation.GetDisplayedCurrencySymbolUseCase
import com.algorand.android.module.parity.domain.usecase.implementation.GetUsdToAlgoConversionRateUseCase
import com.algorand.android.module.parity.domain.usecase.implementation.GetUsdValuePerAssetUseCase
import com.algorand.android.module.parity.domain.usecase.implementation.InitializeParityCacheUseCase
import com.algorand.android.module.parity.domain.usecase.primary.GetPrimaryAlgoParityValue
import com.algorand.android.module.parity.domain.usecase.primary.GetPrimaryCurrencyAssetParityValue
import com.algorand.android.module.parity.domain.usecase.primary.GetPrimaryCurrencyName
import com.algorand.android.module.parity.domain.usecase.primary.GetPrimaryCurrencySymbol
import com.algorand.android.module.parity.domain.usecase.primary.GetPrimaryCurrencySymbolOrName
import com.algorand.android.module.parity.domain.usecase.primary.GetUsdToPrimaryCurrencyConversionRate
import com.algorand.android.module.parity.domain.usecase.primary.implementation.GetPrimaryAlgoParityValueUseCase
import com.algorand.android.module.parity.domain.usecase.primary.implementation.GetPrimaryCurrencyAssetParityValueUseCase
import com.algorand.android.module.parity.domain.usecase.primary.implementation.GetPrimaryCurrencyNameUseCase
import com.algorand.android.module.parity.domain.usecase.primary.implementation.GetPrimaryCurrencySymbolOrNameUseCase
import com.algorand.android.module.parity.domain.usecase.primary.implementation.GetPrimaryCurrencySymbolUseCase
import com.algorand.android.module.parity.domain.usecase.primary.implementation.GetUsdToPrimaryCurrencyConversionRateUseCase
import com.algorand.android.module.parity.domain.usecase.secondary.GetSecondaryAlgoParityValue
import com.algorand.android.module.parity.domain.usecase.secondary.GetSecondaryCurrencyAssetParityValue
import com.algorand.android.module.parity.domain.usecase.secondary.GetSecondaryCurrencySymbol
import com.algorand.android.module.parity.domain.usecase.secondary.GetUsdToSecondaryCurrencyConversionRate
import com.algorand.android.module.parity.domain.usecase.secondary.implementation.GetSecondaryAlgoParityValueUseCase
import com.algorand.android.module.parity.domain.usecase.secondary.implementation.GetSecondaryCurrencyAssetParityValueUseCase
import com.algorand.android.module.parity.domain.usecase.secondary.implementation.GetSecondaryCurrencySymbolUseCase
import com.algorand.android.module.parity.domain.usecase.secondary.implementation.GetUsdToSecondaryCurrencyConversionRateUseCase
import com.algorand.android.network_utils.exceptions.RetrofitErrorHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
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
