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

package com.algorand.android.module.account.local.data.database

import androidx.room.*
import com.algorand.android.module.account.local.data.database.AccountDatabase.Companion.DATABASE_VERSION
import com.algorand.android.module.account.local.data.database.dao.*
import com.algorand.android.module.account.local.data.database.model.*

@Database(
    entities = [
        LedgerBleEntity::class,
        LedgerUsbEntity::class,
        NoAuthEntity::class,
        Algo25Entity::class
    ],
    version = DATABASE_VERSION
)
internal abstract class AccountDatabase : RoomDatabase() {

    abstract fun ledgerBleDao(): LedgerBleDao
    abstract fun ledgerUsbDao(): LedgerUsbDao
    abstract fun noAuthDao(): NoAuthDao
    abstract fun algo25Dao(): Algo25Dao

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "account_database"
    }
}
