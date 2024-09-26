package com.algorand.android.module.nameservice.di

import com.algorand.android.module.caching.InMemoryLocalCache
import com.algorand.android.module.nameservice.data.mapper.NameServiceMapper
import com.algorand.android.module.nameservice.data.mapper.NameServiceMapperImpl
import com.algorand.android.module.nameservice.data.mapper.NameServiceSearchResultMapper
import com.algorand.android.module.nameservice.data.mapper.NameServiceSearchResultMapperImpl
import com.algorand.android.module.nameservice.data.mapper.NameServiceSourceMapper
import com.algorand.android.module.nameservice.data.mapper.NameServiceSourceMapperImpl
import com.algorand.android.module.nameservice.data.repository.NameServiceRepositoryImpl
import com.algorand.android.module.nameservice.data.service.NameServiceApiService
import com.algorand.android.module.nameservice.domain.repository.NameServiceRepository
import com.algorand.android.module.nameservice.domain.usecase.GetAccountNameService
import com.algorand.android.module.nameservice.domain.usecase.GetNameServiceSearchResults
import com.algorand.android.module.nameservice.domain.usecase.GetNameServiceSearchResultsUseCase
import com.algorand.android.module.nameservice.domain.usecase.InitializeAccountNameService
import com.algorand.android.module.network.exceptions.RetrofitErrorHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
internal object NameServiceModule {

    @Provides
    fun provideInitializeAccountNameService(
        nameServiceRepository: NameServiceRepository
    ): InitializeAccountNameService {
        return InitializeAccountNameService(nameServiceRepository::initializeNameServiceCache)
    }

    @Provides
    @Singleton
    fun provideNameServiceSearchResultMapper(
        mapper: NameServiceSearchResultMapperImpl
    ): NameServiceSearchResultMapper = mapper

    @Provides
    @Singleton
    fun provideGetNameServiceSearchResults(
        useCase: GetNameServiceSearchResultsUseCase
    ): GetNameServiceSearchResults = useCase

    @Provides
    @Singleton
    fun provideNameServiceRepository(
        nameServiceApiService: NameServiceApiService,
        hipoErrorHandler: RetrofitErrorHandler,
        nameServiceMapper: NameServiceMapper,
        nameServiceSearchResultMapper: NameServiceSearchResultMapper
    ): NameServiceRepository {
        return NameServiceRepositoryImpl(
            nameServiceApiService,
            hipoErrorHandler,
            nameServiceMapper,
            InMemoryLocalCache(),
            nameServiceSearchResultMapper
        )
    }

    @Provides
    fun provideNameServiceApiService(
        @Named("mobileAlgorandRetrofitInterface") retrofit: Retrofit
    ): NameServiceApiService {
        return retrofit.create(NameServiceApiService::class.java)
    }

    @Provides
    fun provideGetNameService(
        nameServiceRepository: NameServiceRepository
    ): GetAccountNameService {
        return GetAccountNameService(nameServiceRepository::getNameService)
    }

    @Provides
    fun provideNameServiceMapper(
        mapper: NameServiceMapperImpl
    ): NameServiceMapper = mapper

    @Provides
    fun provideNameServiceSourceMapper(
        mapper: NameServiceSourceMapperImpl
    ): NameServiceSourceMapper = mapper
}
