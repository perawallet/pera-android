/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.usecase

import android.app.NotificationManager
import com.algorand.android.module.appcache.usecase.ClearAppSessionCache
import com.algorand.android.module.banner.domain.usecase.ClearDismissedBannerIds
import com.algorand.android.contacts.component.domain.usecase.DeleteAllContacts
import com.algorand.android.core.AccountManager
import com.algorand.android.modules.walletconnect.domain.WalletConnectManager
import javax.inject.Inject

class DeleteAllDataUseCase @Inject constructor(
    private val deleteAllContacts: DeleteAllContacts,
    private val accountManager: AccountManager,
    private val walletConnectManager: WalletConnectManager,
    private val clearAppSessionCache: ClearAppSessionCache,
    private val clearDismissedBannerIds: ClearDismissedBannerIds
) {
    suspend fun deleteAllData(notificationManager: NotificationManager?, onDeletionCompleted: suspend (() -> Unit)) {
        accountManager.removeAllData()
        deleteAllContacts()
        walletConnectManager.killAllSessions()
        clearAppSessionCache()
        clearDismissedBannerIds()
        notificationManager?.cancelAll()
        onDeletionCompleted()
    }
}
