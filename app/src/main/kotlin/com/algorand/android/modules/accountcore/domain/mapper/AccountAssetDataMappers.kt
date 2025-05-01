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

package com.algorand.android.modules.accountcore.domain.mapper

import com.algorand.android.models.BaseAccountAssetData
import com.algorand.android.modules.parity.domain.model.ParityValue
import com.algorand.wallet.asset.domain.model.Asset
import com.algorand.wallet.asset.domain.model.AssetDetail
import java.math.BigDecimal
import java.math.BigInteger

internal interface PendingAdditionAssetDataMapper {
    operator fun invoke(asset: Asset): BaseAccountAssetData.PendingAssetData.AdditionAssetData
}

internal interface PendingDeletionAssetDataMapper {
    operator fun invoke(asset: Asset): BaseAccountAssetData.PendingAssetData.DeletionAssetData
}

internal interface AlgoAssetDataMapper {
    operator fun invoke(
        amount: BigInteger,
        parityValueInSelectedCurrency: ParityValue,
        parityValueInSecondaryCurrency: ParityValue,
        usdValue: BigDecimal
    ): BaseAccountAssetData.BaseOwnedAssetData.OwnedAssetData
}

internal interface OwnedAssetDataMapper {
    operator fun invoke(
        assetDetail: AssetDetail,
        amount: BigInteger,
        formattedAmount: String,
        formattedCompactAmount: String,
        parityValueInSelectedCurrency: ParityValue,
        parityValueInSecondaryCurrency: ParityValue,
        optedInAtRound: Long?
    ): BaseAccountAssetData.BaseOwnedAssetData.OwnedAssetData
}
