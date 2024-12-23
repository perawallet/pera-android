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

package com.algorand.common.asset.domain.model

import com.algorand.common.asset.domain.util.AssetConstants.ALGO_ID

sealed interface Asset {

    val id: Long
    val assetInfo: AssetInfo?
    val verificationTier: VerificationTier

    val isAlgo
        get() = id == ALGO_ID

    val fullName: String?
        get() = assetInfo?.name?.fullName

    val shortName: String?
        get() = assetInfo?.name?.shortName

    val usdValue: String?
        get() = assetInfo?.fiat?.usdValue

    val creatorAddress: String?
        get() = assetInfo?.creator?.publicKey

    val logoUri: String?
        get() = assetInfo?.logo?.uri

    fun getDecimalsOrZero(): Int {
        return assetInfo?.decimals ?: 0
    }

    fun hasUsdValue(): Boolean {
        return assetInfo?.fiat?.usdValue != null || id == ALGO_ID
    }

    data class AssetInfo(
        val name: Name,
        val decimals: Int,
        val fiat: Fiat?,
        val creator: AssetCreator?,
        val logo: Logo?,
        val explorerUrl: String?,
        val project: Project?,
        val social: Social?,
        val description: String?,
        val supply: Supply?,
        val url: String?,
        val isAvailableOnDiscoverMobile: Boolean?
    )

    data class Name(
        val fullName: String,
        val shortName: String
    )

    data class Logo(
        val uri: String?,
        val svgUri: String?
    )

    data class Project(
        val name: String?,
        val url: String?,
    )

    data class Social(
        val discordUrl: String?,
        val telegramUrl: String?,
        val twitterUsername: String?
    )

    data class Supply(
        val total: String?,
        val max: String?
    )

    data class Fiat(
        val usdValue: String?,
        val last24HoursAlgoPriceChangePercentage: String?
    )
}
