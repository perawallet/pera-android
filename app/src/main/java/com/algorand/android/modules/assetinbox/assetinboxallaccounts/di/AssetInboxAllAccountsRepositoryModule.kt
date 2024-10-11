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

package com.algorand.android.modules.assetinbox.assetinboxallaccounts.di

import com.algorand.android.modules.assetinbox.assetinboxallaccounts.data.mapper.AssetInboxAllAccountsMapper
import com.algorand.android.modules.assetinbox.assetinboxallaccounts.data.mapper.AssetInboxAllAccountsMapperImpl
import com.algorand.android.modules.assetinbox.assetinboxallaccounts.data.repository.AssetInboxAllAccountsRepositoryImpl
import com.algorand.android.modules.assetinbox.assetinboxallaccounts.data.service.AssetInboxAllAccountsApiService
import com.algorand.android.modules.assetinbox.assetinboxallaccounts.domain.repository.AssetInboxAllAccountsRepository
import com.algorand.android.modules.assetinbox.assetinboxallaccounts.domain.usecase.GetAssetInboxAllAccounts
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.ui.mapper.AssetInboxAllAccountsPreviewMapper
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.ui.mapper.AssetInboxAllAccountsPreviewMapperImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object AssetInboxAllAccountsRepositoryModule {

    @Provides
    @Singleton
    fun provideAssetInboxAllAccountsRepository(
        repository: AssetInboxAllAccountsRepositoryImpl
    ): AssetInboxAllAccountsRepository = repository

    @Provides
    @Singleton
    fun provideAssetInboxAllAccountsApiService(
        @Named("mobileAlgorandRetrofitInterface") retrofit: Retrofit
    ): AssetInboxAllAccountsApiService {
        return retrofit.create(AssetInboxAllAccountsApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideGetAssetInboxAllAccounts(
        repository: AssetInboxAllAccountsRepository
    ): GetAssetInboxAllAccounts = GetAssetInboxAllAccounts(repository::getAssetInboxAllAccounts)

    @Provides
    @Singleton
    fun provideAssetInboxAllAccountsMapper(
        assetInboxAllAccountsMapperImpl: AssetInboxAllAccountsMapperImpl
    ): AssetInboxAllAccountsMapper = assetInboxAllAccountsMapperImpl

    @Provides
    @Singleton
    fun provideAssetInboxAllAccountsPreviewMapper(
        assetInboxAllAccountsPreviewMapperImpl: AssetInboxAllAccountsPreviewMapperImpl
    ): AssetInboxAllAccountsPreviewMapper = assetInboxAllAccountsPreviewMapperImpl
}
