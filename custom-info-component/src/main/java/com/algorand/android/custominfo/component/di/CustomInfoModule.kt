package com.algorand.android.custominfo.component.di

import com.algorand.android.custominfo.component.domain.repository.CustomInfoRepository
import com.algorand.android.custominfo.component.domain.usecase.*
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object CustomInfoModule {

    @Provides
    @Singleton
    fun provideGetCustomInfoOrNull(
        repository: CustomInfoRepository
    ): GetCustomInfoOrNull = GetCustomInfoOrNull(repository::getCustomInfoOrNull)

    @Provides
    @Singleton
    fun provideSetCustomInfo(
        repository: CustomInfoRepository
    ): SetCustomInfo = SetCustomInfo(repository::setCustomInfo)

    @Provides
    @Singleton
    fun provideSetCustomName(
        repository: CustomInfoRepository
    ): SetCustomName = SetCustomName(repository::setCustomName)

    @Provides
    @Singleton
    fun provideGetCustomInfo(
        repository: CustomInfoRepository
    ): GetCustomInfo = GetCustomInfo(repository::getCustomInfo)

    @Provides
    @Singleton
    fun provideDeleteCustomInfo(
        repository: CustomInfoRepository
    ): DeleteCustomInfo = DeleteCustomInfo(repository::deleteCustomInfo)
}
