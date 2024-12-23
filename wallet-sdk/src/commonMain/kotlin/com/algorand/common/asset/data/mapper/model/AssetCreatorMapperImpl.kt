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

package com.algorand.common.asset.data.mapper.model

import com.algorand.common.asset.data.model.AssetCreatorResponse
import com.algorand.common.asset.domain.model.AssetCreator

internal class AssetCreatorMapperImpl : AssetCreatorMapper {

    override fun invoke(response: AssetCreatorResponse): AssetCreator? {
        return with(response) {
            if (id == null && publicKey == null && isVerifiedAssetCreator == null) {
                return@with null
            }
            AssetCreator(
                id = id,
                publicKey = publicKey,
                isVerifiedAssetCreator = isVerifiedAssetCreator,
            )
        }
    }

    override fun invoke(id: Long?, address: String?, isVerified: Boolean?): AssetCreator? {
        if (id == null && address == null && isVerified == null) {
            return null
        }
        return AssetCreator(
            id = id,
            publicKey = address,
            isVerifiedAssetCreator = isVerified,
        )
    }
}
