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

package com.algorand.android.modules.collectibles.common.mapper

import com.algorand.android.models.BaseAccountAssetData
import com.algorand.android.modules.parity.domain.usecase.GetPrimaryCurrencyAssetParityValue
import com.algorand.android.modules.parity.domain.usecase.GetSecondaryCurrencyAssetParityValue
import com.algorand.android.utils.formatting.FormatAmountByCollectibleFractionalDigit
import com.algorand.android.utils.orZero
import com.algorand.wallet.account.info.domain.model.AssetHolding
import com.algorand.wallet.asset.domain.model.AudioCollectibleDetail
import com.algorand.wallet.asset.domain.model.CollectibleDetail
import com.algorand.wallet.asset.domain.model.ImageCollectibleDetail
import com.algorand.wallet.asset.domain.model.MixedCollectibleDetail
import com.algorand.wallet.asset.domain.model.UnsupportedCollectibleDetail
import com.algorand.wallet.asset.domain.model.VideoCollectibleDetail
import javax.inject.Inject

internal class BaseOwnedCollectibleDataFactoryImpl @Inject constructor(
    private val ownedCollectibleImageDataMapper: OwnedCollectibleImageDataMapper,
    private val ownedCollectibleVideoDataMapper: OwnedCollectibleVideoDataMapper,
    private val ownedCollectibleMixedDataMapper: OwnedCollectibleMixedDataMapper,
    private val ownedCollectibleAudioDataMapper: OwnedCollectibleAudioDataMapper,
    private val ownedCollectibleNotSupportedDataMapper: OwnedCollectibleNotSupportedDataMapper,
    private val getPrimaryCurrencyAssetParityValue: GetPrimaryCurrencyAssetParityValue,
    private val getSecondaryCurrencyAssetParityValue: GetSecondaryCurrencyAssetParityValue,
    private val formatAmountByCollectibleFractionalDigit: FormatAmountByCollectibleFractionalDigit
) : BaseOwnedCollectibleDataFactory {

    @Suppress("LongMethod")
    override fun invoke(
        assetHolding: AssetHolding,
        collectibleDetail: CollectibleDetail
    ): BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData {
        val safeDecimal = collectibleDetail.getDecimalsOrZero()
        val amount = assetHolding.amount
        val parityValueInSelectedCurrency = getPrimaryCurrencyAssetParityValue(
            amount,
            collectibleDetail.usdValue.orZero(),
            safeDecimal
        )
        val parityValueInSecondaryCurrency = getSecondaryCurrencyAssetParityValue(
            amount,
            collectibleDetail.usdValue.orZero(),
            safeDecimal
        )

        return when (collectibleDetail) {
            is ImageCollectibleDetail -> {
                ownedCollectibleImageDataMapper(
                    collectibleDetail = collectibleDetail,
                    amount = amount,
                    formattedAmount = formatAmountByCollectibleFractionalDigit(amount, safeDecimal),
                    formattedCompactAmount = formatAmountByCollectibleFractionalDigit(amount, safeDecimal, true),
                    parityValueInSelectedCurrency = parityValueInSelectedCurrency,
                    parityValueInSecondaryCurrency = parityValueInSecondaryCurrency,
                    optedInAtRound = assetHolding.optedInAtRound
                )
            }
            is VideoCollectibleDetail -> {
                ownedCollectibleVideoDataMapper(
                    collectibleDetail = collectibleDetail,
                    amount = amount,
                    formattedAmount = formatAmountByCollectibleFractionalDigit(amount, safeDecimal),
                    formattedCompactAmount = formatAmountByCollectibleFractionalDigit(amount, safeDecimal, true),
                    parityValueInSelectedCurrency = parityValueInSelectedCurrency,
                    parityValueInSecondaryCurrency = parityValueInSecondaryCurrency,
                    optedInAtRound = assetHolding.optedInAtRound
                )
            }
            is MixedCollectibleDetail -> {
                ownedCollectibleMixedDataMapper(
                    collectibleDetail = collectibleDetail,
                    amount = amount,
                    formattedAmount = formatAmountByCollectibleFractionalDigit(amount, safeDecimal),
                    formattedCompactAmount = formatAmountByCollectibleFractionalDigit(amount, safeDecimal, true),
                    parityValueInSelectedCurrency = parityValueInSelectedCurrency,
                    parityValueInSecondaryCurrency = parityValueInSecondaryCurrency,
                    optedInAtRound = assetHolding.optedInAtRound
                )
            }
            is AudioCollectibleDetail -> {
                ownedCollectibleAudioDataMapper(
                    collectibleDetail = collectibleDetail,
                    amount = amount,
                    formattedAmount = formatAmountByCollectibleFractionalDigit(amount, safeDecimal),
                    formattedCompactAmount = formatAmountByCollectibleFractionalDigit(amount, safeDecimal, true),
                    parityValueInSelectedCurrency = parityValueInSelectedCurrency,
                    parityValueInSecondaryCurrency = parityValueInSecondaryCurrency,
                    optedInAtRound = assetHolding.optedInAtRound
                )
            }
            is UnsupportedCollectibleDetail -> {
                ownedCollectibleNotSupportedDataMapper(
                    collectibleDetail = collectibleDetail,
                    amount = amount,
                    formattedAmount = formatAmountByCollectibleFractionalDigit(amount, safeDecimal),
                    formattedCompactAmount = formatAmountByCollectibleFractionalDigit(amount, safeDecimal, true),
                    parityValueInSelectedCurrency = parityValueInSelectedCurrency,
                    parityValueInSecondaryCurrency = parityValueInSecondaryCurrency,
                    optedInAtRound = assetHolding.optedInAtRound
                )
            }
        }
    }
}
