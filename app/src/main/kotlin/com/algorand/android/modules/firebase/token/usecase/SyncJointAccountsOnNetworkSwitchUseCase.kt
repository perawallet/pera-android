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

package com.algorand.android.modules.firebase.token.usecase

import com.algorand.android.deviceregistration.domain.usecase.DeviceIdUseCase
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccounts
import com.algorand.wallet.jointaccount.creation.domain.usecase.CreateJointAccount
import com.algorand.wallet.remoteconfig.domain.model.FeatureToggle
import com.algorand.wallet.logger.PeraErrorLogger
import com.algorand.wallet.remoteconfig.domain.usecase.IsFeatureToggleEnabled
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

internal class SyncJointAccountsOnNetworkSwitchUseCase @Inject constructor(
    private val getLocalAccounts: GetLocalAccounts,
    private val createJointAccount: CreateJointAccount,
    private val deviceIdUseCase: DeviceIdUseCase,
    private val isFeatureToggleEnabled: IsFeatureToggleEnabled,
    private val errorLogger: PeraErrorLogger
) : SyncJointAccountsOnNetworkSwitch {

    override operator fun invoke(scope: CoroutineScope) {
        scope.launch {
            if (!isFeatureToggleEnabled(FeatureToggle.JOINT_ACCOUNT.key)) return@launch
            val deviceId = deviceIdUseCase.getSelectedNodeDeviceId().orEmpty()
            if (deviceId.isBlank()) return@launch
            val jointAccounts = getLocalAccounts().filterIsInstance<LocalAccount.Joint>()
            supervisorScope {
                for (account in jointAccounts) {
                    launch {
                        try {
                            createJointAccount(
                                participantAddresses = account.participantAddresses,
                                threshold = account.threshold,
                                version = account.version,
                                deviceId = deviceId
                            )
                        } catch (e: Exception) {
                            errorLogger.logError(e)
                        }
                    }
                }
            }
        }
    }
}
