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
import com.algorand.backup.domain.security.BackupEncryptionManager
import com.algorand.wallet.logger.PeraErrorLogger
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject
import kotlin.io.encoding.Base64

internal class DefaultSecretsBackupPayloadMapper @Inject constructor(
    private val gson: Gson,
    private val backupEncryptionManager: BackupEncryptionManager,
    private val errorLogger: PeraErrorLogger
) : SecretsBackupPayloadMapper {

    override fun decrypt(ciphertext: String): List<SecretsBackupPayload>? {
        return try {
            val decryptedText = backupEncryptionManager.decrypt(ciphertext)
            val wrappedItems = gson.fromJson(decryptedText, Array<BackupAccountWrapper>::class.java).toList()
            wrappedItems.mapNotNull { wrapper ->
                mapSecretsBackupPayload(wrapper)
            }
        } catch (exception: Exception) {
            errorLogger.logError(exception)
            null
        }
    }

    override fun encrypt(payloads: List<SecretsBackupPayload>): String {
        val wrappedPayload = payloads.map { payload ->
            when (payload) {
                is SecretsBackupPayload.Algo25 -> wrapAlgo25(payload)
                is SecretsBackupPayload.HdSeed -> wrapHdSeed(payload)
            }
        }
        val json = gson.toJson(wrappedPayload)
        return backupEncryptionManager.encrypt(json)
    }

    private fun mapSecretsBackupPayload(wrapper: BackupAccountWrapper): SecretsBackupPayload? {
        return when (wrapper.type) {
            HD_SEED_TYPE -> mapHdSeed(wrapper.data)
            ALGO_25_TYPE -> mapAlgo25(wrapper.data)
            else -> {
                errorLogger.logError("$logTag: Unsupported backup secret type: ${wrapper.type}")
                null
            }
        }
    }

    private fun wrapHdSeed(hdSeed: SecretsBackupPayload.HdSeed): BackupAccountWrapper {
        val hdSeedData = HdSeedSecretsData(
            firstDerivedAddress = hdSeed.address,
            entropy = hdSeed.entropy,
            seed = hdSeed.seed
        )
        return BackupAccountWrapper(
            type = HD_SEED_TYPE,
            data = Base64.encode(gson.toJson(hdSeedData).toByteArray())
        )
    }

    private fun wrapAlgo25(algo25: SecretsBackupPayload.Algo25): BackupAccountWrapper {
        val algo25Data = Algo25SecretsData(algoAddress = algo25.address, mnemonic = algo25.mnemonic)
        return BackupAccountWrapper(
            type = ALGO_25_TYPE,
            data = Base64.encode(gson.toJson(algo25Data).toByteArray())
        )
    }

    private fun mapHdSeed(data: String): SecretsBackupPayload.HdSeed? {
        return tryMap<HdSeedSecretsData>(data)?.run {
            SecretsBackupPayload.HdSeed(entropy = entropy, seed = seed, address = firstDerivedAddress)
        }
    }

    private fun mapAlgo25(data: String): SecretsBackupPayload.Algo25? {
        return tryMap<Algo25SecretsData>(data)?.run {
            SecretsBackupPayload.Algo25(address = algoAddress, mnemonic = mnemonic)
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

        val logTag: String = DefaultSecretsBackupPayloadMapper::class.java.simpleName
    }
}