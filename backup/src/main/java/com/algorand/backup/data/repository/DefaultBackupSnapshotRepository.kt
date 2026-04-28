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

import android.util.Base64
import com.algorand.backup.account.domain.mapper.AddressBackupPayloadMapper
import com.algorand.backup.account.domain.model.AddressBackupPayload
import com.algorand.backup.contact.domain.model.ContactBackupPayload
import com.algorand.backup.data.model.BackupSnapshotCacheData
import com.algorand.backup.domain.repository.BackupSnapshotRepository
import com.algorand.wallet.foundation.cache.PersistentCache
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class DefaultBackupSnapshotRepository(
    private val persistentCache: PersistentCache<BackupSnapshotCacheData>,
    private val addressMapper: AddressBackupPayloadMapper
) : BackupSnapshotRepository {

    private val mutex = Mutex()

    override suspend fun getAddressPayloads(): List<AddressBackupPayload> {
        val cache = persistentCache.get() ?: return emptyList()
        return cache.addresses.values.mapNotNull { encoded ->
            addressMapper.deserialize(Base64.decode(encoded, Base64.NO_WRAP))
        }
    }

    override suspend fun getContactPayloads(): List<ContactBackupPayload> {
        val cache = persistentCache.get() ?: return emptyList()
        return cache.contacts.map { (address, name) -> ContactBackupPayload(address = address, name = name) }
    }

    override suspend fun upsertAddressPayloads(payloads: List<AddressBackupPayload>) {
        if (payloads.isEmpty()) return
        mutex.withLock {
            val current = persistentCache.get() ?: BackupSnapshotCacheData()
            val updated = current.addresses.toMutableMap().apply {
                payloads.forEach { payload ->
                    put(payload.address, Base64.encodeToString(addressMapper.serialize(payload), Base64.NO_WRAP))
                }
            }
            persistentCache.put(current.copy(addresses = updated))
        }
    }

    override suspend fun upsertContactPayloads(payloads: List<ContactBackupPayload>) {
        if (payloads.isEmpty()) return
        mutex.withLock {
            val current = persistentCache.get() ?: BackupSnapshotCacheData()
            val updated = current.contacts.toMutableMap().apply {
                payloads.forEach { payload -> put(payload.address, payload.name) }
            }
            persistentCache.put(current.copy(contacts = updated))
        }
    }

    override suspend fun removeAddresses(addresses: Collection<String>) {
        if (addresses.isEmpty()) return
        mutex.withLock {
            val current = persistentCache.get() ?: return
            val updated = current.addresses.toMutableMap().apply { addresses.forEach(::remove) }
            persistentCache.put(current.copy(addresses = updated))
        }
    }

    override suspend fun removeContacts(addresses: Collection<String>) {
        if (addresses.isEmpty()) return
        mutex.withLock {
            val current = persistentCache.get() ?: return
            val updated = current.contacts.toMutableMap().apply { addresses.forEach(::remove) }
            persistentCache.put(current.copy(contacts = updated))
        }
    }

    override suspend fun clear() {
        mutex.withLock { persistentCache.clear() }
    }
}
