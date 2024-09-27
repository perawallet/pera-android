package com.algorand.android.module.deviceid.di

import android.content.SharedPreferences
import com.algorand.android.module.foundation.app.ProvideApplicationName
import com.algorand.android.module.foundation.locale.LocaleProvider
import com.algorand.android.module.deviceid.data.repository.DeviceIdRepositoryImpl
import com.algorand.android.module.deviceid.data.service.DeviceIdApiService
import com.algorand.android.module.deviceid.data.storage.MainnetDeviceIdLocalSource
import com.algorand.android.module.deviceid.data.storage.NotificationUserIdLocalSource
import com.algorand.android.module.deviceid.data.storage.TestnetDeviceIdLocalSource
import com.algorand.android.module.deviceid.domain.repository.DeviceIdRepository
import com.algorand.android.module.deviceid.domain.usecase.GetMainnetDeviceId
import com.algorand.android.module.deviceid.domain.usecase.GetNodeDeviceId
import com.algorand.android.module.deviceid.domain.usecase.GetNotificationUserId
import com.algorand.android.module.deviceid.domain.usecase.GetSelectedNodeDeviceId
import com.algorand.android.module.deviceid.domain.usecase.GetTestnetDeviceId
import com.algorand.android.module.deviceid.domain.usecase.RegisterDeviceId
import com.algorand.android.module.deviceid.domain.usecase.SetMainnetDeviceId
import com.algorand.android.module.deviceid.domain.usecase.SetNotificationUserId
import com.algorand.android.module.deviceid.domain.usecase.SetSelectedNodeDeviceId
import com.algorand.android.module.deviceid.domain.usecase.SetTestnetDeviceId
import com.algorand.android.module.deviceid.domain.usecase.UnregisterDeviceId
import com.algorand.android.module.deviceid.domain.usecase.UpdateDeviceId
import com.algorand.android.module.deviceid.domain.usecase.implementation.GetNodeDeviceIdUseCase
import com.algorand.android.module.deviceid.domain.usecase.implementation.GetSelectedNodeDeviceIdUseCase
import com.algorand.android.module.deviceid.domain.usecase.implementation.RegisterDeviceIdUseCase
import com.algorand.android.module.deviceid.domain.usecase.implementation.SetSelectedNodeDeviceIdUseCase
import com.algorand.android.module.deviceid.domain.usecase.implementation.UnregisterDeviceIdUseCase
import com.algorand.android.module.deviceid.utils.ProvideDeviceIdPlatform
import com.algorand.android.module.deviceid.utils.ProvideDeviceIdPlatformImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
internal object DeviceIdModule {

    @Provides
    @Singleton
    fun provideDeviceIdRepository(
        localeProvider: LocaleProvider,
        deviceIdApiService: DeviceIdApiService,
        sharedPreferences: SharedPreferences,
        provideApplicationName: ProvideApplicationName,
        provideDeviceIdPlatform: ProvideDeviceIdPlatform
    ): DeviceIdRepository {
        return DeviceIdRepositoryImpl(
            mainnetDeviceIdLocalSource = MainnetDeviceIdLocalSource(sharedPreferences),
            testnetDeviceIdLocalSource = TestnetDeviceIdLocalSource(sharedPreferences),
            notificationUserIdLocalSource = NotificationUserIdLocalSource(sharedPreferences),
            localeProvider,
            deviceIdApiService,
            provideApplicationName,
            provideDeviceIdPlatform
        )
    }

    @Provides
    @Singleton
    fun provideDeviceIdApiService(
        @Named("mobileAlgorandRetrofitInterface") retrofit: Retrofit
    ): DeviceIdApiService {
        return retrofit.create(DeviceIdApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideGetMainnetDeviceId(
        deviceIdRepository: DeviceIdRepository
    ): GetMainnetDeviceId {
        return GetMainnetDeviceId(deviceIdRepository::getMainnetDeviceId)
    }

    @Provides
    @Singleton
    fun provideGetNotificationUserId(
        deviceIdRepository: DeviceIdRepository
    ): GetNotificationUserId {
        return GetNotificationUserId(deviceIdRepository::getNotificationUserId)
    }

    @Provides
    @Singleton
    fun provideGetTestnetDeviceId(
        deviceIdRepository: DeviceIdRepository
    ): GetTestnetDeviceId {
        return GetTestnetDeviceId(deviceIdRepository::getTestnetDeviceId)
    }

    @Provides
    @Singleton
    fun provideSetMainnetDeviceId(
        deviceIdRepository: DeviceIdRepository
    ): SetMainnetDeviceId {
        return SetMainnetDeviceId(deviceIdRepository::setMainnetDeviceId)
    }

    @Provides
    @Singleton
    fun provideSetNotificationUserId(
        deviceIdRepository: DeviceIdRepository
    ): SetNotificationUserId {
        return SetNotificationUserId(deviceIdRepository::setNotificationUserId)
    }

    @Provides
    @Singleton
    fun provideSetTestnetDeviceId(
        deviceIdRepository: DeviceIdRepository
    ): SetTestnetDeviceId {
        return SetTestnetDeviceId(deviceIdRepository::setTestnetDeviceId)
    }

    @Provides
    @Singleton
    fun provideUpdateDeviceId(
        deviceIdRepository: DeviceIdRepository
    ): UpdateDeviceId {
        return UpdateDeviceId(deviceIdRepository::updateDeviceId)
    }

    @Provides
    @Singleton
    fun provideRegisterDeviceId(useCase: RegisterDeviceIdUseCase): RegisterDeviceId = useCase

    @Provides
    @Singleton
    fun provideProvideDeviceIdPlatform(
        impl: ProvideDeviceIdPlatformImpl
    ): ProvideDeviceIdPlatform = impl

    @Provides
    @Singleton
    fun provideGetNodeDeviceId(useCase: GetNodeDeviceIdUseCase): GetNodeDeviceId = useCase

    @Provides
    @Singleton
    fun provideGetSelectedNodeDeviceId(useCase: GetSelectedNodeDeviceIdUseCase): GetSelectedNodeDeviceId = useCase

    @Provides
    @Singleton
    fun provideSetSelectedNodeDeviceId(useCase: SetSelectedNodeDeviceIdUseCase): SetSelectedNodeDeviceId = useCase

    @Provides
    @Singleton
    fun provideUnregisterDeviceId(useCase: UnregisterDeviceIdUseCase): UnregisterDeviceId = useCase
}
