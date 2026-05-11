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

import com.algorand.backup.account.domain.model.Algo25SecretsData
import com.algorand.backup.account.domain.model.BackupAccountWrapper
import com.algorand.backup.account.domain.model.HdSeedSecretsData
import com.algorand.backup.account.domain.model.SecretsBackupPayload
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject
import kotlin.io.encoding.Base64

internal class DefaultSecretsBackupPayloadMapper @Inject constructor(
    private val gson: Gson
) : SecretsBackupPayloadMapper {

    override fun serialize(payload: SecretsBackupPayload): ByteArray {
        val wrapper = when (payload) {
            is SecretsBackupPayload.HdSeed -> wrapHdSeed(payload)
            is SecretsBackupPayload.Algo25 -> wrapAlgo25(payload)
        }
        return gson.toJson(wrapper).toByteArray(Charsets.UTF_8)
    }

    override fun deserialize(bytes: ByteArray): SecretsBackupPayload? {
        return try {
            val wrapper = gson.fromJson(String(bytes, Charsets.UTF_8), BackupAccountWrapper::class.java)
            mapToPayload(wrapper)
        } catch (e: Exception) {
            null
        }
    }

    private fun mapToPayload(wrapper: BackupAccountWrapper): SecretsBackupPayload? {
        return when (wrapper.type) {
            HD_SEED_TYPE -> mapHdSeed(wrapper.data)
            ALGO_25_TYPE -> mapAlgo25(wrapper.data)
            else -> null
        }
    }

    private fun wrapHdSeed(payload: SecretsBackupPayload.HdSeed) = BackupAccountWrapper(
        type = HD_SEED_TYPE,
        data = encodeData(HdSeedSecretsData(payload.address, payload.entropy, payload.seed))
    )

    private fun wrapAlgo25(payload: SecretsBackupPayload.Algo25) = BackupAccountWrapper(
        type = ALGO_25_TYPE,
        data = encodeData(Algo25SecretsData(payload.address, payload.mnemonic))
    )

    private fun mapHdSeed(data: String): SecretsBackupPayload.HdSeed? {
        return decodeData<HdSeedSecretsData>(data)?.let {
            SecretsBackupPayload.HdSeed(it.firstDerivedAddress, it.seed, it.entropy)
        }
    }

    private fun mapAlgo25(data: String): SecretsBackupPayload.Algo25? {
        return decodeData<Algo25SecretsData>(data)?.let {
            SecretsBackupPayload.Algo25(it.algoAddress, it.mnemonic)
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
    }
}
