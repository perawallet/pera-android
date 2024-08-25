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

package com.algorand.android.node.domain.usecase.implementation

import com.algorand.android.node.domain.Node
import com.algorand.android.node.domain.NodeConstants.MAINNET_NETWORK_SLUG
import com.algorand.android.node.domain.NodeConstants.TESTNET_NETWORK_SLUG
import com.algorand.android.node.domain.usecase.GetActiveNode
import com.algorand.android.node.domain.usecase.GetActiveNodeNetworkSlug
import javax.inject.Inject

internal class GetActiveNodeNetworkSlugUseCase @Inject constructor(
    private val getActiveNode: GetActiveNode
) : GetActiveNodeNetworkSlug {

    override fun invoke(): String {
        return when (getActiveNode()) {
            Node.Mainnet -> MAINNET_NETWORK_SLUG
            Node.Testnet -> TESTNET_NETWORK_SLUG
        }
    }
}
