package com.algorand.android.nameservice.di

import com.algorand.android.caching.InMemoryLocalCache
import com.algorand.android.nameservice.data.mapper.*
import com.algorand.android.nameservice.data.repository.NameServiceRepositoryImpl
import com.algorand.android.nameservice.data.service.NameServiceApiService
import com.algorand.android.nameservice.domain.repository.NameServiceRepository
import com.algorand.android.nameservice.domain.usecase.*
import com.hipo.hipoexceptionsandroid.RetrofitErrorHandler
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.*
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
