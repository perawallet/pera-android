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
import com.algorand.android.banner.domain.usecase.BannersUseCase
import com.algorand.android.core.LegacyAccountManager
import com.algorand.android.modules.walletconnect.domain.WalletConnectManager
import com.algorand.android.repository.ContactRepository
import com.algorand.wallet.account.custom.domain.usecase.ClearAllCustomInformation
import com.algorand.wallet.account.local.domain.usecase.DeleteAllLocalAccounts
import javax.inject.Inject

class DeleteAllDataUseCase @Inject constructor(
    private val contactRepository: ContactRepository,
    private val legacyAccountManager: LegacyAccountManager,
    private val walletConnectManager: WalletConnectManager,
    private val coreCacheUseCase: CoreCacheUseCase,
    private val bannersUseCase: BannersUseCase,
    private val deleteAllLocalAccounts: DeleteAllLocalAccounts,
    private val notificationManager: NotificationManager?,
    private val clearAllCustomInformation: ClearAllCustomInformation
) {
    suspend fun deleteAllData() {
        legacyAccountManager.removeAllData()
        walletConnectManager.killAllSessions()
        deleteAllLocalAccounts()
        clearAllCustomInformation()
        contactRepository.deleteAllContacts()
        coreCacheUseCase.clearAllCachedData()
        bannersUseCase.clearBannerCacheAndDismissedBannerIdList()
        notificationManager?.cancelAll()
    }
}
