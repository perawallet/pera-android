/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.module.deeplink.di

import com.algorand.android.module.deeplink.DeepLinkHandler
import com.algorand.android.module.deeplink.DeepLinkHandlerImpl
import com.algorand.android.module.deeplink.factory.DeepLinkFactory
import com.algorand.android.module.deeplink.factory.DeepLinkFactoryImpl
import com.algorand.android.module.deeplink.mapper.WebImportQrCodeMapper
import com.algorand.android.module.deeplink.mapper.WebImportQrCodeMapperImpl
import com.algorand.android.module.deeplink.usecase.CreateNotificationDeepLink
import com.algorand.android.module.deeplink.usecase.CreateNotificationDeepLinkUseCase
import com.algorand.android.module.deeplink.usecase.ParseDeepLink
import com.algorand.android.module.deeplink.usecase.ParseDeepLinkUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DeepLinkModule {

    @Provides
    @Singleton
    fun provideWebImportQrCodeMapper(impl: WebImportQrCodeMapperImpl): WebImportQrCodeMapper = impl

    @Provides
    @Singleton
    fun provideParseDeepLink(useCase: ParseDeepLinkUseCase): ParseDeepLink = useCase

    @Provides
    @Singleton
    fun provideDeepLinkFactory(impl: DeepLinkFactoryImpl): DeepLinkFactory = impl

    @Provides
    @Singleton
    fun provideDeepLinkHandler(impl: DeepLinkHandlerImpl): DeepLinkHandler = impl

    @Provides
    @Singleton
    fun provideCreateNotificationDeepLink(
        useCase: CreateNotificationDeepLinkUseCase
    ): CreateNotificationDeepLink = useCase
}