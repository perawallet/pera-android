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

package com.algorand.wallet.account.local.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.algorand.wallet.account.local.data.database.model.Algo25Entity
import com.algorand.wallet.account.local.data.database.model.NoAuthEntity

@Dao
internal interface Algo25NoAuthDao {

    @Upsert
    suspend fun upsertNoAuthEntities(entities: List<NoAuthEntity>)

    @Query("DELETE FROM algo_25 WHERE algo_address IN (:algoAddresses)")
    suspend fun deleteAlgo25Entities(algoAddresses: List<String>)

    @Query("SELECT * FROM algo_25")
    suspend fun getAllAlgo25Entities(): List<Algo25Entity>

    @Transaction
    suspend fun updateAlgo25AccountsToNoAuthAccounts(entities: List<NoAuthEntity>) {
        deleteAlgo25Entities(entities.map { it.algoAddress })
        upsertNoAuthEntities(entities)
    }
}
