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

package com.algorand.android.modules.walletconnect.client.v1.session

import app.perawallet.walletconnectv1.impls.WCSession
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletConnectV1SessionCachedDataHandler @Inject constructor() {

    private val connectedSessions: ConcurrentHashMap<Long, WalletConnectV1SessionCachedData> =
        ConcurrentHashMap(INITIAL_CAPACITY)

    fun getSessionById(id: Long): WCSession? = connectedSessions[id]?.session

    fun getCachedDataById(id: Long): WalletConnectV1SessionCachedData? = connectedSessions[id]

    fun addNewCachedData(sessionCachedData: WalletConnectV1SessionCachedData) {
        if (connectedSessions.contains(sessionCachedData.sessionId)) return
        connectedSessions[sessionCachedData.sessionId] = sessionCachedData
    }

    fun deleteCachedData(sessionId: Long, onCacheDeleted: (WalletConnectV1SessionCachedData) -> Unit) {
        connectedSessions.remove(sessionId)?.also { onCacheDeleted(it) }
    }

    private companion object {
        const val INITIAL_CAPACITY = 50
    }
}
