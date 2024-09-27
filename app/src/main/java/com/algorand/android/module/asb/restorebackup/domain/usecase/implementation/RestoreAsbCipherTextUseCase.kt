package com.algorand.android.module.asb.restorebackup.domain.usecase.implementation

import android.util.Base64
import com.algorand.android.module.foundation.json.JsonSerializer
import com.algorand.android.module.asb.backupprotocol.model.BackupProtocolContent
import com.algorand.android.module.asb.restorebackup.domain.model.RestoreCipherTextResult
import com.algorand.android.module.asb.restorebackup.domain.usecase.RestoreAsbCipherText
import com.algorand.android.module.asb.restorebackup.validation.AsbBackUpProtocolContentValidator
import javax.inject.Inject

internal class RestoreAsbCipherTextUseCase @Inject constructor(
    private val jsonSerializer: JsonSerializer,
    private val asbBackUpProtocolContentValidator: AsbBackUpProtocolContentValidator
) : RestoreAsbCipherText {

    override fun invoke(encryptedPayload: String): RestoreCipherTextResult {
        val decodedBackupProtocolContent = encryptedPayload.decodeBase64ToString().orEmpty()
        val backupProtocolContent = jsonSerializer.fromJson(
            json = decodedBackupProtocolContent,
            type = BackupProtocolContent::class.java
        )
        return asbBackUpProtocolContentValidator.validate(backupProtocolContent)
    }

    private fun String.decodeBase64ToString(): String? {
        return try {
            val stringInByteArray = Base64.decode(this, Base64.NO_WRAP)
            String(stringInByteArray, Charsets.UTF_8)
        } catch (exception: Exception) {
            null
        }
    }
}
