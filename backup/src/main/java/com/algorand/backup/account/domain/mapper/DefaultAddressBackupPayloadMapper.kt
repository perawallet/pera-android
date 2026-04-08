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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject
import kotlin.io.encoding.Base64

internal class DefaultAddressBackupPayloadMapper @Inject constructor(
    private val gson: Gson
) : AddressBackupPayloadMapper {

    override fun serialize(payload: AddressBackupPayload): ByteArray {
        val wrapper = when (payload) {
            is AddressBackupPayload.Algo25 -> wrapAlgo25(payload)
            is AddressBackupPayload.HdKey -> wrapHdKey(payload)
            is AddressBackupPayload.HdSeed -> wrapHdSeed(payload)
            is AddressBackupPayload.LedgerBle -> wrapLedgerBle(payload)
            is AddressBackupPayload.NoAuth -> wrapNoAuth(payload)
            is AddressBackupPayload.Joint -> wrapJoint(payload)
        }
        return gson.toJson(wrapper).toByteArray(Charsets.UTF_8)
    }

    override fun deserialize(bytes: ByteArray): AddressBackupPayload? {
        return try {
            val wrapper = gson.fromJson(String(bytes, Charsets.UTF_8), BackupAccountWrapper::class.java)
            mapToPayload(wrapper)
        } catch (e: Exception) {
            null
        }
    }

    private fun mapToPayload(wrapper: BackupAccountWrapper): AddressBackupPayload? {
        return when (wrapper.type) {
            ALGO_25_TYPE -> mapAlgo25(wrapper.data)
            HD_KEY_TYPE -> mapHdKey(wrapper.data)
            HD_SEED_TYPE -> mapHdSeed(wrapper.data)
            LEDGER_BLE_TYPE -> mapLedgerBle(wrapper.data)
            NO_AUTH_TYPE -> mapNoAuth(wrapper.data)
            JOINT_TYPE -> mapJoint(wrapper.data)
            else -> null
        }
    }

    private fun wrapAlgo25(payload: AddressBackupPayload.Algo25) = BackupAccountWrapper(
        type = ALGO_25_TYPE,
        data = encodeData(Algo25AddressData(payload.address, payload.customName))
    )

    private fun wrapHdKey(payload: AddressBackupPayload.HdKey) = BackupAccountWrapper(
        type = HD_KEY_TYPE,
        data = encodeData(
            HdKeyAddressData(
                payload.seedFirstDerivedAddress, payload.address, payload.publicKey,
                payload.account, payload.change, payload.keyIndex,
                payload.derivationType, payload.customName
            )
        )
    )

    private fun wrapHdSeed(payload: AddressBackupPayload.HdSeed) = BackupAccountWrapper(
        type = HD_SEED_TYPE,
        data = encodeData(HdSeedAddressData(payload.address))
    )

    private fun wrapLedgerBle(payload: AddressBackupPayload.LedgerBle) = BackupAccountWrapper(
        type = LEDGER_BLE_TYPE,
        data = encodeData(
            LedgerBleAddressData(
                payload.address, payload.deviceMacAddress, payload.bluetoothName,
                payload.indexInLedger, payload.customName
            )
        )
    )

    private fun wrapNoAuth(payload: AddressBackupPayload.NoAuth) = BackupAccountWrapper(
        type = NO_AUTH_TYPE,
        data = encodeData(NoAuthAddressData(payload.address, payload.customName))
    )

    private fun wrapJoint(payload: AddressBackupPayload.Joint) = BackupAccountWrapper(
        type = JOINT_TYPE,
        data = encodeData(
            JointAddressData(
                payload.address, payload.participantAddresses, payload.threshold,
                payload.version, payload.customName
            )
        )
    )

    private fun mapAlgo25(data: String): AddressBackupPayload.Algo25? {
        return decodeData<Algo25AddressData>(data)?.let {
            AddressBackupPayload.Algo25(it.algoAddress, it.customName)
        }
    }

    private fun mapHdKey(data: String): AddressBackupPayload.HdKey? {
        return decodeData<HdKeyAddressData>(data)?.let {
            AddressBackupPayload.HdKey(
                it.algoAddress, it.seedFirstDerivedAddress, it.publicKey,
                it.account, it.change, it.keyIndex, it.derivationType, it.customName
            )
        }
    }

    private fun mapHdSeed(data: String): AddressBackupPayload.HdSeed? {
        return decodeData<HdSeedAddressData>(data)?.let {
            AddressBackupPayload.HdSeed(it.firstDerivedAddress)
        }
    }

    private fun mapLedgerBle(data: String): AddressBackupPayload.LedgerBle? {
        return decodeData<LedgerBleAddressData>(data)?.let {
            AddressBackupPayload.LedgerBle(
                it.algoAddress, it.deviceMacAddress, it.bluetoothName,
                it.indexInLedger, it.customName
            )
        }
    }

    private fun mapNoAuth(data: String): AddressBackupPayload.NoAuth? {
        return decodeData<NoAuthAddressData>(data)?.let {
            AddressBackupPayload.NoAuth(it.algoAddress, it.customName)
        }
    }

    private fun mapJoint(data: String): AddressBackupPayload.Joint? {
        return decodeData<JointAddressData>(data)?.let {
            AddressBackupPayload.Joint(
                it.algoAddress, it.participantAddresses, it.threshold,
                it.version, it.customName
            )
        }
    }

    private fun <T> encodeData(data: T): String {
        return Base64.encode(gson.toJson(data).toByteArray())
    }

    private inline fun <reified T> decodeData(data: String): T? {
        return try {
            val decoded = String(Base64.decode(data))
            gson.fromJson(decoded, object : TypeToken<T>() {}.type)
        } catch (e: Exception) {
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
    }
}
