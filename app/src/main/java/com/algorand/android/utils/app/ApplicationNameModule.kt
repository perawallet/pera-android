package com.algorand.android.utils.app

import com.algorand.android.foundation.app.GetAppFlavor
import com.algorand.android.foundation.app.ProvideApplicationName
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object ApplicationNameModule {

    @Provides
    @Singleton
    fun provideApplicationNameProvide(impl: ProvideApplicationNameImpl): ProvideApplicationName = impl

    @Provides
    @Singleton
    fun provideGetAppFlavor(impl: GetAppFlavorImpl): GetAppFlavor = impl
}
