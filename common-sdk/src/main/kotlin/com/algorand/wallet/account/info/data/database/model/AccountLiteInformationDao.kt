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

package com.algorand.wallet.account.info.data.database.model

import androidx.room.ColumnInfo
import java.math.BigInteger

internal data class AccountLiteInformationDao(
    @ColumnInfo(name = "algo_address")
    val address: String,
    @ColumnInfo(name = "auth_algo_address")
    val rekeyAuthAddress: String?,
    @ColumnInfo(name = "algo_amount")
    val algoBalance: BigInteger,
    @ColumnInfo(name = "min_required_balance")
    val minRequiredBalance: BigInteger
)
