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

package com.algorand.android.assetdetailui.detail.usecase

import com.algorand.android.assetdetailui.detail.model.AssetDetailPreview
import java.math.BigDecimal
import kotlinx.coroutines.flow.Flow

interface GetAssetDetailPreviewFlow {
    suspend operator fun invoke(
        accountAddress: String,
        assetId: Long,
        isQuickActionButtonsVisible: Boolean
    ): Flow<AssetDetailPreview?>
}

internal interface GetAssetDetailTextColorResOfChangePercentage {
    operator fun invoke(last24HoursChange: BigDecimal?): Int?
}

internal interface GetAssetDetailIconResOfChangePercentage {
    operator fun invoke(last24HoursChange: BigDecimal?): Int?
}

internal interface IsChangePercentageVisible {
    operator fun invoke(last24HoursChange: BigDecimal?): Boolean
}
