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

package com.algorand.android.customviews.accountandassetitem.model

import com.algorand.android.assetsearch.ui.model.VerificationTierConfiguration
import com.algorand.android.models.ButtonConfiguration
import com.algorand.android.utils.AssetName
import com.algorand.android.utils.assetdrawable.BaseAssetDrawableProvider
import java.math.BigDecimal

// TODO: Remove this class and its children and pass as much as atomic value possible to AssetItemView/AccountItemView
sealed class BaseItemConfiguration {

    abstract val primaryValueText: String?
    abstract val secondaryValueText: String?

    abstract val primaryValue: BigDecimal?
    abstract val secondaryValue: BigDecimal?

    abstract val actionButtonConfiguration: ButtonConfiguration?
    abstract val checkButtonConfiguration: ButtonConfiguration?
    abstract val dragButtonConfiguration: ButtonConfiguration?

    sealed class BaseAssetItemConfiguration : BaseItemConfiguration() {

        abstract val verificationTierConfiguration: VerificationTierConfiguration?
        abstract val assetId: Long
        abstract val assetIconDrawableProvider: BaseAssetDrawableProvider?
        abstract val primaryAssetName: AssetName?
        abstract val secondaryAssetName: AssetName?
        abstract val showWithAssetId: Boolean?

        data class AssetItemConfiguration(
            override val assetId: Long,
            override val primaryValue: BigDecimal? = null,
            override val primaryValueText: String? = null,
            override val secondaryValue: BigDecimal? = null,
            override val secondaryValueText: String? = null,
            override val primaryAssetName: AssetName? = null,
            override val secondaryAssetName: AssetName? = null,
            override val verificationTierConfiguration: VerificationTierConfiguration? = null,
            override val assetIconDrawableProvider: BaseAssetDrawableProvider? = null,
            override val showWithAssetId: Boolean? = null,
            override val checkButtonConfiguration: ButtonConfiguration? = null,
            override val dragButtonConfiguration: ButtonConfiguration? = null,
            override val actionButtonConfiguration: ButtonConfiguration? = null,
            val isPending: Boolean? = null
        ) : BaseAssetItemConfiguration()
    }
}
