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

package com.algorand.android.mapper

import com.algorand.android.assetdetail.component.asset.domain.model.VerificationTier
import com.algorand.android.models.WalletConnectTransactionAssetDetail
import javax.inject.Inject

class WalletConnectTransactionAssetDetailMapper @Inject constructor() {

    fun mapToWalletConnectTransactionAssetDetail(
        assetId: Long,
        fullName: String?,
        shortName: String?,
        fractionDecimals: Int?,
        verificationTier: VerificationTier
    ): WalletConnectTransactionAssetDetail {
        return WalletConnectTransactionAssetDetail(
            assetId = assetId,
            fullName = fullName,
            shortName = shortName,
            fractionDecimals = fractionDecimals,
            verificationTier = verificationTier
        )
    }
}
