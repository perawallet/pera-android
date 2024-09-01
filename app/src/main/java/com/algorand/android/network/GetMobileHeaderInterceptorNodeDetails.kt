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

package com.algorand.android.network

import com.algorand.android.BuildConfig
import com.algorand.android.network.model.MobileHeaderInterceptorNodeDetails
import com.algorand.android.node.domain.Node
import com.algorand.android.node.domain.usecase.GetActiveNode

class GetMobileHeaderInterceptorNodeDetails(
    private val getActiveNode: GetActiveNode
) {

    operator fun invoke(): MobileHeaderInterceptorNodeDetails {
        return when (getActiveNode()) {
            Node.Mainnet -> getMainnetNodeDetails()
            Node.Testnet -> getTestnetNodeDetails()
        }
    }

    private fun getMainnetNodeDetails(): MobileHeaderInterceptorNodeDetails {
        return MobileHeaderInterceptorNodeDetails(
            baseUrl = BuildConfig.MOBILE_ALGORAND_MAINNET_BASE_URL
        )
    }

    private fun getTestnetNodeDetails(): MobileHeaderInterceptorNodeDetails {
        return MobileHeaderInterceptorNodeDetails(
            baseUrl = BuildConfig.MOBILE_ALGORAND_TESTNET_BASE_URL
        )
    }
}