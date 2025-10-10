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

package com.algorand.android.credentials.passkeys.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.algorand.android.credentials.passkeys.data.model.PasskeyEntity
import com.algorand.android.credentials.passkeys.data.model.SiteEntity

@Database(entities = [PasskeyEntity::class, SiteEntity::class], version = PasskeyDatabase.VERSION)
internal abstract class PasskeyDatabase : RoomDatabase() {

    abstract fun passkeySiteDao(): PasskeySiteDao
    abstract fun passkeyDao(): PasskeyDao

    companion object {
        const val DATABASE_NAME = "passkey_db"
        const val VERSION = 1
    }
}
