package com.algorand.android.module.asb.backupprotocol.usecase.implementation

import android.util.Base64
import com.algorand.android.foundation.json.JsonSerializer
import com.algorand.android.module.asb.backupprotocol.mapper.BackUpProtocolContentMapper
import com.algorand.android.module.asb.backupprotocol.model.BackUpPayload
import com.algorand.android.module.asb.backupprotocol.usecase.CreateAsbBackUpFilePayload
import com.algorand.android.module.asb.backupprotocol.usecase.CreateBackUpProtocolPayload
import com.algorand.android.module.asb.domain.usecase.CreateAsbCipherText
import java.nio.charset.StandardCharsets
import javax.inject.Inject

internal class CreateAsbBackUpFilePayloadUseCase @Inject constructor(
    private val createBackUpProtocolPayload: CreateBackUpProtocolPayload,
    private val jsonSerializer: JsonSerializer,
    private val createAsbCipherText: CreateAsbCipherText,
    private val backUpProtocolContentMapper: BackUpProtocolContentMapper
) : CreateAsbBackUpFilePayload {

    override fun invoke(payload: BackUpPayload): String? {
        val backUpProtocolPayload = createBackUpProtocolPayload.invoke(
            nodeDeviceId = payload.nodeDeviceId,
            accounts = payload.accounts
        )
        val serializedPayload = jsonSerializer.toJson(backUpProtocolPayload)
        val cipherText = createAsbCipherText(
            payload = serializedPayload,
            mnemonics = payload.mnemonics
        ) ?: return null

        val backUpProtocolContent = backUpProtocolContentMapper(cipherText)
        val serializedContent = jsonSerializer.toJson(backUpProtocolContent)
        return serializedContent.encodeBase64()
    }

    private fun String.encodeBase64(): String? {
        return try {
            Base64.encodeToString(toByteArray(StandardCharsets.UTF_8), Base64.NO_WRAP)
        } catch (exception: Exception) {
            null
        }
    }
}
