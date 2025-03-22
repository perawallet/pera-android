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

package com.algorand.android.modules.backupprotocol.domain.usecase

import com.algorand.android.deviceregistration.domain.usecase.DeviceIdUseCase
import com.algorand.android.modules.asb.util.AlgorandSecureBackupUtils.isAccountEligible
import com.algorand.android.modules.backupprotocol.model.BackupProtocolElement
import com.algorand.android.modules.backupprotocol.model.BackupProtocolPayload
import com.algorand.android.utils.extensions.encodeBase64
import com.algorand.android.utils.toShortenedAddress
import com.algorand.wallet.account.custom.domain.usecase.GetAccountCustomName
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetAlgo25SecretKey
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccount
import com.algorand.wallet.asb.domain.usecase.BackupProtocolConstants.SINGLE_ACCOUNT_TYPE_NAME
import com.algorand.wallet.asb.domain.usecase.BackupProtocolConstants.WATCH_ACCOUNT_TYPE_NAME
import javax.inject.Inject

class CreateBackupProtocolPayloadUseCase @Inject constructor(
    private val deviceIdUseCase: DeviceIdUseCase,
    private val getLocalAccount: GetLocalAccount,
    private val getAccountCustomName: GetAccountCustomName,
    private val getAlgo25SecretKey: GetAlgo25SecretKey
) {

    suspend operator fun invoke(accountAddresses: List<String>): BackupProtocolPayload? {
        val deviceId = deviceIdUseCase.getSelectedNodeDeviceId() ?: return null
        val accountBackupProtocolElementList = accountAddresses.mapNotNull { accountAddress ->
            getAccountBackupProtocolElement(accountAddress)
        }
        return BackupProtocolPayload(
            deviceId = deviceId,
            providerName = DEFAULT_PROVIDER_NAME,
            accounts = accountBackupProtocolElementList
        )
    }

    private suspend fun getAccountBackupProtocolElement(accountAddress: String): BackupProtocolElement? {
        val accountDetail = getLocalAccount(accountAddress) ?: return null
        if (!isAccountEligible(accountDetail)) return null
        val accountType = convertAccountTypeToBackupProtocolAccountType(accountDetail) ?: return null

        return BackupProtocolElement(
            address = accountDetail.algoAddress,
            name = getAccountCustomName(accountAddress) ?: accountAddress.toShortenedAddress(),
            accountType = accountType,
            privateKey = getAlgo25SecretKey(accountAddress)?.encodeBase64().orEmpty(),
            metadata = null
        )
    }

    private fun convertAccountTypeToBackupProtocolAccountType(account: LocalAccount): String? {
        return when (account) {
            is LocalAccount.Algo25 -> SINGLE_ACCOUNT_TYPE_NAME
            is LocalAccount.HdKey -> null // TODO
            is LocalAccount.LedgerBle -> null
            is LocalAccount.NoAuth -> WATCH_ACCOUNT_TYPE_NAME
        }
    }

    companion object {
        private const val DEFAULT_PROVIDER_NAME = "Pera Wallet"
    }
}
