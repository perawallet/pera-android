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

package com.algorand.backup.data.mapper

import com.algorand.backup.data.api.model.BackupWebSocketEventResponse
import com.algorand.backup.domain.model.BackupWebSocketEvent
import com.google.gson.Gson
import javax.inject.Inject

internal class BackupWebSocketEventMapper @Inject constructor(private val gson: Gson) {

    fun mapToEvent(text: String): BackupWebSocketEvent? {
        return try {
            val response = gson.fromJson(text, BackupWebSocketEventResponse::class.java)
            mapResponseToEvent(response)
        } catch (e: Exception) {
            null
        }
    }

    private fun mapResponseToEvent(response: BackupWebSocketEventResponse): BackupWebSocketEvent {
        return when (response.type) {
            TYPE_ITEMS_UPDATED -> BackupWebSocketEvent.ItemsUpdated(
                fromSeq = response.fromSeq ?: 0,
                toSeq = response.toSeq ?: 0
            )
            else -> BackupWebSocketEvent.Unknown(response.type ?: "null")
        }
    }

    private companion object {
        const val TYPE_ITEMS_UPDATED = "ITEMS_UPDATED"
    }
}
