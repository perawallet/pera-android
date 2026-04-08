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
import com.algorand.backup.domain.usecase.LocalBackupDataProvider
import com.algorand.wallet.account.local.domain.model.HdSeedFirstAddress
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetAllHdSeedFirstAddresses
import com.algorand.wallet.account.local.domain.usecase.GetAllHdSeeds
import com.algorand.wallet.account.custom.domain.usecase.GetAccountCustomName
import com.algorand.wallet.account.local.domain.usecase.GetAccountMnemonic
import com.algorand.wallet.account.local.domain.usecase.GetHdEntropy
import com.algorand.wallet.account.local.domain.usecase.GetHdSeed
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccounts
import com.algorand.wallet.foundation.PeraResult
import javax.inject.Inject

internal class DefaultLocalBackupDataProvider @Inject constructor(
    private val getLocalAccounts: GetLocalAccounts,
    private val getAllHdSeeds: GetAllHdSeeds,
    private val getAllHdSeedFirstAddresses: GetAllHdSeedFirstAddresses,
    private val getHdEntropy: GetHdEntropy,
    private val getHdSeed: GetHdSeed,
    private val getAccountMnemonic: GetAccountMnemonic,
    private val getAccountCustomName: GetAccountCustomName
) : LocalBackupDataProvider {

    override suspend fun getAddressPayloads(): List<AddressBackupPayload> {
        val accounts = getLocalAccounts()
        val seedFirstAddresses = getAllHdSeedFirstAddresses().associateBy { it.seedId }
        return accounts.map { account ->
            val customName = getAccountCustomName(account.algoAddress)
            account.toAddressPayload(seedFirstAddresses, customName)
        }
    }

    override suspend fun getSecretsPayloads(): List<SecretsBackupPayload> {
        val hdSeedPayloads = getHdSeedPayloads()
        val algo25Payloads = getAlgo25Payloads()
        return hdSeedPayloads + algo25Payloads
    }

    private fun LocalAccount.toAddressPayload(
        seedFirstAddresses: Map<Int, HdSeedFirstAddress>,
        customName: String?
    ): AddressBackupPayload {
        return when (this) {
            is LocalAccount.Algo25 -> toAlgo25AddressPayload(customName)
            is LocalAccount.HdKey -> toHdKeyAddressPayload(seedFirstAddresses, customName)
            is LocalAccount.LedgerBle -> toLedgerBleAddressPayload(customName)
            is LocalAccount.NoAuth -> toNoAuthAddressPayload(customName)
            is LocalAccount.Joint -> toJointAddressPayload(customName)
        }
    }

    private fun LocalAccount.Algo25.toAlgo25AddressPayload(customName: String?) = AddressBackupPayload.Algo25(
        address = algoAddress,
        customName = customName
    )

    private fun LocalAccount.HdKey.toHdKeyAddressPayload(
        seedFirstAddresses: Map<Int, HdSeedFirstAddress>,
        customName: String?
    ): AddressBackupPayload.HdKey {
        val seedFirstAddress = seedFirstAddresses[seedId]?.firstAddress.orEmpty()
        return AddressBackupPayload.HdKey(
            address = algoAddress,
            seedFirstDerivedAddress = seedFirstAddress,
            publicKey = Base64.encodeToString(publicKey, Base64.NO_WRAP),
            account = account,
            change = change,
            keyIndex = keyIndex,
            derivationType = derivationType,
            customName = customName
        )
    }

    private fun LocalAccount.LedgerBle.toLedgerBleAddressPayload(customName: String?) = AddressBackupPayload.LedgerBle(
        address = algoAddress,
        deviceMacAddress = deviceMacAddress,
        bluetoothName = bluetoothName,
        indexInLedger = indexInLedger,
        customName = customName
    )

    private fun LocalAccount.NoAuth.toNoAuthAddressPayload(customName: String?) = AddressBackupPayload.NoAuth(
        address = algoAddress,
        customName = customName
    )

    private fun LocalAccount.Joint.toJointAddressPayload(customName: String?) = AddressBackupPayload.Joint(
        address = algoAddress,
        participantAddresses = participantAddresses,
        threshold = threshold,
        version = version,
        customName = customName
    )

    private suspend fun getHdSeedPayloads(): List<SecretsBackupPayload.HdSeed> {
        val seeds = getAllHdSeeds()
        val seedFirstAddresses = getAllHdSeedFirstAddresses().associateBy { it.seedId }
        return seeds.mapNotNull { seed ->
            val entropy = getHdEntropy(seed.seedId) ?: return@mapNotNull null
            val seedBytes = getHdSeed(seed.seedId) ?: return@mapNotNull null
            val firstAddress = seedFirstAddresses[seed.seedId]?.firstAddress ?: return@mapNotNull null
            SecretsBackupPayload.HdSeed(
                address = firstAddress,
                seed = Base64.encodeToString(seedBytes, Base64.NO_WRAP),
                entropy = Base64.encodeToString(entropy, Base64.NO_WRAP)
            )
        }
    }

    private suspend fun getAlgo25Payloads(): List<SecretsBackupPayload.Algo25> {
        val accounts = getLocalAccounts()
        return accounts.filterIsInstance<LocalAccount.Algo25>().mapNotNull { account ->
            val mnemonicResult = getAccountMnemonic(account.algoAddress)
            if (mnemonicResult is PeraResult.Success) {
                SecretsBackupPayload.Algo25(
                    address = account.algoAddress,
                    mnemonic = mnemonicResult.data.words.joinToString(" ")
                )
            } else null
        }
    }
}
