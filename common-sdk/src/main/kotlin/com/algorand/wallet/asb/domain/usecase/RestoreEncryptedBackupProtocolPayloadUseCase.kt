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

package com.algorand.wallet.asb.domain.usecase

import com.algorand.wallet.asb.algosdk.AlgorandSdkEncryptionUtils
import com.algorand.wallet.asb.domain.mapper.AsbBackupDataMapper
import com.algorand.wallet.asb.domain.model.AsbBackupData
import com.algorand.wallet.asb.domain.model.BackupProtocolPayload
import com.algorand.wallet.foundation.json.JsonSerializer
import javax.inject.Inject

internal class RestoreEncryptedBackupProtocolPayloadUseCase @Inject constructor(
    private val jsonSerializer: JsonSerializer,
    private val algorandSdkEncryptionUtils: AlgorandSdkEncryptionUtils,
    private val asbBackupDataMapper: AsbBackupDataMapper
) : RestoreEncryptedBackupProtocolPayload {

    override operator fun invoke(cipherText: String, cipherKey: ByteArray): AsbBackupData? {
        val decryptedContent = algorandSdkEncryptionUtils.decryptContent(cipherText, cipherKey) ?: return null
        val decryptedBackupProtocolPayload = getBackupProtocolPayload(decryptedContent) ?: return null
        return asbBackupDataMapper(decryptedBackupProtocolPayload)
    }

    private fun getBackupProtocolPayload(decryptedContent: String): BackupProtocolPayload? {
        return jsonSerializer.fromJson(
            json = decryptedContent,
            type = BackupProtocolPayload::class.java
        )
    }
}
