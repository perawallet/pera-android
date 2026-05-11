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

package com.algorand.wallet.inbox.data.repository

import com.algorand.wallet.foundation.cache.InMemoryCachedObject
import com.algorand.wallet.foundation.cache.PersistentCache
import com.algorand.wallet.inbox.domain.model.InboxMessages
import com.algorand.wallet.inbox.domain.repository.InboxRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

internal class DefaultInboxRepository(
    private val inboxCache: InMemoryCachedObject<InboxMessages>,
    private val inboxCacheFlow: MutableStateFlow<InboxMessages?>,
    private val lastOpenedTimeCache: PersistentCache<String>
) : InboxRepository {

    override fun getInboxMessagesFlow(): Flow<InboxMessages?> = inboxCacheFlow

    override suspend fun getInboxMessages(): InboxMessages? = inboxCache.get()

    override fun getLastOpenedTime(): ZonedDateTime? {
        return lastOpenedTimeCache.get()?.let { timeString ->
            try {
                ZonedDateTime.parse(timeString, DateTimeFormatter.ISO_DATE_TIME)
            } catch (_: Exception) {
                null
            }
        }
    }

    override fun setLastOpenedTime(time: ZonedDateTime) {
        lastOpenedTimeCache.put(time.format(DateTimeFormatter.ISO_DATE_TIME))
    }
}
