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

package com.algorand.android.ui.settings.developersettings

import androidx.lifecycle.viewModelScope
import com.algorand.android.core.BaseViewModel
import com.algorand.android.models.AccountCreation
import com.algorand.android.ui.settings.usecase.DeveloperSettingsPreviewUseCase
import com.algorand.android.utils.analytics.CreationType
import com.algorand.wallet.algosdk.transaction.sdk.AlgoAccountSdk
import com.algorand.wallet.encryption.domain.manager.AESPlatformManager
import com.algorand.wallet.remoteconfig.domain.usecase.ENABLE_ACCOUNT_DB_MIGRATION_VIEWER
import com.algorand.wallet.remoteconfig.domain.usecase.HD_WALLET_BUTTON_TOGGLE
import com.algorand.wallet.remoteconfig.domain.usecase.IsFeatureToggleEnabled
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class DeveloperSettingsViewModel @Inject constructor(
    private val developerSettingsPreviewUseCase: DeveloperSettingsPreviewUseCase,
    private val isFeatureToggleEnabled: IsFeatureToggleEnabled,
    private val algoAccountSdk: AlgoAccountSdk,
    private val aesPlatformManager: AESPlatformManager,
) : BaseViewModel() {

    var firstAccountAddress: String? = null

    init {
        updateFirstAccountAddress()
    }

    fun isConnectedToTestnet(): Boolean {
        return developerSettingsPreviewUseCase.isConnectedToTestnet()
    }

    private fun updateFirstAccountAddress() {
        viewModelScope.launch {
            firstAccountAddress = developerSettingsPreviewUseCase.getFirstAccountAddress()
        }
    }

    fun showMigrationViewer(): Boolean {
        return isFeatureToggleEnabled
            .invoke(ENABLE_ACCOUNT_DB_MIGRATION_VIEWER)
    }

    fun showCreateLegacyAlgo25Account(): Boolean {
        return isFeatureToggleEnabled
            .invoke(HD_WALLET_BUTTON_TOGGLE)
    }

    fun createAlgo25Account(): AccountCreation? {
        val account = algoAccountSdk.createAlgo25Account()
            ?: return null

        return AccountCreation(
            address = account.address,
            customName = null,
            isBackedUp = false,
            type = AccountCreation.Type.Algo25(
                aesPlatformManager.encryptByteArray(account.secretKey)
            ),
            creationType = CreationType.CREATE
        )
    }
}
