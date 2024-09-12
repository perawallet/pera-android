package com.algorand.android.currency.di

import android.content.SharedPreferences
import com.algorand.android.currency.data.repository.CurrencyRepositoryImpl
import com.algorand.android.currency.data.service.CurrencyApiService
import com.algorand.android.currency.data.storage.CurrencyLocalSource
import com.algorand.android.currency.domain.repository.CurrencyRepository
import com.algorand.android.currency.domain.usecase.*
import com.algorand.android.currency.domain.usecase.implementation.*
import com.algorand.android.network_utils.exceptions.RetrofitErrorHandler
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.*

@Module
@InstallIn(SingletonComponent::class)
internal object CurrencyModule {

    @Provides
    fun provideGetPrimaryCurrencyId(repository: CurrencyRepository): GetPrimaryCurrencyId {
        return GetPrimaryCurrencyId(repository::getPrimaryCurrencyPreference)
    }

    @Provides
    fun provideSetPrimaryCurrencyId(repository: CurrencyRepository): SetPrimaryCurrencyId {
        return SetPrimaryCurrencyId(repository::setPrimaryCurrencyPreference)
    }

    @Provides
    fun provideSetPrimaryCurrencyChangeListener(repository: CurrencyRepository): SetPrimaryCurrencyChangeListener {
        return SetPrimaryCurrencyChangeListener(repository::setPrimaryCurrencyChangeListener)
    }

    @Provides
    fun provideRemovePrimaryCurrencyChangeListener(
        repository: CurrencyRepository
    ): RemovePrimaryCurrencyChangeListener {
        return RemovePrimaryCurrencyChangeListener(repository::removePrimaryCurrencyChangeListener)
    }

    @Provides
    fun provideGetSelectedCurrency(useCase: GetSelectedCurrencyImpl): GetSelectedCurrency = useCase

    @Provides
    fun provideGetCurrencyOptionList(repository: CurrencyRepository): GetCurrencyOptionList {
        return GetCurrencyOptionList(repository::getCurrencyOptionList)
    }

    @Provides
    @Singleton
    fun provideCurrencyRepository(
        hipoApiErrorHandler: RetrofitErrorHandler,
        currencyApiService: CurrencyApiService,
        sharedPreferences: SharedPreferences
    ): CurrencyRepository {
        return CurrencyRepositoryImpl(
            hipoApiErrorHandler,
            currencyApiService,
            CurrencyLocalSource(sharedPreferences)
        )
    }

    @Provides
    @Singleton
    fun provideCurrencyApiService(
        @Named("mobileAlgorandRetrofitInterface") mobileAlgorandRetrofitInterface: Retrofit
    ): CurrencyApiService {
        return mobileAlgorandRetrofitInterface.create(CurrencyApiService::class.java)
    }

    @Provides
    fun provideIsPrimaryCurrencyAlgo(
        useCase: IsPrimaryCurrencyAlgoUseCase
    ): IsPrimaryCurrencyAlgo = useCase
}
