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

package com.algorand.android.credentials.passkeys.validator.domain.usecase

import android.content.pm.SigningInfo
import androidx.credentials.provider.CallingAppInfo
import com.algorand.android.credentials.passkeys.domain.PeraMessageDigest
import com.algorand.android.credentials.passkeys.validator.domain.repository.AppInfoValidationRepository
import com.algorand.wallet.foundation.PeraResult
import javax.inject.Inject

internal class IsAssetLinksValidUseCase @Inject constructor(
    private val appInfoValidationRepository: AppInfoValidationRepository
) : IsAssetLinksValid {

    override suspend fun invoke(rpId: String, callingAppInfo: CallingAppInfo): PeraResult<Boolean> {
        val websiteUrl = getWebsiteUrl(rpId)
        return appInfoValidationRepository.getAssetLinkCheckResult(
            url = websiteUrl,
            pkgName = callingAppInfo.packageName,
            certId = computeLatestCertification(callingAppInfo.signingInfo).orEmpty()
        ).map { it.isLinked }
    }

    private fun getWebsiteUrl(rpId: String): String {
        val protocol = "https://"
        return if (rpId.startsWith(protocol)) rpId else "${protocol}$rpId"
    }

    private fun computeLatestCertification(callerSigningInfo: SigningInfo): String? {
        if (callerSigningInfo.hasMultipleSigners()) {
            return null
        }
        return computeNormalizedSha256Fingerprint(callerSigningInfo.signingCertificateHistory[0].toByteArray())
    }

    private fun computeNormalizedSha256Fingerprint(signature: ByteArray): String {
        val md = PeraMessageDigest.getInstance()
        return bytesToHexString(md.digest(signature))
    }

    private fun bytesToHexString(bytes: ByteArray): String {
        return bytes.joinToString(":") { "%02X".format(it) }
    }
}
