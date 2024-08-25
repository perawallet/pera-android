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

package com.algorand.android.assetsorting.di

import android.content.SharedPreferences
import com.algorand.android.assetsorting.data.repository.AssetSortPreferencesRepositoryImpl
import com.algorand.android.assetsorting.data.storage.AssetSortPreferencesLocalSource
import com.algorand.android.assetsorting.domain.repository.AssetSortPreferencesRepository
import com.algorand.android.assetsorting.domain.usecase.GetAssetSortPreference
import com.algorand.android.assetsorting.domain.usecase.SetAssetSortPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AssetSortingModule {

    @Provides
    @Singleton
    fun provideAssetSortPreferencesRepository(
        sharedPreferences: SharedPreferences
    ): AssetSortPreferencesRepository {
        return AssetSortPreferencesRepositoryImpl(AssetSortPreferencesLocalSource(sharedPreferences))
    }

    @Provides
    @Singleton
    fun provideGetAssetSortPreference(
        repository: AssetSortPreferencesRepository
    ): GetAssetSortPreference = GetAssetSortPreference(repository::getAssetSortPreference)

    @Provides
    @Singleton
    fun provideSetAssetSortPreference(
        repository: AssetSortPreferencesRepository
    ): SetAssetSortPreference = SetAssetSortPreference(repository::saveAssetSortPreference)
}
