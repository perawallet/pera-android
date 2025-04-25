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

package com.algorand.android.modules.backupprotocol.domain.usecase

import com.algorand.android.deviceregistration.domain.usecase.DeviceIdUseCase
import com.algorand.android.modules.backupprotocol.model.BackupProtocolElement
import com.algorand.android.modules.backupprotocol.model.BackupProtocolPayload
import com.algorand.android.utils.extensions.encodeBase64
import com.algorand.android.utils.toShortenedAddress
import com.algorand.wallet.account.custom.domain.usecase.GetAccountCustomName
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetAlgo25SecretKey
import com.algorand.wallet.asb.domain.usecase.GetAsbEligibleAccounts
import com.algorand.wallet.asb.domain.utils.BackupProtocolConstants.ALGO_25_ACCOUNT_TYPE_NAME
import com.algorand.wallet.asb.domain.utils.BackupProtocolConstants.NO_AUTH_ACCOUNT_TYPE_NAME
import javax.inject.Inject

class CreateBackupProtocolPayloadUseCase @Inject constructor(
    private val deviceIdUseCase: DeviceIdUseCase,
    private val getAccountCustomName: GetAccountCustomName,
    private val getAlgo25SecretKey: GetAlgo25SecretKey,
    private val getAsbEligibleAccounts: GetAsbEligibleAccounts
) {

    suspend operator fun invoke(): BackupProtocolPayload? {
        val deviceId = deviceIdUseCase.getSelectedNodeDeviceId() ?: return null
        val accountBackupProtocolElementList = getAsbEligibleAccounts().mapNotNull { localAccount ->
            getAccountBackupProtocolElement(localAccount)
        }
        return BackupProtocolPayload(
            deviceId = deviceId,
            providerName = DEFAULT_PROVIDER_NAME,
            accounts = accountBackupProtocolElementList
        )
    }

    private suspend fun getAccountBackupProtocolElement(localAccount: LocalAccount): BackupProtocolElement? {
        val accountType = convertAccountTypeToBackupProtocolAccountType(localAccount) ?: return null
        val address = localAccount.algoAddress
        return BackupProtocolElement(
            address = address,
            name = getAccountCustomName(address) ?: address.toShortenedAddress(),
            accountType = accountType,
            privateKey = getAlgo25SecretKey(address)?.encodeBase64().orEmpty(),
            metadata = null
        )
    }

    private fun convertAccountTypeToBackupProtocolAccountType(account: LocalAccount): String? {
        return when (account) {
            is LocalAccount.Algo25 -> ALGO_25_ACCOUNT_TYPE_NAME
            is LocalAccount.HdKey -> null // TODO
            is LocalAccount.LedgerBle -> null
            is LocalAccount.NoAuth -> NO_AUTH_ACCOUNT_TYPE_NAME
        }
    }

    companion object {
        private const val DEFAULT_PROVIDER_NAME = "Pera Wallet"
    }
}
