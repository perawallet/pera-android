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

package com.algorand.android.module.node.data.repository

import com.algorand.android.caching.CacheResult
import com.algorand.android.caching.SingleInMemoryLocalCache
import com.algorand.android.module.node.data.storage.SelectedNodeLocalSource
import com.algorand.android.module.node.domain.Node
import com.algorand.android.module.node.domain.repository.NodeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class NodeRepositoryImpl(
    private val activeNodeCache: SingleInMemoryLocalCache<Node>,
    private val selectedNodeLocalSource: SelectedNodeLocalSource
) : NodeRepository {

    override fun initializeActiveNode(defaultNode: Node): Node {
        val selectedNode = selectedNodeLocalSource.getData(defaultNode)
        activeNodeCache.put(CacheResult.Success.create(selectedNode))
        return selectedNode
    }

    override fun getActiveNodeAsFlow(): Flow<Node> {
        return activeNodeCache.cacheFlow.map { (it as CacheResult.Success).data }
    }

    override fun getActiveNode(): Node {
        return (activeNodeCache.getOrNull() as CacheResult.Success).data
    }

    override fun setActiveNode(node: Node) {
        selectedNodeLocalSource.saveData(node)
        activeNodeCache.put(CacheResult.Success.create(node))
    }
}
