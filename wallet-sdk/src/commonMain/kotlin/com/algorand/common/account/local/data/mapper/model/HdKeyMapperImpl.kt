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

package com.algorand.common.account.local.data.mapper.model

import com.algorand.common.account.local.data.database.model.HdKeyEntity
import com.algorand.common.account.local.domain.model.LocalAccount
import com.algorand.common.encryption.HdPrivateKeyEncryptionManager

internal class HdKeyMapperImpl(
    private val privateKeyEncryptionManager: HdPrivateKeyEncryptionManager
) : HdKeyMapper {
    override suspend fun invoke(entity: HdKeyEntity): LocalAccount.HdKey {
        return LocalAccount.HdKey(
            address = entity.algoAddress,
            publicKey = entity.publicKey,
            privateKey = privateKeyEncryptionManager.decrypt(entity.encryptedPrivateKey),
            seedId = entity.seedId,
            account = entity.account,
            change = entity.change,
            keyIndex = entity.keyIndex,
            derivationType = entity.derivationType
        )
    }
}