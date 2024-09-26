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

package com.algorand.android.ui.settings.node.ui.mapper

import com.algorand.android.module.node.data.mapper.NodeNameMapper
import com.algorand.android.module.node.domain.Node
import com.algorand.android.module.node.domain.usecase.GetActiveNode
import com.algorand.android.ui.settings.node.ui.model.NodeItem
import com.algorand.android.ui.settings.node.ui.model.NodeSettingsPreview
import javax.inject.Inject

class NodeSettingsPreviewMapper @Inject constructor(
    private val getActiveNode: GetActiveNode,
    private val nodeNameMapper: NodeNameMapper
) {

    fun mapToNodeSettingsPreview(isLoading: Boolean, nodeList: List<Node>): NodeSettingsPreview {
        val activeNode = getActiveNode()
        val nodeItemList = nodeList.map {
            NodeItem(it, nodeNameMapper(it), it == activeNode)
        }
        return NodeSettingsPreview(isLoading = isLoading, nodeList = nodeItemList)
    }
}
