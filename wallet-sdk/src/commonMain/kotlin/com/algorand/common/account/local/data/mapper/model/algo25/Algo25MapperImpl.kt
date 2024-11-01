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

package com.algorand.common.account.local.data.mapper.model.algo25

import com.algorand.common.account.local.data.database.model.Algo25Entity
import com.algorand.common.account.local.domain.model.LocalAccount
import com.algorand.common.encryption.*

internal class Algo25MapperImpl(
    private val addressEncryptionManager: AddressEncryptionManager,
    private val secretKeyEncryptionManager: SecretKeyEncryptionManager
) : Algo25Mapper {

    override fun invoke(entity: Algo25Entity): LocalAccount.Algo25 {
        val decryptedAddress = addressEncryptionManager.decrypt(entity.encryptedAddress)
        val decryptedSecretKey = secretKeyEncryptionManager.decrypt(entity.encryptedSecretKey)
        return LocalAccount.Algo25(
            address = decryptedAddress,
            secretKey = decryptedSecretKey
        )
    }
}
