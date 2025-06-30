package com.algorand.android.credentials.encoding

import android.util.Base64
import androidx.credentials.provider.CallingAppInfo
import java.security.MessageDigest

fun b64Decode(str: String): ByteArray {
    return Base64.decode(str, Base64.NO_PADDING or Base64.NO_WRAP or Base64.URL_SAFE)
}

fun b64Encode(data: ByteArray): String {
    return Base64.encodeToString(data, Base64.NO_PADDING or Base64.NO_WRAP or Base64.URL_SAFE)
}

fun appInfoToOrigin(info: CallingAppInfo): String {
    val cert = info.signingInfo.apkContentsSigners[0].toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    val certHash = md.digest(cert)
    return "android:apk-key-hash:${b64Encode(certHash)}"
}
