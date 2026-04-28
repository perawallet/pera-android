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

package com.algorand.backup.domain.usecase

import com.algorand.backup.account.domain.model.AddressBackupPayload
import com.algorand.backup.contact.domain.model.ContactBackupPayload
import com.algorand.backup.domain.model.BackupWebSocketEvent
import com.algorand.backup.domain.model.Argon2idConfig
import com.algorand.backup.domain.model.BackupId
import com.algorand.backup.domain.model.BackupItemKey
import com.algorand.backup.domain.model.CreatedBackup
import com.algorand.backup.domain.model.DerivedKeyMaterial
import com.algorand.backup.domain.model.DeviceId
import com.algorand.backup.domain.model.LatestSync
import com.algorand.backup.domain.model.PullSyncResult
import com.algorand.backup.domain.model.DeletedBackupItems
import com.algorand.backup.domain.model.PushedDirtyBackupItems
import com.algorand.backup.domain.model.PushSyncResult
import com.algorand.backup.domain.model.RestoredBackup
import com.algorand.backup.domain.model.SensitiveBytes
import com.algorand.backup.domain.model.SyncBackupResult
import com.algorand.backup.domain.model.SyncItemState
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.foundation.PeraResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow

interface CreateBackup {
    suspend operator fun invoke(mnemonic: String, deviceId: DeviceId): PeraResult<CreatedBackup>
}

interface RestoreBackup {
    suspend operator fun invoke(
        mnemonic: String,
        salt: ByteArray,
        argon2idConfig: Argon2idConfig,
        deviceId: DeviceId
    ): PeraResult<RestoredBackup>
}

interface PullBackupSync {
    suspend operator fun invoke(backupId: BackupId): PullSyncResult
}

interface PushBackupSync {
    suspend operator fun invoke(
        backupId: BackupId,
        deviceId: DeviceId,
        encryptedPayloads: Map<BackupItemKey, String>
    ): PushSyncResult
}

internal interface PushDirtyBackupItems {
    suspend operator fun invoke(
        backupId: BackupId,
        deviceId: DeviceId,
        dirtyItems: Map<BackupItemKey, SyncItemState>,
        encryptedPayloads: Map<BackupItemKey, String>
    ): PeraResult<PushedDirtyBackupItems>
}

internal interface DeletePendingBackupItems {
    suspend operator fun invoke(
        backupId: BackupId,
        pendingDeletes: Map<BackupItemKey, SyncItemState>
    ): PeraResult<DeletedBackupItems>
}

interface DeleteBackupItem {
    suspend operator fun invoke(backupId: BackupId, key: BackupItemKey, deleteFromServer: Boolean)
}

interface EncryptBackupPayloads {
    suspend operator fun invoke(payloads: Map<BackupItemKey, ByteArray>): PeraResult<Map<BackupItemKey, String>>
}

interface DecryptBackupPayloads {
    suspend operator fun invoke(
        backupId: BackupId,
        keys: List<BackupItemKey>
    ): PeraResult<Map<BackupItemKey, ByteArray>>
}

fun interface HasBackup {
    operator fun invoke(): Boolean
}

fun interface GetBackupId {
    operator fun invoke(): BackupId?
}

fun interface GetBackupDeviceId {
    operator fun invoke(): DeviceId?
}

internal fun interface StoreBackupCredentials {
    operator fun invoke(backupId: BackupId, deviceId: DeviceId, authPrivateKey: SensitiveBytes): PeraResult<Unit>
}

internal interface UseBackupPrivateKey {
    operator fun <T : Any> invoke(block: (SensitiveBytes) -> T): PeraResult<T>
}

internal fun interface ClearBackupCredentials {
    operator fun invoke()
}

interface ReactivateBackupItem {
    suspend operator fun invoke(backupId: BackupId, key: BackupItemKey)
}

internal fun interface RegisterBackup {
    suspend operator fun invoke(keyMaterial: DerivedKeyMaterial, deviceId: DeviceId): PeraResult<Unit>
}

interface DisableBackup {
    suspend operator fun invoke()
}

interface DeleteBackup {
    suspend operator fun invoke(): PeraResult<Unit>
}

interface PullAndImportSync {
    suspend operator fun invoke(): SyncBackupResult
}

interface PreparePushPayloads {
    suspend operator fun invoke(backupId: BackupId): PeraResult<Map<BackupItemKey, String>>
}

interface SyncBackup {
    suspend operator fun invoke(): SyncBackupResult
}

fun interface GetLatestSync {
    suspend operator fun invoke(): LatestSync?
}

fun interface SaveBackupSyncResult {
    suspend operator fun invoke(result: SyncBackupResult)
}

fun interface GetAddressBackupSnapshot {
    suspend operator fun invoke(): List<AddressBackupPayload>
}

fun interface GetContactBackupSnapshot {
    suspend operator fun invoke(): List<ContactBackupPayload>
}

internal fun interface ConnectBackupWebSocket {
    operator fun invoke(scope: CoroutineScope)
}

internal fun interface DisconnectBackupWebSocket {
    operator fun invoke()
}

internal fun interface GetBackupWebSocketEvents {
    operator fun invoke(): SharedFlow<BackupWebSocketEvent>
}
