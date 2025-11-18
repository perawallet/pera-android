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

package com.algorand.android.credentials.passkeys.validator.di

import com.algorand.android.credentials.passkeys.validator.CallingAppInfoValidator
import com.algorand.android.credentials.passkeys.validator.PasskeyCallingAppInfoValidator
import com.algorand.android.credentials.passkeys.validator.data.network.AssetLinksApiService
import com.algorand.android.credentials.passkeys.validator.data.network.GStaticApiService
import com.algorand.android.credentials.passkeys.validator.data.repository.DefaultAppInfoValidationRepository
import com.algorand.android.credentials.passkeys.validator.domain.repository.AppInfoValidationRepository
import com.algorand.android.credentials.passkeys.validator.domain.usecase.GetCallingAppOriginCheckingGpmAllowlist
import com.algorand.android.credentials.passkeys.validator.domain.usecase.GetCallingAppOriginCheckingGpmAllowlistUseCase
import com.algorand.android.credentials.passkeys.validator.domain.usecase.IsAssetLinksValid
import com.algorand.android.credentials.passkeys.validator.domain.usecase.IsAssetLinksValidUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object ValidationModule {

    @Provides
    fun provideAppInfoValidationRepository(
        repository: DefaultAppInfoValidationRepository
    ): AppInfoValidationRepository = repository

    @Provides
    @Singleton
    fun provideGStaticApiService(): GStaticApiService {
        val okhttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            .build()
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://www.gstatic.com/")
            .client(okhttpClient)
            .build().create(GStaticApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAssetLinksApiService(): AssetLinksApiService {
        val okhttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            .build()
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://digitalassetlinks.googleapis.com/")
            .client(okhttpClient)
            .build().create(AssetLinksApiService::class.java)
    }

    @Provides
    fun provideGetCallingAppOriginCheckingGpmAllowlist(
        useCase: GetCallingAppOriginCheckingGpmAllowlistUseCase
    ): GetCallingAppOriginCheckingGpmAllowlist = useCase

    @Provides
    fun provideIsAssetLinksValid(useCase: IsAssetLinksValidUseCase): IsAssetLinksValid = useCase

    @Provides
    fun provideCallingAppInfoValidator(validator: PasskeyCallingAppInfoValidator): CallingAppInfoValidator = validator
}
