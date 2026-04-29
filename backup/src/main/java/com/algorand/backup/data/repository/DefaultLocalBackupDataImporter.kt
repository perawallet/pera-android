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
import com.algorand.wallet.account.custom.domain.usecase.SetAccountCustomName
import com.algorand.wallet.account.local.domain.usecase.GetHdEntropy
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetSeedIdByFirstAddress
import com.algorand.wallet.account.local.domain.usecase.IsThereAnySeedWithFirstAddress
import com.algorand.wallet.algosdk.bip39.model.HdKeyAddressIndex
import com.algorand.wallet.algosdk.bip39.sdk.Bip39Wallet
import com.algorand.wallet.algosdk.bip39.sdk.Bip39WalletProvider
import com.algorand.wallet.algosdk.transaction.sdk.AlgoAccountSdk
import com.algorand.wallet.encryption.domain.utils.clearFromMemory
import com.algorand.wallet.logger.PeraErrorLogger
import javax.inject.Inject

internal class DefaultLocalBackupDataImporter @Inject constructor(
    private val getLocalAccount: GetLocalAccount,
    private val addAlgo25Account: AddAlgo25Account,
    private val addHdSeed: AddHdSeed,
    private val addHdKeyAccount: AddHdKeyAccount,
    private val addLedgerBleAccount: AddLedgerBleAccount,
    private val addNoAuthAccount: AddNoAuthAccount,
    private val addJointAccount: AddJointAccount,
    private val getSeedIdByFirstAddress: GetSeedIdByFirstAddress,
    private val isThereAnySeedWithFirstAddress: IsThereAnySeedWithFirstAddress,
    private val getHdEntropy: GetHdEntropy,
    private val bip39WalletProvider: Bip39WalletProvider,
    private val algoAccountSdk: AlgoAccountSdk,
    private val setAccountCustomName: SetAccountCustomName,
    private val errorLogger: PeraErrorLogger
) : LocalBackupDataImporter {

    override suspend fun importSecrets(
        secretsPayloads: List<SecretsBackupPayload>,
        addressPayloads: List<AddressBackupPayload>
    ) {
        for (payload in secretsPayloads) {
            importSecretsPayload(payload, addressPayloads)
        }
    }

    override suspend fun importAddresses(payloads: List<AddressBackupPayload>): Set<String> {
        val imported = mutableSetOf<String>()
        val seedIdCache = mutableMapOf<String, Int?>()
        for (payload in payloads) {
            if (importAddressPayload(payload, seedIdCache)) {
                imported += payload.address
            }
        }
        return imported
    }

    private suspend fun importSecretsPayload(
        payload: SecretsBackupPayload,
        addressPayloads: List<AddressBackupPayload>
    ) {
        when (payload) {
            is SecretsBackupPayload.Algo25 -> importAlgo25Account(payload, addressPayloads.findAlgo25(payload.address))
            is SecretsBackupPayload.HdSeed -> importHdSeed(payload)
        }
    }

    private suspend fun importAddressPayload(
        payload: AddressBackupPayload,
        seedIdCache: MutableMap<String, Int?>
    ): Boolean {
        if (getLocalAccount(payload.address) != null) {
            applyCustomName(payload)
            return true
        }
        return importAddressByType(payload, seedIdCache)
    }

    private suspend fun applyCustomName(payload: AddressBackupPayload) {
        val customName = payload.customName ?: return
        setAccountCustomName(payload.address, customName)
    }

    private suspend fun importAddressByType(
        payload: AddressBackupPayload,
        seedIdCache: MutableMap<String, Int?>
    ): Boolean = when (payload) {
        is AddressBackupPayload.Algo25 -> getLocalAccount(payload.address) != null
        is AddressBackupPayload.HdSeed -> isThereAnySeedWithFirstAddress(payload.address)
        is AddressBackupPayload.HdKey -> importHdKeyAccount(payload, seedIdCache)
        is AddressBackupPayload.LedgerBle -> importLedgerBleAccount(payload).let { true }
        is AddressBackupPayload.NoAuth -> importNoAuthAccount(payload).let { true }
        is AddressBackupPayload.Joint -> importJointAccount(payload).let { true }
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
        try {
            addHdSeed(entropy)
        } finally {
            entropy.clearFromMemory()
        }
    }

    private suspend fun importHdKeyAccount(
        payload: AddressBackupPayload.HdKey,
        seedIdCache: MutableMap<String, Int?>
    ): Boolean {
        val seedId = resolveSeedId(payload.seedFirstDerivedAddress, seedIdCache)
        if (seedId == null) {
            logSeedUnavailable(payload)
            return false
        }
        val privateKey = derivePrivateKey(seedId, payload)
        if (privateKey == null) {
            logPrivateKeyFailure(payload)
            return false
        }
        saveHdKeyAccount(payload, seedId, privateKey)
        return true
    }

    private suspend fun saveHdKeyAccount(
        payload: AddressBackupPayload.HdKey,
        seedId: Int,
        privateKey: ByteArray
    ) {
        try {
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
        } finally {
            privateKey.clearFromMemory()
        }
    }

    private suspend fun resolveSeedId(
        seedFirstDerivedAddress: String,
        cache: MutableMap<String, Int?>
    ): Int? {
        return cache.getOrPut(seedFirstDerivedAddress) {
            getSeedIdByFirstAddress(seedFirstDerivedAddress)
        }
    }

    private suspend fun derivePrivateKey(seedId: Int, payload: AddressBackupPayload.HdKey): ByteArray? {
        val entropy = getHdEntropy(seedId) ?: return null
        var wallet: Bip39Wallet? = null
        return try {
            wallet = bip39WalletProvider.getBip39Wallet(entropy)
            wallet.generateAddress(payload.toAddressIndex()).privateKey
        } catch (e: Exception) {
            errorLogger.logError(e)
            null
        } finally {
            wallet?.invalidate()
            entropy.clearFromMemory()
        }
    }

    private fun AddressBackupPayload.HdKey.toAddressIndex(): HdKeyAddressIndex {
        return HdKeyAddressIndex(accountIndex = account, changeIndex = change, keyIndex = keyIndex)
    }

    private fun logSeedUnavailable(payload: AddressBackupPayload.HdKey) {
        errorLogger.logError(
            IllegalStateException(
                "Cannot import HdKey ${payload.address}: parent seed ${payload.seedFirstDerivedAddress} unavailable"
            )
        )
    }

    private fun logPrivateKeyFailure(payload: AddressBackupPayload.HdKey) {
        errorLogger.logError(
            IllegalStateException("Failed to derive private key for HdKey ${payload.address}")
        )
    }

    private suspend fun importLedgerBleAccount(payload: AddressBackupPayload.LedgerBle) {
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
        addNoAuthAccount(
            address = payload.address,
            customName = payload.customName,
            orderIndex = Int.MAX_VALUE
        )
    }

    private suspend fun importJointAccount(payload: AddressBackupPayload.Joint) {
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
