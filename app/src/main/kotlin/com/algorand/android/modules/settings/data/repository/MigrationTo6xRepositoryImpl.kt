/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.settings.data.repository

import com.algorand.android.modules.settings.domain.repository.MigrationTo6xRepository
import com.algorand.wallet.foundation.cache.PersistentCache

internal class MigrationTo6xRepositoryImpl(
    private val migrateTo6x: PersistentCache<Boolean>,
) : MigrationTo6xRepository {

    override suspend fun saveMigratedTo6xCheck(check: Boolean) {
        migrateTo6x.put(check)
    }

    override suspend fun getMigratedTo6xCheck(): Boolean {
        return migrateTo6x.get() ?: false
    }
}
