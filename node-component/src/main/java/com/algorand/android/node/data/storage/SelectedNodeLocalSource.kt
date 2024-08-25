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

package com.algorand.android.node.data.storage

import android.content.SharedPreferences
import com.algorand.android.caching.SharedPrefLocalSource
import com.algorand.android.node.data.mapper.NodeStorageMapper
import com.algorand.android.node.domain.Node
import javax.inject.Inject

internal class SelectedNodeLocalSource @Inject constructor(
    sharedPref: SharedPreferences,
    private val nodeStorageMapper: NodeStorageMapper
) : SharedPrefLocalSource<Node>(sharedPref) {

    override val key: String
        get() = SELECTED_NODE_KEY

    override fun getDataOrNull(): Node? {
        val nodeString = sharedPref.getString(SELECTED_NODE_KEY, null) ?: return null
        return nodeStorageMapper(nodeString)
    }

    override fun saveData(data: Node) {
        val nodeString = nodeStorageMapper(data)
        saveData { it.putString(SELECTED_NODE_KEY, nodeString) }
    }

    override fun getData(defaultValue: Node): Node {
        val nodeString = sharedPref.getString(SELECTED_NODE_KEY, null) ?: return defaultValue
        return nodeStorageMapper(nodeString)
    }

    companion object {
        const val SELECTED_NODE_KEY = "SELECTED_NODE_KEY"
    }
}
