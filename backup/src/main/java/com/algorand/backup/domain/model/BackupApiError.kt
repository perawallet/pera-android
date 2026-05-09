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

package com.algorand.backup.domain.model

sealed class BackupApiError : Exception() {

    data class AuthenticationFailed(
        val backupId: BackupId
    ) : BackupApiError()

    data class NotFound(
        val backupId: BackupId
    ) : BackupApiError()

    data class VersionConflict(
        val key: BackupItemKey,
        val currentVersion: Int,
        val currentHash: ItemHash?
    ) : BackupApiError()

    data class SeqTooOld(
        val backupId: BackupId
    ) : BackupApiError()

    class BackupAlreadyExists : BackupApiError() {
        private fun readResolve(): Any = BackupAlreadyExists()
    }

    data class NetworkError(
        override val cause: Throwable?
    ) : BackupApiError()

    data class ServerError(
        val statusCode: Int,
        override val message: String?
    ) : BackupApiError()
}
