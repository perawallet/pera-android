package com.algorand.android.modules.card.di

import com.algorand.android.modules.perawebview.GetAuthorizedAddressesWebMessage
import com.algorand.android.modules.perawebview.GetAuthorizedAddressesWebMessagesUseCase
import com.algorand.android.modules.perawebview.GetDeviceIdWebMessage
import com.algorand.android.modules.perawebview.GetDeviceIdWebMessageUseCase
import com.algorand.android.modules.perawebview.ParseOpenSystemBrowserUrl
import com.algorand.android.modules.perawebview.ParseOpenSystemBrowserUrlUseCase
import com.algorand.android.modules.perawebview.PeraWebMessageBuilder
import com.algorand.android.modules.perawebview.PeraWebMessageBuilderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CardModule {

    @Provides
    @Singleton
    fun provideGetAuthorizedAddressesWebMessage(
        useCase: GetAuthorizedAddressesWebMessagesUseCase
    ): GetAuthorizedAddressesWebMessage = useCase

    @Provides
    @Singleton
    fun provideGetDeviceIdWebMessage(useCase: GetDeviceIdWebMessageUseCase): GetDeviceIdWebMessage = useCase

    @Provides
    @Singleton
    fun provideParseOpenSystemBrowserUrl(useCase: ParseOpenSystemBrowserUrlUseCase): ParseOpenSystemBrowserUrl = useCase

    @Provides
    @Singleton
    fun providePeraWebMessageBuilder(impl: PeraWebMessageBuilderImpl): PeraWebMessageBuilder = impl
}
