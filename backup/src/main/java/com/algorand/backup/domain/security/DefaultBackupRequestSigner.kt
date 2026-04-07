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

package com.algorand.backup.domain.security

import android.util.Base64
import com.algorand.backup.domain.model.BackupId
import com.algorand.backup.domain.model.DeviceId
import com.algorand.backup.domain.model.RegistrationProof
import com.algorand.backup.domain.model.SensitiveBytes
import com.algorand.backup.domain.model.SignedRequest
import com.algorand.backup.domain.repository.BackupAuthRepository
import com.algorand.wallet.foundation.PeraResult
import java.security.MessageDigest
import javax.inject.Inject
import okhttp3.Request
import okio.Buffer
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
import org.bouncycastle.crypto.signers.Ed25519Signer

internal class DefaultBackupRequestSigner @Inject constructor(
    private val nonceGenerator: NonceGenerator,
    private val backupAuthRepository: BackupAuthRepository
) : BackupRequestSigner {

    override fun signHttpRequest(request: Request): PeraResult<SignedRequest> {
        val backupId = backupAuthRepository.getBackupId()
            ?: return PeraResult.Error(IllegalStateException("Backup ID not found"))
        val deviceId = backupAuthRepository.getDeviceId()
            ?: return PeraResult.Error(IllegalStateException("Device ID not found"))

        return backupAuthRepository.usePrivateKey { authPrivateKey ->
            val nonce = nonceGenerator.generate()
            val message = buildMessage(request, nonce)
            val signature = sign(message, authPrivateKey.reveal())
            SignedRequest(backupId, deviceId, nonce, signature = encodeBase64(signature))
        }
    }

    private fun buildMessage(request: Request, nonce: String): ByteArray {
        val method = request.method
        val path = request.url.encodedPath
        val bodyHash = computeBodyHash(request)
        return buildMessageBytes(method, path, bodyHash, nonce)
    }

    private fun computeBodyHash(request: Request): String {
        val bodyBytes = request.body?.let { body ->
            val buffer = Buffer()
            body.writeTo(buffer)
            buffer.readByteArray()
        } ?: ByteArray(0)
        val hash = MessageDigest.getInstance(SHA_256).digest(bodyBytes)
        return hash.joinToString("") { "%02x".format(it) }
    }

    override fun createRegistrationProof(
        authPrivateKey: SensitiveBytes,
        authPublicKey: SensitiveBytes,
        backupId: BackupId,
        deviceId: DeviceId,
        nonce: String
    ): RegistrationProof {
        val message = buildMessageBytes(MESSAGE_PREFIX_REGISTER, backupId.value, deviceId.value, nonce)
        val signature = sign(message, authPrivateKey.reveal())
        return RegistrationProof(
            backupId = backupId,
            deviceId = deviceId,
            publicKey = encodeBase64(authPublicKey.reveal()),
            nonce = nonce,
            signature = encodeBase64(signature)
        )
    }

    override fun createWebSocketToken(backupId: BackupId, deviceId: DeviceId, timestamp: String): PeraResult<String> {
        return backupAuthRepository.usePrivateKey { authPrivateKey ->
            val message = buildMessageBytes(MESSAGE_PREFIX_WS, backupId.value, deviceId.value, timestamp)
            val signature = sign(message, authPrivateKey.reveal())
            encodeBase64(signature)
        }
    }

    private fun sign(message: ByteArray, privateKey: ByteArray): ByteArray {
        val signer = Ed25519Signer()
        signer.init(true, Ed25519PrivateKeyParameters(privateKey, 0))
        signer.update(message, 0, message.size)
        return signer.generateSignature()
    }

    private fun buildMessageBytes(vararg parts: String): ByteArray {
        return parts.joinToString(MESSAGE_SEPARATOR).toByteArray(Charsets.UTF_8)
    }

    private fun encodeBase64(data: ByteArray): String = Base64.encodeToString(data, Base64.NO_WRAP)

    companion object {
        private const val SHA_256 = "SHA-256"
        private const val MESSAGE_SEPARATOR = "|"
        private const val MESSAGE_PREFIX_REGISTER = "REGISTER"
        private const val MESSAGE_PREFIX_WS = "WS"
    }
}
