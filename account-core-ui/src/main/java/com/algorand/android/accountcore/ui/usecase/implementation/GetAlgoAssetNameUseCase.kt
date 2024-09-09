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

package com.algorand.android.accountcore.ui.usecase.implementation

import com.algorand.android.accountcore.ui.model.AlgoAssetName
import com.algorand.android.accountcore.ui.usecase.GetAlgoAssetName
import com.algorand.android.accountcore.ui.usecase.GetAssetName
import com.algorand.android.assetdetail.component.AssetConstants.ALGO_FULL_NAME
import com.algorand.android.assetdetail.component.AssetConstants.ALGO_SHORT_NAME
import javax.inject.Inject

internal class GetAlgoAssetNameUseCase @Inject constructor(
    private val getAssetName: GetAssetName
) : GetAlgoAssetName {

    override fun invoke(): AlgoAssetName {
        return AlgoAssetName(
            fullName = getAssetName(ALGO_FULL_NAME),
            shortName = getAssetName(ALGO_SHORT_NAME)
        )
    }
}
