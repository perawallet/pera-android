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

package com.algorand.android.ui.backup.sync.security

import android.util.Base64
import com.algorand.backup.domain.model.Argon2idConfig
import javax.inject.Inject
import org.json.JSONObject

internal class BackupSyncQrPayloadJsonSerializer @Inject constructor() : BackupSyncQrPayloadSerializer {

    override fun serialize(payload: BackupSyncPayload): String {
        val argon = JSONObject()
            .put(KEY_ARGON_TIME_COST, payload.argon2idConfig.timeCost)
            .put(KEY_ARGON_MEMORY_COST, payload.argon2idConfig.memoryCost)
            .put(KEY_ARGON_PARALLELISM, payload.argon2idConfig.parallelism)
            .put(KEY_ARGON_OUTPUT_LENGTH, payload.argon2idConfig.outputLength)
        return JSONObject()
            .put(KEY_MNEMONIC, payload.mnemonic)
            .put(KEY_SALT, Base64.encodeToString(payload.salt, Base64.NO_WRAP))
            .put(KEY_ARGON_CONFIG, argon)
            .toString()
    }

    override fun deserialize(serialized: String): BackupSyncPayload {
        val root = JSONObject(serialized)
        val argon = root.getJSONObject(KEY_ARGON_CONFIG)
        return BackupSyncPayload(
            mnemonic = root.getString(KEY_MNEMONIC),
            salt = Base64.decode(root.getString(KEY_SALT), Base64.NO_WRAP),
            argon2idConfig = Argon2idConfig(
                timeCost = argon.getInt(KEY_ARGON_TIME_COST),
                memoryCost = argon.getInt(KEY_ARGON_MEMORY_COST),
                parallelism = argon.getInt(KEY_ARGON_PARALLELISM),
                outputLength = argon.getInt(KEY_ARGON_OUTPUT_LENGTH)
            )
        )
    }

    private companion object {
        const val KEY_MNEMONIC = "m"
        const val KEY_SALT = "s"
        const val KEY_ARGON_CONFIG = "a"
        const val KEY_ARGON_TIME_COST = "t"
        const val KEY_ARGON_MEMORY_COST = "m"
        const val KEY_ARGON_PARALLELISM = "p"
        const val KEY_ARGON_OUTPUT_LENGTH = "o"
    }
}
