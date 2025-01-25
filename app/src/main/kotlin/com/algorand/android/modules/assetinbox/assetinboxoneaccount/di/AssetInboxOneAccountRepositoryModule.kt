/*
 *  Copyright 2022 Pera Wallet, LDA
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.algorand.android.modules.assetinbox.assetinboxoneaccount.di

import com.algorand.android.modules.assetinbox.assetinboxoneaccount.data.mapper.AssetInboxOneAccountMapperImpl
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.data.mapper.AssetInboxOneAccountMapper
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.data.repository.AssetInboxOneAccountRepositoryImpl
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.data.service.AssetInboxOneAccountApiService
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.domain.repository.AssetInboxOneAccountRepository
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.domain.usecase.GetAssetInboxOneAccountPaginated
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.ui.mapper.Arc59ReceiveDetailNavArgsMapper
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.ui.mapper.Arc59ReceiveDetailNavArgsMapperImpl
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.ui.mapper.AssetInboxOneAccountPreviewMapper
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.ui.mapper.AssetInboxOneAccountPreviewMapperImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
internal object AssetInboxOneAccountRepositoryModule {
    @Provides
    @Singleton
    fun provideAssetInboxOneAccountMapper(
        assetInboxOneAccountMapper: AssetInboxOneAccountMapperImpl
    ): AssetInboxOneAccountMapper = assetInboxOneAccountMapper

    @Provides
    @Singleton
    fun provideArc59ReceiveDetailNavArgsMapper(
        arc59ReceiveDetailNavArgsMapper: Arc59ReceiveDetailNavArgsMapperImpl
    ): Arc59ReceiveDetailNavArgsMapper = arc59ReceiveDetailNavArgsMapper

    @Provides
    @Singleton
    fun provideAssetInboxOneAccountPreviewMapper(
        assetInboxOneAccountPreviewMapper: AssetInboxOneAccountPreviewMapperImpl
    ): AssetInboxOneAccountPreviewMapper = assetInboxOneAccountPreviewMapper

    @Provides
    @Singleton
    fun provideAssetInboxOneAccountApiService(
        @Named("mobileAlgorandRetrofitInterface") retrofit: Retrofit
    ): AssetInboxOneAccountApiService {
        return retrofit.create(AssetInboxOneAccountApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideGetAssetInboxOneAccountPaginated(
        repository: AssetInboxOneAccountRepository
    ): GetAssetInboxOneAccountPaginated = GetAssetInboxOneAccountPaginated(repository::getAssetInboxOneAccount)

    @Provides
    @Singleton
    fun provideAssetInboxOneAccountRepository(
        repository: AssetInboxOneAccountRepositoryImpl
    ): AssetInboxOneAccountRepository = repository
}
