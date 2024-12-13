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

package com.algorand.common.account.info.data.mapper

import com.algorand.common.account.info.data.database.model.AssetHoldingEntity
import com.algorand.common.account.info.data.database.model.AssetStatusEntity
import com.algorand.common.account.info.data.model.AssetHoldingResponse
import com.algorand.common.encryption.AddressEncryptionManager

internal class AssetHoldingEntityMapperImpl(
    private val addressEncryptionManager: AddressEncryptionManager
) : AssetHoldingEntityMapper {

    override fun invoke(address: String, response: AssetHoldingResponse): AssetHoldingEntity? {
        return AssetHoldingEntity(
            encryptedAddress = addressEncryptionManager.encrypt(address),
            assetId = response.assetId ?: return null,
            amount = response.amount ?: return null,
            isDeleted = response.isDeleted ?: false,
            isFrozen = response.isFrozen ?: false,
            optedInAtRound = response.optedInAtRound,
            optedOutAtRound = response.optedOutAtRound,
            assetStatusEntity = AssetStatusEntity.OWNED_BY_ACCOUNT
        )
    }

    override fun invoke(responses: List<Pair<String, AssetHoldingResponse>>): List<AssetHoldingEntity> {
        return responses.mapNotNull { (address, assetHoldingResponse) ->
            invoke(address, assetHoldingResponse)
        }
    }
}
