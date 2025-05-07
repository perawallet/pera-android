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

package com.algorand.wallet.asset.domain.model

import com.algorand.wallet.account.info.domain.model.AssetStatus
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_ID
import java.math.BigDecimal
import java.math.BigInteger

data class AssetLite(
    val address: String,
    val amount: BigInteger,
    val decimal: Int,
    val usdValue: BigDecimal?,
    val totalUsdValue: BigDecimal?,
    val assetId: Long,
    val name: String?,
    val shortName: String?,
    val type: Type,
    val verificationTier: VerificationTier,
    val assetStatus: AssetStatus,
    val optedInAtRound: Long?
) {

    val isAlgo: Boolean
        get() = assetId == ALGO_ID

    val logoUrl: String?
        get() = type.logoUrl

    sealed interface Type {

        val logoUrl: String?

        data class Asset(override val logoUrl: String?) : Type

        data class Collectible(
            override val logoUrl: String?,
            val name: String,
            val collectionName: String?,
            val mediaType: CollectibleMediaType
        ) : Type
    }
}
