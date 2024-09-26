package com.algorand.android.module.currency.di

import android.content.SharedPreferences
import com.algorand.android.module.currency.data.repository.CurrencyRepositoryImpl
import com.algorand.android.module.currency.data.service.CurrencyApiService
import com.algorand.android.module.currency.data.storage.CurrencyLocalSource
import com.algorand.android.module.currency.domain.repository.CurrencyRepository
import com.algorand.android.module.currency.domain.usecase.GetCurrencyOptionList
import com.algorand.android.module.currency.domain.usecase.GetPrimaryCurrencyId
import com.algorand.android.module.currency.domain.usecase.GetSelectedCurrency
import com.algorand.android.module.currency.domain.usecase.IsPrimaryCurrencyAlgo
import com.algorand.android.module.currency.domain.usecase.RemovePrimaryCurrencyChangeListener
import com.algorand.android.module.currency.domain.usecase.SetPrimaryCurrencyChangeListener
import com.algorand.android.module.currency.domain.usecase.SetPrimaryCurrencyId
import com.algorand.android.module.currency.domain.usecase.implementation.GetSelectedCurrencyImpl
import com.algorand.android.module.currency.domain.usecase.implementation.IsPrimaryCurrencyAlgoUseCase
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
