@file:Suppress("MagicNumber")
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

package com.algorand.android.credentials.passkeys.foundation

import android.util.Base64
import java.math.BigInteger
import java.security.interfaces.ECPublicKey
import javax.inject.Inject

internal class DefaultCoseMapper @Inject constructor() : CoseMapper {

    override fun mapPublicKeyToCose(key: ECPublicKey): MutableMap<Int, Any> {
        val x = bigIntToFixedArray(key.w.affineX)
        val y = bigIntToFixedArray(key.w.affineY)
        val coseKey = mutableMapOf<Int, Any>()
        coseKey[1] = 2 // EC Key type
        coseKey[3] = -7 // ES256
        coseKey[-1] = 1 // P-265 Curve
        coseKey[-2] = x // x
        coseKey[-3] = y // y
        return coseKey
    }

    private fun bigIntToFixedArray(n: BigInteger): ByteArray {
        assert(n.signum() >= 0)
        val bytes = n.toByteArray()
        var offset = 0
        if (bytes[0] == 0x00.toByte()) {
            offset++
        }
        val bytesLen = bytes.size - offset
        assert(bytesLen <= 32)

        val output = ByteArray(32)
        System.arraycopy(bytes, offset, output, 32 - bytesLen, bytesLen)
        return output
    }

    override fun mapCoseKeyToSpki(coseKey: MutableMap<Int, Any>): ByteArray? {
        try {
            val spkiPrefix: ByteArray = Base64.decode("MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE", 0)
            val x = coseKey[-2] as ByteArray
            val y = coseKey[-3] as ByteArray
            return spkiPrefix + x + y
        } catch (_: Exception) {
            // Log exceptions
        }
        return null
    }
}
