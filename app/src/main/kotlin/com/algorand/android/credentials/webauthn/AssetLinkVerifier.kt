package com.algorand.android.credentials.webauthn

import android.content.pm.SigningInfo
import android.util.Log
import org.json.JSONObject
import java.net.URL
import java.security.MessageDigest

class AssetLinkVerifier(private val websiteUrl: String) {
    fun verify(callingPackage: String, callerSigningInfo: SigningInfo): Boolean {
        val assetLinkCheckJsonResponse = callDigitalAssetLinkApi(
            websiteUrl,
            callingPackage,
            computeLatestCertification(callerSigningInfo)!!,
        )
        Log.i("AssetLinkVerifier", "Response: $assetLinkCheckJsonResponse")
        return JSONObject(assetLinkCheckJsonResponse).getBoolean("linked")
    }

    private fun computeLatestCertification(callerSigningInfo: SigningInfo): String? {
        if (callerSigningInfo.hasMultipleSigners()) {
            return null
        }
        return computeNormalizedSha256Fingerprint(
            callerSigningInfo.signingCertificateHistory[0].toByteArray(),
        )
    }

    private fun callDigitalAssetLinkApi(
        websiteUrl: String,
        callingPackage: String,
        callingCert: String,
    ): String {
        val apiEndpoint = "https://digitalassetlinks.googleapis.com/v1/assetlinks:check" +
                "?source.web.site=$websiteUrl+" +
                "&target.android_app.package_name=$callingPackage" +
                "&target.android_app.certificate.sha256_fingerprint=$callingCert" +
                "&relation=delegate_permission/common.handle_all_urls"
        return URL(apiEndpoint).readText()
    }

    private fun computeNormalizedSha256Fingerprint(signature: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return bytesToHexString(digest.digest(signature))
    }

    private fun bytesToHexString(bytes: ByteArray): String {
        return bytes.joinToString(":") { "%02X".format(it) }
    }
}
