/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

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
