package com.algorand.android.module.date.domain.di

import com.algorand.android.module.date.domain.usecase.GetCurrentZonedDateTime
import com.algorand.android.module.date.domain.usecase.implementation.GetCurrentZonedDateTimeUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DateModule {

    @Provides
    @Singleton
    fun provideGetCurrentZonedDateTime(useCase: GetCurrentZonedDateTimeUseCase): GetCurrentZonedDateTime = useCase
}
