package com.algorand.android.deviceregistration.di

import com.algorand.android.deviceregistration.domain.usecase.*
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class DeviceIdModule {

    @Provides
    @Singleton
    fun provideMigrateDeviceIdIfNeed(useCase: MigrateDeviceIdIfNeedUseCase): MigrateDeviceIdIfNeed = useCase
}
