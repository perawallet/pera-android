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

package com.algorand.wallet.asb.domain.mapper

import com.algorand.wallet.asb.domain.model.AsbBackupAccount
import com.algorand.wallet.asb.domain.model.AsbBackupData
import com.algorand.wallet.asb.domain.model.BackupProtocolElement
import com.algorand.wallet.asb.domain.model.BackupProtocolPayload
import com.algorand.wallet.asb.domain.utils.BackupProtocolConstants.ALGO_25_ACCOUNT_TYPE_NAME
import com.algorand.wallet.asb.domain.utils.BackupProtocolConstants.NO_AUTH_ACCOUNT_TYPE_NAME
import javax.inject.Inject

internal class AsbBackupDataMapperImpl @Inject constructor() : AsbBackupDataMapper {

    override fun invoke(payload: BackupProtocolPayload): AsbBackupData {
        return AsbBackupData(
            deviceId = payload.deviceId,
            providerName = payload.providerName,
            accounts = payload.accounts?.mapNotNull { mapAccounts(it) }
        )
    }

    private fun mapAccounts(backupProtocolElement: BackupProtocolElement): AsbBackupAccount? {
        val accountType = when (backupProtocolElement.accountType) {
            ALGO_25_ACCOUNT_TYPE_NAME -> getSafeSingleAccountType(backupProtocolElement)
            NO_AUTH_ACCOUNT_TYPE_NAME -> AsbBackupAccount.AccountType.Watch
            else -> null
        }
        if (accountType == null) return null
        return AsbBackupAccount(
            address = backupProtocolElement.address,
            name = backupProtocolElement.name,
            metadata = backupProtocolElement.metadata,
            accountType = accountType
        )
    }

    private fun getSafeSingleAccountType(backupProtocolElement: BackupProtocolElement): AsbBackupAccount.AccountType {
        return if (backupProtocolElement.privateKey.isNullOrBlank()) {
            AsbBackupAccount.AccountType.Watch
        } else {
            AsbBackupAccount.AccountType.Algo25(backupProtocolElement.privateKey)
        }
    }
}
