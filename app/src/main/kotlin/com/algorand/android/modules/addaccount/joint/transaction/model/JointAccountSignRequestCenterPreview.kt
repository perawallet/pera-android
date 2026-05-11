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

package com.algorand.android.modules.addaccount.joint.transaction.model

import com.algorand.android.assetsearch.ui.model.VerificationTierConfiguration
import com.algorand.android.ui.compose.widget.asset.icon.AssetIconDrawable

sealed interface JointAccountSignRequestCenterPreview {

    data class Transfer(
        val recipientShortAddress: String,
        val amount: String,
        val convertedAmount: String
    ) : JointAccountSignRequestCenterPreview

    data class AssetAction(
        val type: Type,
        val shortAddress: String,
        val assetIcon: AssetIconDrawable,
        val assetName: String,
        val assetUnitName: String,
        val assetIdText: String,
        val verificationTier: VerificationTierConfiguration
    ) : JointAccountSignRequestCenterPreview {
        enum class Type { OPT_IN, OPT_OUT }
    }
}
