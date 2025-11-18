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

package com.algorand.android.credentials.passkeys.data.repository

import com.algorand.android.credentials.passkeys.data.database.PasskeyDao
import com.algorand.android.credentials.passkeys.data.database.PasskeySiteDao
import com.algorand.android.credentials.passkeys.data.mapper.PasskeyEntityMapper
import com.algorand.android.credentials.passkeys.data.mapper.PasskeyMapper
import com.algorand.android.credentials.passkeys.data.model.SiteEntity
import com.algorand.android.credentials.passkeys.domain.model.AddPasskeyArgs
import com.algorand.android.credentials.passkeys.domain.model.Passkey
import com.algorand.android.credentials.passkeys.domain.repository.PasskeyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class DefaultPasskeyRepository @Inject constructor(
    private val passkeyDao: PasskeyDao,
    private val passkeySiteDao: PasskeySiteDao,
    private val passkeyMapper: PasskeyMapper,
    private val passkeyEntityMapper: PasskeyEntityMapper
) : PasskeyRepository {

    override fun getAllPasskeysAsFlow(): Flow<List<Passkey>> {
        return passkeySiteDao.getPasskeysAsFlow().map { siteWithPasskeysQueries ->
            siteWithPasskeysQueries.map { siteWithPasskeys ->
                siteWithPasskeys.passkeys.map { passkeyEntity ->
                    passkeyMapper.mapToPasskey(passkeyEntity, siteWithPasskeys.site)
                }
            }.flatten()
        }
    }

    override suspend fun getSitePasskeysCount(url: String): Int {
        val siteId = passkeySiteDao.getSiteId(url) ?: return 0
        return passkeyDao.getPasskeyCountBySiteId(siteId)
    }

    override suspend fun getSitePasskeys(url: String): List<Passkey> {
        return passkeySiteDao.getPasskeysByUrl(url).map {
            it.passkeys.map { passkeyEntity -> passkeyMapper.mapToPasskey(passkeyEntity, it.site) }
        }.flatten()
    }

    override suspend fun addNewPasskey(args: AddPasskeyArgs) {
        val siteId = passkeySiteDao.getSiteId(args.siteUrl)
        val passkeyEntity = if (siteId == null) {
            val entity = SiteEntity(url = args.siteUrl, name = args.siteName)
            val newSiteId = passkeySiteDao.insert(entity)
            passkeyEntityMapper.mapToPasskeyEntity(args, newSiteId)
        } else {
            passkeyEntityMapper.mapToPasskeyEntity(args, siteId)
        }
        passkeyDao.insert(passkeyEntity)
    }

    override suspend fun getPasskey(credId: String): Passkey? {
        val passkeyEntity = passkeyDao.getByCredId(credId) ?: return null
        val siteEntity = passkeySiteDao.getSiteById(passkeyEntity.siteId) ?: return null
        return passkeyMapper.mapToPasskey(passkeyEntity, siteEntity)
    }

    override suspend fun removePasskeyByCredentialId(credId: String) {
        val passkey = passkeyDao.getByCredId(credId)
        if (passkey != null) {
            passkeyDao.deleteByCredId(credId)
            val passkeyCountForSite = passkeyDao.getPasskeyCountBySiteId(passkey.siteId)
            if (passkeyCountForSite == 0) {
                passkeySiteDao.delete(passkey.siteId)
            }
        }
    }

    override suspend fun clearAllPasskeys() {
        passkeyDao.deleteAll()
        passkeySiteDao.deleteAll()
    }

    override suspend fun setPasskeyLastUsedTime(credId: String, lastUsed: Long) {
        passkeyDao.updateLastUsedTime(credId, lastUsed)
    }

    override suspend fun doesPasskeyExist(rpId: String, username: String, bip44Address: String): Boolean {
        return passkeyDao.doesPasskeyExist(rpId, username, bip44Address)
    }
}
