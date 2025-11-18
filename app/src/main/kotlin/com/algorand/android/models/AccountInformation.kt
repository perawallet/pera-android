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

package com.algorand.android.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigInteger

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

    fun isRekeyed(): Boolean {
        return !rekeyAdminAddress.isNullOrEmpty() && rekeyAdminAddress != address
    }
}
