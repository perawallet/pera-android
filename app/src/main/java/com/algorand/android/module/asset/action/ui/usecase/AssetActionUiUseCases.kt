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

package com.algorand.android.module.asset.action.ui.usecase

import com.algorand.android.module.asset.action.ui.model.AssetActionAccountDetail
import com.algorand.android.module.asset.action.ui.model.AssetActionInformation
import com.algorand.android.module.asset.action.ui.model.AssetActionPreview
import com.algorand.android.module.foundation.PeraResult
import kotlinx.coroutines.flow.Flow

interface GetAssetActionPreview {
    operator fun invoke(assetId: Long, accountAddress: String?): Flow<AssetActionPreview>
}

internal interface GetAssetActionInformation {
    suspend operator fun invoke(assetId: Long): PeraResult<AssetActionInformation>
}

internal interface GetAssetActionAccountDetail {
    suspend operator fun invoke(accountAddress: String): AssetActionAccountDetail
}
