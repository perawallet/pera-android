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

package com.algorand.backup.account.domain.mapper

import com.algorand.backup.account.domain.model.AddressBackupPayload
import com.algorand.backup.account.domain.model.Algo25AddressData
import com.algorand.backup.account.domain.model.BackupAccountWrapper
import com.algorand.backup.account.domain.model.HdKeyAddressData
import com.algorand.backup.account.domain.model.HdSeedAddressData
import com.algorand.backup.account.domain.model.JointAddressData
import com.algorand.backup.account.domain.model.LedgerBleAddressData
import com.algorand.backup.account.domain.model.NoAuthAddressData
import com.algorand.backup.domain.security.BackupEncryptionManager
import com.algorand.wallet.logger.PeraErrorLogger
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject
import kotlin.io.encoding.Base64

internal class DefaultAddressBackupPayloadMapper @Inject constructor(
    private val gson: Gson,
    private val backupEncryptionManager: BackupEncryptionManager,
    private val errorLogger: PeraErrorLogger
) : AddressBackupPayloadMapper {

    override fun decrypt(ciphertext: String): List<AddressBackupPayload>? {
        return try {
            val decryptedText = backupEncryptionManager.decrypt(ciphertext)
            val wrappedItems = gson.fromJson(decryptedText, Array<BackupAccountWrapper>::class.java).toList()
            wrappedItems.mapNotNull { wrapper ->
                mapAccountBackupPayload(wrapper)
            }
        } catch (exception: Exception) {
            errorLogger.logError(exception)
            null
        }
    }

    override fun encrypt(payloads: List<AddressBackupPayload>): String {
        val wrappedPayload = payloads.map { payload ->
            when (payload) {
                is AddressBackupPayload.Algo25 -> wrapAlgo25(payload)
                is AddressBackupPayload.HdKey -> wrapHdKey(payload)
                is AddressBackupPayload.HdSeed -> wrapHdSeed(payload)
                is AddressBackupPayload.Joint -> wrapJoint(payload)
                is AddressBackupPayload.LedgerBle -> wrapLedgerBle(payload)
                is AddressBackupPayload.NoAuth -> wrapNoAuth(payload)
            }
        }
        val json = gson.toJson(wrappedPayload)
        return backupEncryptionManager.encrypt(json)
    }

    private fun mapAccountBackupPayload(wrapper: BackupAccountWrapper): AddressBackupPayload? {
        return when (wrapper.type) {
            HD_SEED_TYPE -> mapHdSeed(wrapper.data)
            ALGO_25_TYPE -> mapAlgo25(wrapper.data)
            NO_AUTH_TYPE -> mapNoAuth(wrapper.data)
            JOINT_TYPE -> mapJoint(wrapper.data)
            LEDGER_BLE_TYPE -> mapLedgerBle(wrapper.data)
            HD_KEY_TYPE -> mapHdKey(wrapper.data)
            else -> {
                errorLogger.logError("$logTag Unknown address backup payload type: ${wrapper.type}")
                null
            }
        }
    }

    private fun wrapHdSeed(hdSeed: AddressBackupPayload.HdSeed): BackupAccountWrapper {
        val hdSeedAddressData = HdSeedAddressData(firstDerivedAddress = hdSeed.address)
        return BackupAccountWrapper(
            type = HD_SEED_TYPE,
            data = Base64.encode(gson.toJson(hdSeedAddressData).toByteArray())
        )
    }

    private fun wrapAlgo25(algo25: AddressBackupPayload.Algo25): BackupAccountWrapper {
        val algo25Data = Algo25AddressData(algoAddress = algo25.address, customName = algo25.customName)
        return BackupAccountWrapper(
            type = ALGO_25_TYPE,
            data = Base64.encode(gson.toJson(algo25Data).toByteArray())
        )
    }

    private fun wrapNoAuth(noAuth: AddressBackupPayload.NoAuth): BackupAccountWrapper {
        val noAuthAddressData = NoAuthAddressData(algoAddress = noAuth.address, customName = noAuth.customName)
        return BackupAccountWrapper(
            type = NO_AUTH_TYPE,
            data = Base64.encode(gson.toJson(noAuthAddressData).toByteArray())
        )
    }

    private fun wrapJoint(joint: AddressBackupPayload.Joint): BackupAccountWrapper {
        val jointAddressData = JointAddressData(
            algoAddress = joint.address,
            participantAddresses = joint.participantAddresses,
            threshold = joint.threshold,
            version = joint.version,
            customName = joint.customName
        )
        return BackupAccountWrapper(
            type = JOINT_TYPE,
            data = Base64.encode(gson.toJson(jointAddressData).toByteArray())
        )
    }

    private fun wrapLedgerBle(ledgerBle: AddressBackupPayload.LedgerBle): BackupAccountWrapper {
        val ledgerBleAddressData = LedgerBleAddressData(
            algoAddress = ledgerBle.address,
            deviceMacAddress = ledgerBle.deviceMacAddress,
            bluetoothName = ledgerBle.bluetoothName,
            indexInLedger = ledgerBle.indexInLedger,
            customName = ledgerBle.customName
        )
        return BackupAccountWrapper(
            type = LEDGER_BLE_TYPE,
            data = Base64.encode(gson.toJson(ledgerBleAddressData).toByteArray())
        )
    }

    private fun wrapHdKey(hdKey: AddressBackupPayload.HdKey): BackupAccountWrapper {
        val hdKeyAddressData = HdKeyAddressData(
            seedFirstDerivedAddress = hdKey.seedFirstDerivedAddress,
            algoAddress = hdKey.address,
            publicKey = hdKey.publicKey,
            account = hdKey.account,
            change = hdKey.change,
            keyIndex = hdKey.keyIndex,
            derivationType = hdKey.derivationType,
            customName = hdKey.customName
        )
        return BackupAccountWrapper(
            type = HD_KEY_TYPE,
            data = Base64.encode(gson.toJson(hdKeyAddressData).toByteArray())
        )
    }

    private fun mapHdKey(data: String): AddressBackupPayload.HdKey? {
        return tryMap<HdKeyAddressData>(data)?.run {
            AddressBackupPayload.HdKey(
                seedFirstDerivedAddress = seedFirstDerivedAddress,
                address = algoAddress,
                publicKey = publicKey,
                account = account,
                change = change,
                keyIndex = keyIndex,
                derivationType = derivationType,
                customName = customName
            )
        }
    }

    private fun mapLedgerBle(data: String): AddressBackupPayload.LedgerBle? {
        return tryMap<LedgerBleAddressData>(data)?.run {
            AddressBackupPayload.LedgerBle(
                address = algoAddress,
                deviceMacAddress = deviceMacAddress,
                bluetoothName = bluetoothName,
                indexInLedger = indexInLedger,
                customName = customName
            )
        }
    }

    private fun mapNoAuth(data: String): AddressBackupPayload.NoAuth? {
        return tryMap<NoAuthAddressData>(data)?.run {
            AddressBackupPayload.NoAuth(address = algoAddress, customName = customName)
        }
    }

    private fun mapJoint(data: String): AddressBackupPayload.Joint? {
        return tryMap<JointAddressData>(data)?.run {
            AddressBackupPayload.Joint(
                address = algoAddress,
                participantAddresses = participantAddresses,
                threshold = threshold,
                version = version,
                customName = customName
            )
        }
    }

    private fun mapHdSeed(data: String): AddressBackupPayload.HdSeed? {
        return tryMap<HdSeedAddressData>(data)?.run {
            AddressBackupPayload.HdSeed(address = firstDerivedAddress)
        }
    }

    private fun mapAlgo25(data: String): AddressBackupPayload.Algo25? {
        return tryMap<Algo25AddressData>(data)?.run {
            AddressBackupPayload.Algo25(address = algoAddress, customName = customName)
        }
    }

    private inline fun <reified T> tryMap(data: String): T? {
        return try {
            val base64decoded = String(Base64.decode(data))
            val type = object : TypeToken<T>() {}.type
            gson.fromJson<T>(base64decoded, type)
        } catch (exception: Exception) {
            errorLogger.logError(exception)
            null
        }
    }

    private companion object {
        const val HD_SEED_TYPE = "hd_seed"
        const val ALGO_25_TYPE = "algo_25"
        const val NO_AUTH_TYPE = "no_auth"
        const val JOINT_TYPE = "joint"
        const val LEDGER_BLE_TYPE = "ledger_ble"
        const val HD_KEY_TYPE = "hd_key"

        val logTag: String = DefaultAddressBackupPayloadMapper::class.java.simpleName
    }
}