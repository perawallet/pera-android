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

package com.algorand.backup.data.service

import android.util.Log
import com.algorand.backup.domain.model.SignedRequest
import com.algorand.backup.domain.repository.BackupAuthRepository
import com.algorand.backup.domain.security.BackupRequestSigner
import com.algorand.wallet.foundation.PeraResult
import javax.inject.Inject
import javax.inject.Singleton
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

@Singleton
internal class BackupAuthInterceptor @Inject constructor(
    private val backupAuthRepository: BackupAuthRepository,
    private val requestSigner: BackupRequestSigner
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        if (!backupAuthRepository.hasCredentials()) {
            return chain.proceed(originalRequest)
        }

        val backupId = backupAuthRepository.getBackupId() ?: return chain.proceed(originalRequest)
        val deviceId = backupAuthRepository.getDeviceId() ?: return chain.proceed(originalRequest)

        val signedRequestResult = backupAuthRepository.usePrivateKey { authPrivateKey ->
            requestSigner.signHttpRequest(originalRequest, authPrivateKey, backupId, deviceId)
        }

        val signedRequest = when (signedRequestResult) {
            is PeraResult.Success -> signedRequestResult.data
            is PeraResult.Error -> {
                Log.e(LOG_TAG, "Failed to sign request: ${signedRequestResult.exception.message}")
                return chain.proceed(originalRequest)
            }
        }

        val authenticatedRequest = createAuthenticatedRequest(originalRequest, signedRequest)
        return chain.proceed(authenticatedRequest)
    }

    private fun createAuthenticatedRequest(originalRequest: Request, signedRequest: SignedRequest): Request {
        return originalRequest.newBuilder()
            .addHeader(HEADER_BACKUP_ID, signedRequest.backupId.value)
            .addHeader(HEADER_DEVICE_ID, signedRequest.deviceId.value)
            .addHeader(HEADER_NONCE, signedRequest.nonce)
            .addHeader(HEADER_SIGNATURE, signedRequest.signature)
            .build()
    }

    companion object {
        private const val LOG_TAG = "BackupAuthInterceptor"
        private const val HEADER_BACKUP_ID = "X-Backup-Id"
        private const val HEADER_DEVICE_ID = "X-Device-Id"
        private const val HEADER_NONCE = "X-Nonce"
        private const val HEADER_SIGNATURE = "X-Signature"
    }
}
