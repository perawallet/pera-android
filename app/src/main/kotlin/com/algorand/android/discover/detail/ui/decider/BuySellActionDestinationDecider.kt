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

package com.algorand.android.discover.detail.ui.decider

import com.algorand.android.discover.common.ui.model.DiscoverAction
import com.algorand.android.discover.common.ui.model.DiscoverAction.BUY_ALGO
import com.algorand.android.discover.common.ui.model.DiscoverAction.SWAP_FROM_ALGO
import com.algorand.android.discover.common.ui.model.DiscoverAction.SWAP_FROM_TOKEN
import com.algorand.android.discover.common.ui.model.DiscoverAction.SWAP_TO_TOKEN
import com.algorand.android.discover.detail.ui.model.BuySellActionRequest
import javax.inject.Inject

class BuySellActionDestinationDecider @Inject constructor() {

    fun getBuySellActionDestination(discoverAction: DiscoverAction?): BuySellActionRequest.Destination? {
        return when (discoverAction) {
            BUY_ALGO -> BuySellActionRequest.Destination.MELD
            SWAP_FROM_ALGO, SWAP_FROM_TOKEN, SWAP_TO_TOKEN -> BuySellActionRequest.Destination.SWAP
            else -> null
        }
    }
}
