/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.module.asset.detail.ui.detail.mapper

import com.algorand.android.accountcore.ui.model.AccountDisplayName
import com.algorand.android.accountcore.ui.summary.model.AccountDetailSummary
import com.algorand.android.module.asset.detail.ui.detail.model.AssetDetailPreview
import com.algorand.android.core.component.domain.model.BaseAccountAssetData
import java.math.BigDecimal

internal interface AssetDetailPreviewMapper {
    suspend operator fun invoke(
        baseOwnedAssetDetail: BaseAccountAssetData.BaseOwnedAssetData,
        accountDisplayName: AccountDisplayName,
        isQuickActionButtonsVisible: Boolean,
        isSwapButtonSelected: Boolean,
        isSwapButtonVisible: Boolean,
        isMarketInformationVisible: Boolean,
        last24HoursChange: BigDecimal?,
        formattedAssetPrice: String?,
        accountDetailSummary: AccountDetailSummary
    ): AssetDetailPreview
}
