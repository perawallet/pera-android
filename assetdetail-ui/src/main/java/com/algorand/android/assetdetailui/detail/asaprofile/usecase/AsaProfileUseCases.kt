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

package com.algorand.android.assetdetailui.detail.asaprofile.usecase

import com.algorand.android.assetdetail.component.asset.domain.model.detail.Asset
import com.algorand.android.assetdetailui.detail.asaprofile.model.AsaProfilePreview
import com.algorand.android.assetdetailui.detail.asaprofile.model.AsaStatusPreview
import kotlinx.coroutines.flow.Flow

interface GetAsaProfilePreview {
    suspend operator fun invoke(accountAddress: String?, assetId: Long): Flow<AsaProfilePreview?>
}

internal interface GetAsaProfilePreviewWithoutAccountInformation {
    suspend operator fun invoke(): Flow<AsaProfilePreview?>
}

internal interface CreateAsaProfilePreviewFromAssetDetail {
    suspend operator fun invoke(asset: Asset, asaStatusPreview: AsaStatusPreview?): AsaProfilePreview
}

internal interface GetAlgoProfilePreviewWithAccountInformation {
    suspend operator fun invoke(accountAddress: String): Flow<AsaProfilePreview?>
}

internal interface GetAsaProfilePreviewWithAccountInformation {
    suspend operator fun invoke(accountAddress: String, assetId: Long): Flow<AsaProfilePreview?>
}
