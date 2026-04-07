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

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.KeyProtection
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.inject.Inject

internal class DefaultPeraAndroidKeyStore @Inject constructor() : PeraAndroidKeyStore {

    private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

    override fun getSecretKey(alias: String): SecretKey? {
        return keyStore.getKey(alias, null) as? SecretKey
    }

    override fun setSecretKeyEntry(alias: String, secretKey: SecretKey, protection: KeyProtection) {
        keyStore.setEntry(alias, KeyStore.SecretKeyEntry(secretKey), protection)
    }

    override fun generateSecretKey(spec: KeyGenParameterSpec) {
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        keyGenerator.init(spec)
        keyGenerator.generateKey()
    }

    override fun deleteEntry(alias: String) {
        if (keyStore.containsAlias(alias)) {
            keyStore.deleteEntry(alias)
        }
    }

    override fun containsAlias(alias: String): Boolean {
        return keyStore.containsAlias(alias)
    }

    private companion object {
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
    }
}
