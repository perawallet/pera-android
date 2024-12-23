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

package com.algorand.common.foundation.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.algorand.common.account.info.data.database.dao.AccountInformationDao
import com.algorand.common.account.info.data.database.dao.AssetHoldingDao
import com.algorand.common.account.info.data.database.model.AccountInformationEntity
import com.algorand.common.account.info.data.database.model.AssetHoldingEntity
import com.algorand.common.asset.data.database.dao.AssetDetailDao
import com.algorand.common.asset.data.database.dao.CollectibleDao
import com.algorand.common.asset.data.database.dao.CollectibleMediaDao
import com.algorand.common.asset.data.database.dao.CollectibleTraitDao
import com.algorand.common.asset.data.database.model.AssetDetailEntity
import com.algorand.common.asset.data.database.model.CollectibleEntity
import com.algorand.common.asset.data.database.model.CollectibleMediaEntity
import com.algorand.common.asset.data.database.model.CollectibleTraitEntity

@Database(
    entities = [
        AccountInformationEntity::class,
        AssetHoldingEntity::class,
        AssetDetailEntity::class,
        CollectibleEntity::class,
        CollectibleMediaEntity::class,
        CollectibleTraitEntity::class
    ],
    version = PeraDatabase.DATABASE_VERSION
)
@ConstructedBy(PeraDatabaseConstructor::class)
internal abstract class PeraDatabase : RoomDatabase() {

    abstract fun accountInformationDao(): AccountInformationDao
    abstract fun assetHoldingDao(): AssetHoldingDao
    abstract fun assetDetailDao(): AssetDetailDao
    abstract fun collectibleDao(): CollectibleDao
    abstract fun collectibleMediaDao(): CollectibleMediaDao
    abstract fun collectibleTraitDao(): CollectibleTraitDao

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "pera_database"
    }
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
internal expect object PeraDatabaseConstructor : RoomDatabaseConstructor<PeraDatabase> {
    override fun initialize(): PeraDatabase
}
