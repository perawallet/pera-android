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
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class BackupAuthInterceptor @Inject constructor() : Interceptor {

    var signedRequest: SignedRequest? = null

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val currentSignedRequest = signedRequest ?: return chain.proceed(originalRequest)

        val authenticatedRequest = originalRequest.newBuilder()
            .addHeader(HEADER_BACKUP_ID, currentSignedRequest.backupId.value)
            .addHeader(HEADER_DEVICE_ID, currentSignedRequest.deviceId.value)
            .addHeader(HEADER_NONCE, currentSignedRequest.nonce)
            .addHeader(HEADER_SIGNATURE, currentSignedRequest.signature)
            .build()

        return chain.proceed(authenticatedRequest)
    }

    companion object {
        private const val HEADER_BACKUP_ID = "X-Backup-Id"
        private const val HEADER_DEVICE_ID = "X-Device-Id"
        private const val HEADER_NONCE = "X-Nonce"
        private const val HEADER_SIGNATURE = "X-Signature"
    }
}
