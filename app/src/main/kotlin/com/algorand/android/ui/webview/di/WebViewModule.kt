/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.ui.webview.di

import com.algorand.android.modules.peraserializer.PeraSerializer
import com.algorand.android.ui.webview.bridge.mapper.DefaultPeraWebInterfaceEventMapper
import com.algorand.android.ui.webview.bridge.mapper.DefaultPeraWebInterfaceEventResponseMapper
import com.algorand.android.ui.webview.bridge.mapper.DefaultPeraWebInterfaceNotifyUserEventMapper
import com.algorand.android.ui.webview.bridge.mapper.DefaultSettingsWebResponseMapper
import com.algorand.android.ui.webview.bridge.mapper.PeraWebInterfaceEventMapper
import com.algorand.android.ui.webview.bridge.mapper.PeraWebInterfaceEventResponseMapper
import com.algorand.android.ui.webview.bridge.mapper.PeraWebInterfaceNotifyUserEventMapper
import com.algorand.android.ui.webview.bridge.mapper.SettingsWebResponseMapper
import com.algorand.android.ui.webview.bridge.serializer.WebViewJsonSerializer
import com.algorand.android.ui.webview.bridge.usecase.GetGetAddressesWebResponse
import com.algorand.android.ui.webview.bridge.usecase.GetGetAddressesWebResponseUseCase
import com.algorand.wallet.logger.PeraErrorLogger
import com.google.gson.FormattingStyle
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object WebViewModule {

    @Provides
    fun providePeraWebInterfaceEventResponseMapper(
        jsonSerializer: WebViewJsonSerializer,
    ): PeraWebInterfaceEventResponseMapper {
        return DefaultPeraWebInterfaceEventResponseMapper(jsonSerializer)
    }

    @Provides
    fun provideSettingsWebResponseMapper(mapper: DefaultSettingsWebResponseMapper): SettingsWebResponseMapper = mapper

    @Provides
    fun provideGetGetAddressesWebResponse(
        useCase: GetGetAddressesWebResponseUseCase
    ): GetGetAddressesWebResponse = useCase

    @Provides
    fun providePeraWebInterfaceNotifyUserEventMapper(
        mapper: DefaultPeraWebInterfaceNotifyUserEventMapper
    ): PeraWebInterfaceNotifyUserEventMapper = mapper

    @Provides
    fun providePeraWebInterfaceEventMapper(
        notifyUserEventMapper: PeraWebInterfaceNotifyUserEventMapper,
        errorLogger: PeraErrorLogger,
        webViewJsonSerializer: WebViewJsonSerializer
    ): PeraWebInterfaceEventMapper {
        return DefaultPeraWebInterfaceEventMapper(webViewJsonSerializer, notifyUserEventMapper, errorLogger)
    }

    @Singleton
    @Provides
    fun provideWebViewJsonSerializer(): WebViewJsonSerializer {
        val gson = GsonBuilder()
            .setFormattingStyle(FormattingStyle.COMPACT)
            .create()
        return WebViewJsonSerializer(PeraSerializer(gson))
    }
}
