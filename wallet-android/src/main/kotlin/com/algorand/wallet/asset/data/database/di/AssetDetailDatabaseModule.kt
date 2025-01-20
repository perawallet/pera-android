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

package com.algorand.wallet.asset.data.database.di

import com.algorand.wallet.asset.data.database.dao.AssetDetailDao
import com.algorand.wallet.asset.data.database.dao.CollectibleDao
import com.algorand.wallet.asset.data.database.dao.CollectibleMediaDao
import com.algorand.wallet.asset.data.database.dao.CollectibleTraitDao
import com.algorand.wallet.foundation.database.PeraDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AssetDetailDatabaseModule {

    @Provides
    @Singleton
    fun provideAssetDetailDao(database: PeraDatabase): AssetDetailDao {
        return database.assetDetailDao()
    }

    @Provides
    @Singleton
    fun provideCollectibleDao(database: PeraDatabase): CollectibleDao {
        return database.collectibleDao()
    }

    @Provides
    @Singleton
    fun provideCollectibleMediaDao(database: PeraDatabase): CollectibleMediaDao {
        return database.collectibleMediaDao()
    }

    @Provides
    @Singleton
    fun provideCollectibleTraitDao(database: PeraDatabase): CollectibleTraitDao {
        return database.collectibleTraitDao()
    }
}
