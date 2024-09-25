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

package com.algorand.android.module.asset.sorting.data.repository

import com.algorand.android.module.asset.sorting.domain.model.AssetSortPreference
import com.algorand.android.module.asset.sorting.domain.repository.AssetSortPreferencesRepository
import com.algorand.android.caching.SharedPrefLocalSource
import javax.inject.Inject

internal class AssetSortPreferencesRepositoryImpl @Inject constructor(
    private val assetSortPreferencesLocalSource: SharedPrefLocalSource<String>
) : AssetSortPreferencesRepository {

    override suspend fun saveAssetSortPreference(sortType: AssetSortPreference?) {
        val safeSortType = sortType ?: DEFAULT_SORT_PREFERENCE
        assetSortPreferencesLocalSource.saveData(safeSortType.name)
    }

    override suspend fun getAssetSortPreference(): AssetSortPreference {
        return AssetSortPreference.entries.firstOrNull {
            it.name == assetSortPreferencesLocalSource.getDataOrNull()
        } ?: DEFAULT_SORT_PREFERENCE
    }

    private companion object {
        val DEFAULT_SORT_PREFERENCE = AssetSortPreference.BALANCE_DESCENDING
    }
}
