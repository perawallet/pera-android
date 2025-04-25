/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.ui.settings.usecase

import com.algorand.android.ui.settings.mapper.SettingsPreviewMapper
import com.algorand.android.ui.settings.model.SettingsPreview
import com.algorand.wallet.account.custom.domain.usecase.GetNotBackedUpAccounts
import com.algorand.wallet.analytics.domain.usecase.GetFirebaseInstanceIdUseCase
import com.algorand.wallet.asb.domain.usecase.GetAsbEligibleAccounts
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SettingsPreviewUseCase @Inject constructor(
    private val settingsPreviewMapper: SettingsPreviewMapper,
    private val getNotBackupAccounts: GetNotBackedUpAccounts,
    private val getFirebaseInstanceIdUseCase: GetFirebaseInstanceIdUseCase,
    private val getAsbEligibleAccounts: GetAsbEligibleAccounts
) {

    fun getSettingsPreviewFlow(): Flow<SettingsPreview> = flow {
        val eligibleToBackUpAddresses = getEligibleToBackUpAddresses()
        val preview = settingsPreviewMapper.mapToSettingsPreview(
            isAlgorandSecureBackupDescriptionVisible = eligibleToBackUpAddresses.isNotEmpty(),
            notBackedUpAccountCounts = eligibleToBackUpAddresses.size,
            firebaseInstanceId = getFirebaseInstanceIdUseCase()
        )
        emit(preview)
    }

    private suspend fun getEligibleToBackUpAddresses(): List<String> {
        val notBackedUpAccounts = getNotBackupAccounts()
        val asbEligibleAccounts = getAsbEligibleAccounts()
        return notBackedUpAccounts.mapNotNull { address ->
            asbEligibleAccounts.find { it.algoAddress == address }?.algoAddress
        }
    }
}
