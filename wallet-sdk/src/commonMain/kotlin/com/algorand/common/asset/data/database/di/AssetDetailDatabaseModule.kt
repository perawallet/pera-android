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

package com.algorand.common.asset.data.database.di

import com.algorand.common.asset.data.database.dao.AssetDetailDao
import com.algorand.common.asset.data.database.dao.CollectibleDao
import com.algorand.common.asset.data.database.dao.CollectibleMediaDao
import com.algorand.common.asset.data.database.dao.CollectibleTraitDao
import com.algorand.common.foundation.database.PeraDatabase
import org.koin.dsl.module

internal val assetDetailDatabaseModule = module {
    single<AssetDetailDao> {
        get<PeraDatabase>().assetDetailDao()
    }
    single<CollectibleDao> {
        get<PeraDatabase>().collectibleDao()
    }
    single<CollectibleMediaDao> {
        get<PeraDatabase>().collectibleMediaDao()
    }
    single<CollectibleTraitDao> {
        get<PeraDatabase>().collectibleTraitDao()
    }
}
