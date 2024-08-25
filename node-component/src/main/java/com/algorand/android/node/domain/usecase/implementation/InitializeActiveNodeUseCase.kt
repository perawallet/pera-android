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

import com.algorand.android.foundation.app.AppFlavor
import com.algorand.android.foundation.app.GetAppFlavor
import com.algorand.android.node.domain.Node
import com.algorand.android.node.domain.repository.NodeRepository
import com.algorand.android.node.domain.usecase.InitializeActiveNode
import javax.inject.Inject

internal class InitializeActiveNodeUseCase @Inject constructor(
    private val getAppFlavor: GetAppFlavor,
    private val nodeRepository: NodeRepository
) : InitializeActiveNode {

    override fun invoke(): Node {
        val defaultNode = when (getAppFlavor()) {
            AppFlavor.STAGING -> Node.Testnet
            AppFlavor.PRODUCTION -> Node.Mainnet
        }
        return nodeRepository.initializeActiveNode(defaultNode)
    }
}
