/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.common.account.local.data.mapper.entity

import com.algorand.common.account.local.data.database.model.Bip39Entity
import com.algorand.common.account.local.domain.model.LocalAccount
import com.algorand.common.encryption.AddressEncryptionManager
import com.algorand.common.encryption.SecretKeyEncryptionManager

internal class Bip39EntityMapperImpl(
    private val addressEncryptionManager: AddressEncryptionManager,
    private val secretKeyEncryptionManager: SecretKeyEncryptionManager
) : Bip39EntityMapper {

    override fun invoke(localAccount: LocalAccount.Bip39): Bip39Entity {
        val encryptedAddress = addressEncryptionManager.encrypt(localAccount.address)
        val encryptedSecretKey = secretKeyEncryptionManager.encrypt(localAccount.secretKey)
        return Bip39Entity(
            encryptedAddress = encryptedAddress,
            encryptedSecretKey = encryptedSecretKey
        )
    }
}
