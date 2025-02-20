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

package com.algorand.android.models

import android.os.Parcelable
import com.algorand.wallet.asset.domain.util.AssetConstants
import java.math.BigInteger
import kotlinx.parcelize.Parcelize

@Parcelize
data class AccountInformation(
    val address: String,
    val amount: BigInteger,
    val participation: Participation?,
    val rekeyAdminAddress: String?,
    private val allAssetHoldingMap: HashMap<Long, AssetHolding>,
    val createdAtRound: Long?,
    val appsLocalState: List<CreatedAppLocalState>? = null,
    val appsTotalSchema: CreatedAppStateScheme? = null,
    val appsTotalExtraPages: Int? = null,
    val totalCreatedApps: Int = 0,
    val lastFetchedRound: Long?
) : Parcelable {

    val assetHoldingMap: HashMap<Long, AssetHolding>
        get() = allAssetHoldingMap.filterNot { it.value.isDeleted } as? HashMap<Long, AssetHolding> ?: hashMapOf()

    fun isCreated(): Boolean {
        return createdAtRound != null
    }

    fun isRekeyed(): Boolean {
        return !rekeyAdminAddress.isNullOrEmpty() && rekeyAdminAddress != address
    }

    fun getAllAssetIds(): List<Long> {
        return assetHoldingMap.keys.toList()
    }

    fun hasAsset(assetId: Long): Boolean {
        return assetHoldingMap.containsKey(assetId) || assetId == AssetConstants.ALGO_ID
    }

    fun getAssetHoldingOrNull(assetId: Long): AssetHolding? {
        return assetHoldingMap.get(assetId)
    }

    fun getAssetHoldingList(): List<AssetHolding> {
        return assetHoldingMap.values.toList()
    }
}
