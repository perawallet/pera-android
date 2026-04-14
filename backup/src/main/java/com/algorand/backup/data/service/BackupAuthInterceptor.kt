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

import com.algorand.backup.domain.model.SignedRequest
import com.algorand.backup.domain.security.BackupRequestSigner
import com.algorand.wallet.foundation.PeraResult
import javax.inject.Inject
import javax.inject.Singleton
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

@Singleton
internal class BackupAuthInterceptor @Inject constructor(private val requestSigner: BackupRequestSigner) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val signedRequest = when (val result = requestSigner.signHttpRequest(originalRequest)) {
            is PeraResult.Success -> result.data
            is PeraResult.Error -> return chain.proceed(originalRequest)
        }

        val authenticatedRequest = createAuthenticatedRequest(originalRequest, signedRequest)
        return chain.proceed(authenticatedRequest)
    }

    private fun createAuthenticatedRequest(originalRequest: Request, signedRequest: SignedRequest): Request {
        val builder = originalRequest.newBuilder()
            .addHeader(HEADER_BACKUP_ID, signedRequest.backupId.value)
            .addHeader(HEADER_DEVICE_ID, signedRequest.deviceId.value)
            .addHeader(HEADER_NONCE, signedRequest.nonce)
            .addHeader(HEADER_SIGNATURE, signedRequest.signature)

        if (originalRequest.body != null) {
            builder.header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
        }

        return builder.build()
    }

    companion object {
        private const val HEADER_BACKUP_ID = "X-Backup-Id"
        private const val HEADER_DEVICE_ID = "X-Device-Id"
        private const val HEADER_NONCE = "X-Nonce"
        private const val HEADER_SIGNATURE = "X-Signature"
        private const val HEADER_CONTENT_TYPE = "Content-Type"
        private const val CONTENT_TYPE_JSON = "application/json"
    }
}
