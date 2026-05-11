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

package com.algorand.backup.data.repository

import com.algorand.backup.data.service.BackupWebSocketClient
import com.algorand.backup.domain.model.BackupWebSocketEvent
import com.algorand.backup.domain.repository.BackupWebSocketRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow

internal class DefaultBackupWebSocketRepository @Inject constructor(
    private val webSocketClient: BackupWebSocketClient
) : BackupWebSocketRepository {

    override val events: SharedFlow<BackupWebSocketEvent> get() = webSocketClient.events

    override fun connect(scope: CoroutineScope) {
        webSocketClient.connect(scope)
    }

    override fun disconnect() {
        webSocketClient.disconnect()
    }
}
