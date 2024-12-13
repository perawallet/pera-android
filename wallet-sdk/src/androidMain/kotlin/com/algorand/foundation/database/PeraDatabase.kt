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

package com.algorand.foundation.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.algorand.common.foundation.database.PeraDatabase
import kotlinx.coroutines.Dispatchers

internal fun getPeraDatabase(context: Context): PeraDatabase {
    return getPeraDatabaseBuilder(context).build()
}

internal fun getPeraDatabaseBuilder(context: Context): RoomDatabase.Builder<PeraDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath(PeraDatabase.DATABASE_NAME)
    return Room.databaseBuilder<PeraDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    ).setQueryCoroutineContext(Dispatchers.IO)
}
