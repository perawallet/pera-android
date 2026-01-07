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

package com.algorand.android.modules.walletconnect.di

import com.algorand.android.modules.walletconnect.domain.usecase.CreateWalletConnectAccount
import com.algorand.android.modules.walletconnect.domain.usecase.CreateWalletConnectAccountUseCase
import com.algorand.android.modules.walletconnect.domain.usecase.CreateWalletConnectArbitraryDataSigner
import com.algorand.android.modules.walletconnect.domain.usecase.CreateWalletConnectArbitraryDataSignerUseCase
import com.algorand.android.modules.walletconnect.domain.usecase.GetWalletConnectTransactionSigner
import com.algorand.android.modules.walletconnect.domain.usecase.GetWalletConnectTransactionSignerUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

@Module
@InstallIn(SingletonComponent::class)
internal object WalletConnectDiModule {

    @Provides
    fun provideGetWalletConnectTransactionSigner(
        useCase: GetWalletConnectTransactionSignerUseCase
    ): GetWalletConnectTransactionSigner = useCase

    @Provides
    fun provideCreateWalletConnectAccount(
        useCase: CreateWalletConnectAccountUseCase
    ): CreateWalletConnectAccount = useCase

    @Provides
    @Singleton
    @Suppress("MagicNumber")
    @Named("walletConnectHttpClient")
    fun provideWalletConnectHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(0, TimeUnit.SECONDS)
            .pingInterval(30, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    fun provideCreateWalletConnectArbitraryDataSigner(
        useCase: CreateWalletConnectArbitraryDataSignerUseCase
    ): CreateWalletConnectArbitraryDataSigner = useCase
}
