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
import com.algorand.android.module.asset.detail.component.asset.domain.model.VerificationTier
import kotlinx.parcelize.Parcelize

@Parcelize
class WalletConnectTransactionAssetDetail(
    val assetId: Long,
    val fullName: String?,
    val shortName: String?,
    val fractionDecimals: Int?,
    val verificationTier: VerificationTier
) : Parcelable
