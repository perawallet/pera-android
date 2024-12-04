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

package com.algorand.common.account.local.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.algorand.common.account.local.data.database.AccountDatabase.Companion.DATABASE_VERSION
import com.algorand.common.account.local.data.database.dao.Algo25Dao
import com.algorand.common.account.local.data.database.dao.LedgerBleDao
import com.algorand.common.account.local.data.database.dao.NoAuthDao
import com.algorand.common.account.local.data.database.model.Algo25Entity
import com.algorand.common.account.local.data.database.model.LedgerBleEntity
import com.algorand.common.account.local.data.database.model.NoAuthEntity

@Database(
    entities = [
        LedgerBleEntity::class,
        NoAuthEntity::class,
        Algo25Entity::class
    ],
    version = DATABASE_VERSION
)
@ConstructedBy(AppDatabaseConstructor::class)
internal abstract class AccountDatabase : RoomDatabase() {

    abstract fun ledgerBleDao(): LedgerBleDao
    abstract fun noAuthDao(): NoAuthDao
    abstract fun algo25Dao(): Algo25Dao

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "account_database"
    }
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
internal expect object AppDatabaseConstructor : RoomDatabaseConstructor<AccountDatabase> {
    override fun initialize(): AccountDatabase
}
