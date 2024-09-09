/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.assetdetailui.detail.nftprofile.mapper

import com.algorand.android.assetdetail.component.asset.domain.model.CollectibleTrait
import com.algorand.android.assetdetailui.detail.nftprofile.model.BaseCollectibleMediaItem
import com.algorand.android.assetdetailui.detail.nftprofile.model.CollectibleMediaItemMapperPayload
import com.algorand.android.assetdetailui.detail.nftprofile.model.CollectibleTraitItem
import com.algorand.android.core.component.detail.domain.model.AccountType
import java.math.BigDecimal
import java.math.BigInteger

interface CollectibleAmountFormatter {
    operator fun invoke(
        nftAmount: BigInteger?,
        fractionalDecimal: Int?,
        formattedAmount: String?,
        formattedCompactAmount: String?
    ): String

    operator fun invoke(nftAmount: BigDecimal?, fractionalDecimal: Int?): String
}

interface CollectibleDetailWarningTextMapper {
    fun mapToWarningTextResId(prismUrl: String?): Int?

    fun mapToOptedInWarningTextResId(
        isOwnedByTheUser: Boolean,
        accountType: AccountType?
    ): Int?
}

internal interface CollectibleMediaItemMapper {

    operator fun invoke(payload: CollectibleMediaItemMapperPayload): BaseCollectibleMediaItem
}

internal interface CollectibleTraitItemMapper {
    operator fun invoke(trait: CollectibleTrait): CollectibleTraitItem
}
