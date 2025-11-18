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

package com.algorand.wallet.foundation.database.util

internal object DaoUtils {

    private const val DEFAULT_CHUNK_SIZE = 900

    internal suspend fun <K, T> smartUpsert(
        newEntity: T,
        getKey: (T) -> K,
        fetchExistingByKey: suspend (K) -> T?,
        upsert: suspend (T) -> Unit,
        diff: (T, T) -> Boolean = { a, b -> a != b }
    ) {
        val existing = fetchExistingByKey(getKey(newEntity))
        if (existing == null || diff(newEntity, existing)) {
            upsert(newEntity)
        }
    }

    internal suspend fun <K, T> smartUpsert(
        newEntities: List<T>,
        getKey: (T) -> K,
        fetchExistingByKeys: suspend (List<K>) -> List<T>,
        upsert: suspend (List<T>) -> Unit,
        diff: (T, T) -> Boolean = { a, b -> a != b },
        chunkSize: Int = DEFAULT_CHUNK_SIZE
    ) {
        val entitiesToUpdate = newEntities.chunked(chunkSize).flatMap { chunk ->
            val existingMap = fetchExistingByKeys(chunk.map(getKey)).associateBy(getKey)
            chunk.mapNotNull { new ->
                val old = existingMap[getKey(new)]
                if (old == null || diff(new, old)) new else null
            }
        }
        if (entitiesToUpdate.isNotEmpty()) upsert(entitiesToUpdate)
    }

    internal suspend fun <T> executeChunked(
        items: List<T>,
        chunkSize: Int = DEFAULT_CHUNK_SIZE,
        action: suspend (List<T>) -> Unit
    ) {
        items.chunked(chunkSize).forEach { chunk ->
            action(chunk)
        }
    }
}
