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

package com.algorand.wallet.swap.data.mapper

import com.algorand.wallet.asset.data.mapper.model.VerificationTierMapper
import com.algorand.wallet.asset.domain.model.VerificationTier
import com.algorand.wallet.swap.data.model.SwapSelectedAssetDto
import com.algorand.wallet.swap.domain.model.SwapSelectedAssetDetail
import java.math.BigInteger
import javax.inject.Inject

internal class DefaultSwapSelectedAssetDetailMapper @Inject constructor(
    private val verificationTierMapper: VerificationTierMapper
) : SwapSelectedAssetDetailMapper {

    override fun invoke(dto: SwapSelectedAssetDto): SwapSelectedAssetDetail? {
        return with(dto) {
            val verificationTier = verificationTier?.let(verificationTierMapper::invoke)
            SwapSelectedAssetDetail(
                assetId = assetId ?: return null,
                unitName = dto.unitName,
                verificationTier = verificationTier ?: VerificationTier.UNKNOWN,
                decimal = dto.decimal ?: return null,
                imageUrl = dto.imageUrl,
                amount = dto.assetHoldingAmount ?: BigInteger.ZERO,
                optInState = if (dto.assetHoldingAmount != null) {
                    SwapSelectedAssetDetail.OptInState.OptedIn
                } else {
                    SwapSelectedAssetDetail.OptInState.NotOptedIn
                },
                usdValue = usdValue
            )
        }
    }
}
