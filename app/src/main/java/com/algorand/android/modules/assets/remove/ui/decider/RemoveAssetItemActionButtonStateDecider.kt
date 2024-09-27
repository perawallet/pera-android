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

package com.algorand.android.modules.assets.remove.ui.decider

import com.algorand.android.models.ui.AccountAssetItemButtonState
import com.algorand.android.module.account.info.domain.model.AssetStatus
import javax.inject.Inject

class RemoveAssetItemActionButtonStateDecider @Inject constructor() {

    fun decideRemoveAssetItemActionButtonState(assetHoldingStatus: AssetStatus?): AccountAssetItemButtonState {
        return when (assetHoldingStatus) {
            AssetStatus.PENDING_FOR_REMOVAL, AssetStatus.PENDING_FOR_ADDITION -> AccountAssetItemButtonState.PROGRESS
            else -> AccountAssetItemButtonState.REMOVAL
        }
    }
}
