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

package com.algorand.android.module.node.data.mapper

import com.algorand.android.module.node.domain.Node
import com.algorand.android.module.node.domain.NodeConstants
import javax.inject.Inject

internal class NodeNameMapperImpl @Inject constructor() : NodeNameMapper {

    override fun invoke(node: Node): String {
        return when (node) {
            is Node.Mainnet -> NodeConstants.MAINNET_NODE_NAME
            is Node.Testnet -> NodeConstants.TESTNET_NODE_NAME
        }
    }
}
