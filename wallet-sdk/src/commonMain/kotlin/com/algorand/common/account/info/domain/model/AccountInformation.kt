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

package com.algorand.common.account.info.domain.model

data class AccountInformation(
    val address: String,
    val amount: String,
    val lastFetchedRound: Long,
    val rekeyAdminAddress: String?,
    val totalAppsOptedIn: Int,
    val totalAssetsOptedIn: Int,
    val totalCreatedApps: Int,
    val totalCreatedAssets: Int,
    val appsTotalExtraPages: Int,
    val appsTotalSchema: AppStateScheme?,
    val assetHoldings: List<AssetHolding>,
    val createdAtRound: Long?
) {

    fun isRekeyed(): Boolean {
        return !rekeyAdminAddress.isNullOrEmpty() && rekeyAdminAddress != address
    }

    fun hasAsset(assetId: Long): Boolean {
        return assetHoldings.any { it.assetId == assetId }
    }

    fun hasAssetAmount(assetId: Long): Boolean {
        return assetHoldings.any { it.assetId == assetId && it.amount != "0" }
    }

    fun getAssetHoldingIds() = assetHoldings.map { it.assetId }

    fun isCreated() = createdAtRound != null
}
