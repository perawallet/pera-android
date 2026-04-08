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

package com.algorand.backup.data.repository

import android.util.Base64
import com.algorand.backup.account.domain.model.AddressBackupPayload
import com.algorand.backup.account.domain.model.SecretsBackupPayload
import com.algorand.backup.domain.usecase.LocalBackupDataImporter
import com.algorand.wallet.account.core.domain.usecase.AddAlgo25Account
import com.algorand.wallet.account.core.domain.usecase.AddHdKeyAccount
import com.algorand.wallet.account.core.domain.usecase.AddHdSeed
import com.algorand.wallet.account.core.domain.usecase.AddJointAccount
import com.algorand.wallet.account.core.domain.usecase.AddLedgerBleAccount
import com.algorand.wallet.account.core.domain.usecase.AddNoAuthAccount
import com.algorand.wallet.account.local.domain.usecase.GetHdSeedId
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccount
import com.algorand.wallet.algosdk.transaction.sdk.AlgoAccountSdk
import com.algorand.wallet.encryption.domain.utils.clearFromMemory
import javax.inject.Inject

internal class DefaultLocalBackupDataImporter @Inject constructor(
    private val getLocalAccount: GetLocalAccount,
    private val addAlgo25Account: AddAlgo25Account,
    private val addHdSeed: AddHdSeed,
    private val addHdKeyAccount: AddHdKeyAccount,
    private val addLedgerBleAccount: AddLedgerBleAccount,
    private val addNoAuthAccount: AddNoAuthAccount,
    private val addJointAccount: AddJointAccount,
    private val getHdSeedId: GetHdSeedId,
    private val algoAccountSdk: AlgoAccountSdk
) : LocalBackupDataImporter {

    override suspend fun importSecrets(
        secretsPayloads: List<SecretsBackupPayload>,
        addressPayloads: List<AddressBackupPayload>
    ) {
        for (payload in secretsPayloads) {
            when (payload) {
                is SecretsBackupPayload.Algo25 -> {
                    val addressPayload = addressPayloads.findAlgo25(payload.address)
                    importAlgo25Account(payload, addressPayload)
                }
                is SecretsBackupPayload.HdSeed -> importHdSeed(payload)
            }
        }
    }

    override suspend fun importAddresses(payloads: List<AddressBackupPayload>) {
        for (payload in payloads) {
            when (payload) {
                is AddressBackupPayload.Algo25 -> Unit
                is AddressBackupPayload.HdSeed -> Unit
                is AddressBackupPayload.HdKey -> importHdKeyAccount(payload)
                is AddressBackupPayload.LedgerBle -> importLedgerBleAccount(payload)
                is AddressBackupPayload.NoAuth -> importNoAuthAccount(payload)
                is AddressBackupPayload.Joint -> importJointAccount(payload)
            }
        }
    }

    private suspend fun importAlgo25Account(
        payload: SecretsBackupPayload.Algo25,
        addressPayload: AddressBackupPayload.Algo25?
    ) {
        if (getLocalAccount(payload.address) != null) return
        val recovered = algoAccountSdk.recoverAlgo25Account(payload.mnemonic) ?: return
        val secretKey = recovered.secretKey.copyOf()
        try {
            addAlgo25Account(
                address = recovered.address,
                secretKey = secretKey,
                isBackedUp = true,
                customName = addressPayload?.customName,
                orderIndex = Int.MAX_VALUE
            )
        } finally {
            secretKey.clearFromMemory()
        }
    }

    private fun List<AddressBackupPayload>.findAlgo25(address: String): AddressBackupPayload.Algo25? {
        return firstOrNull { it is AddressBackupPayload.Algo25 && it.address == address } as? AddressBackupPayload.Algo25
    }

    private suspend fun importHdSeed(payload: SecretsBackupPayload.HdSeed) {
        val entropy = Base64.decode(payload.entropy, Base64.NO_WRAP)
        addHdSeed(entropy)
    }

    private suspend fun importHdKeyAccount(payload: AddressBackupPayload.HdKey) {
        if (getLocalAccount(payload.address) != null) return
        val seedId = getHdSeedId(payload.seedFirstDerivedAddress) ?: return
        // TODO: Add privateKey to AddressBackupPayload.HdKey instead of using a placeholder
        val privateKey = ByteArray(0)
        addHdKeyAccount(
            address = payload.address,
            publicKey = Base64.decode(payload.publicKey, Base64.NO_WRAP),
            privateKey = privateKey,
            seedId = seedId,
            account = payload.account,
            change = payload.change,
            keyIndex = payload.keyIndex,
            derivationType = payload.derivationType,
            isBackedUp = true,
            customName = payload.customName,
            orderIndex = Int.MAX_VALUE
        )
    }

    private suspend fun importLedgerBleAccount(payload: AddressBackupPayload.LedgerBle) {
        if (getLocalAccount(payload.address) != null) return
        addLedgerBleAccount(
            address = payload.address,
            deviceMacAddress = payload.deviceMacAddress,
            indexInLedger = payload.indexInLedger,
            customName = payload.customName,
            bluetoothName = payload.bluetoothName,
            orderIndex = Int.MAX_VALUE
        )
    }

    private suspend fun importNoAuthAccount(payload: AddressBackupPayload.NoAuth) {
        if (getLocalAccount(payload.address) != null) return
        addNoAuthAccount(
            address = payload.address,
            customName = payload.customName,
            orderIndex = Int.MAX_VALUE
        )
    }

    private suspend fun importJointAccount(payload: AddressBackupPayload.Joint) {
        if (getLocalAccount(payload.address) != null) return
        addJointAccount(
            address = payload.address,
            participantAddresses = payload.participantAddresses,
            threshold = payload.threshold,
            version = payload.version,
            customName = payload.customName,
            orderIndex = Int.MAX_VALUE
        )
    }
}
