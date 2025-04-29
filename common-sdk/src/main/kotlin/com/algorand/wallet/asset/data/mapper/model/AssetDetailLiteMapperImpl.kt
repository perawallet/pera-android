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

package com.algorand.wallet.asset.data.mapper.model

import com.algorand.wallet.asset.domain.model.VerificationTier
import com.algorand.wallet.asset.data.database.model.AssetDetailLiteDto
import com.algorand.wallet.asset.lite.domain.model.AssetDetailLite
import javax.inject.Inject

internal class AssetDetailLiteMapperImpl @Inject constructor() : AssetDetailLiteMapper {

    override fun invoke(assetDetailLiteDto: AssetDetailLiteDto): AssetDetailLite {
        return AssetDetailLite(
            assetDetailLiteDto.id,
            assetDetailLiteDto.name,
            assetDetailLiteDto.unitName,
            assetDetailLiteDto.decimals,
            assetDetailLiteDto.assetCreatorAddress,
            assetDetailLiteDto.logoUrl,
            assetDetailLiteDto.usdValue,
            VerificationTier.VERIFIED
        )
    }
}
