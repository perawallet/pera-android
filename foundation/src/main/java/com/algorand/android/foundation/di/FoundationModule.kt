package com.algorand.android.foundation.di

import android.content.Context
import com.algorand.android.foundation.common.PeraSystemServiceManager
import com.algorand.android.foundation.common.PeraSystemServiceManagerImpl
import com.algorand.android.foundation.json.JsonSerializer
import com.algorand.android.foundation.json.PeraSerializer
import com.algorand.android.foundation.locale.LocaleProvider
import com.algorand.android.foundation.locale.LocaleProviderImpl
import com.algorand.android.foundation.permission.PeraPermissionManager
import com.algorand.android.foundation.permission.PeraPermissionManagerImpl
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object FoundationModule {

    @Provides
    @Singleton
    fun provideJsonSerializer(gson: Gson): JsonSerializer = PeraSerializer(gson)

    @Provides
    @Singleton
    fun provideLocaleProvider(localeProviderImpl: LocaleProviderImpl): LocaleProvider = localeProviderImpl

    @Provides
    @Singleton
    fun providePeraPermissionManager(@ApplicationContext context: Context): PeraPermissionManager {
        return PeraPermissionManagerImpl(context)
    }

    @Provides
    @Singleton
    fun providePeraSystemServiceManager(@ApplicationContext context: Context): PeraSystemServiceManager {
        return PeraSystemServiceManagerImpl(context)
    }
}
