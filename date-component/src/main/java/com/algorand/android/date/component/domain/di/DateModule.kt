package com.algorand.android.date.component.domain.di

import com.algorand.android.date.component.domain.usecase.GetCurrentZonedDateTime
import com.algorand.android.date.component.domain.usecase.implementation.GetCurrentZonedDateTimeUseCase
import dagger.*
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
