/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package com.algorand.wallet.asset.lite.domain.model

import com.algorand.wallet.asset.domain.model.VerificationTier
import java.math.BigDecimal

interface AssetDetailLiteBase {
    val id: Long
    val name: String?
    val shortName: String?
    val decimals: Int
    val creatorAddress: String?
    val logoUrl: String?
    val usdValue: BigDecimal?
    val verificationTier: VerificationTier
}

data class AssetDetailLite(
    override val id: Long,
    override val name: String?,
    override val shortName: String?,
    override val decimals: Int,
    override val creatorAddress: String?,
    override val logoUrl: String?,
    override val usdValue: BigDecimal?,
    override val verificationTier: VerificationTier
) : AssetDetailLiteBase

data class CollectibleDetailLite(
    override val id: Long,
    override val name: String?,
    override val shortName: String?,
    override val decimals: Int,
    override val creatorAddress: String?,
    override val logoUrl: String?,
    override val usdValue: BigDecimal?,
    override val verificationTier: VerificationTier,
    val collectibleName: String?
) : AssetDetailLiteBase
