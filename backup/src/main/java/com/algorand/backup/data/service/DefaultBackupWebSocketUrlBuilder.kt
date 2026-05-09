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

package com.algorand.backup.data.service

import com.algorand.backup.domain.repository.BackupSessionRepository
import com.algorand.backup.domain.security.BackupRequestSigner
import com.algorand.wallet.foundation.PeraResult
import java.net.URLEncoder
import java.time.Instant
import javax.inject.Inject

internal class DefaultBackupWebSocketUrlBuilder @Inject constructor(
    private val backupSessionRepository: BackupSessionRepository,
    private val requestSigner: BackupRequestSigner,
    private val baseUrl: String,
) : BackupWebSocketUrlBuilder {

    override fun buildUrl(): PeraResult<String> {
        val backupId = backupSessionRepository.getBackupId() ?: return PeraResult.Error(IllegalArgumentException())
        val deviceId = backupSessionRepository.getDeviceId() ?: return PeraResult.Error(IllegalArgumentException())

        val timestamp = Instant.now().toString()
        val signatureResult = requestSigner.createWebSocketToken(backupId, deviceId, timestamp)
        val signature = signatureResult.getDataOrNull()?.let { URLEncoder.encode(it, "UTF-8") }
            ?: return PeraResult.Error(IllegalStateException("Failed to create signature"))

        val wsBaseUrl = baseUrl
            .replace("http://", "ws://")
            .replace("https://", "wss://")
            .removeSuffix("/")

        val url = "$wsBaseUrl/backup/${backupId.value}?device_id=${deviceId.value}&ts=$timestamp&signature=$signature"
        return PeraResult.Success(url)
    }
}
