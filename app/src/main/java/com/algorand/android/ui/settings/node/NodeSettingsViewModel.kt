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

package com.algorand.android.ui.settings.node

import androidx.lifecycle.viewModelScope
import com.algorand.android.core.BaseViewModel
import com.algorand.android.module.appcache.manager.PushTokenManager
import com.algorand.android.module.appcache.usecase.ClearAppSessionCache
import com.algorand.android.module.appcache.usecase.RefreshAccountCacheManager
import com.algorand.android.module.deviceid.domain.usecase.GetNodeDeviceId
import com.algorand.android.module.deviceid.domain.usecase.UnregisterDeviceId
import com.algorand.android.module.node.domain.Node
import com.algorand.android.module.node.domain.usecase.GetActiveNode
import com.algorand.android.module.node.domain.usecase.SetSelectedNode
import com.algorand.android.ui.settings.node.ui.model.NodeSettingsPreview
import com.algorand.android.usecase.NodeSettingsPreviewUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class NodeSettingsViewModel @Inject constructor(
    nodeSettingsPreviewUseCase: NodeSettingsPreviewUseCase,
    private val getActiveNode: GetActiveNode,
    private val setSelectedNode: SetSelectedNode,
    private val unregisterDeviceId: UnregisterDeviceId,
    private val getNodeDeviceId: GetNodeDeviceId,
    private val pushTokenManager: PushTokenManager,
    private val clearAppSessionCache: ClearAppSessionCache,
    private val refreshAccountCacheManager: RefreshAccountCacheManager
) : BaseViewModel() {

    private val _nodeSettingsFlow = MutableStateFlow(nodeSettingsPreviewUseCase.getInitialPreview())
    val nodeSettingsFlow: StateFlow<NodeSettingsPreview> get() = _nodeSettingsFlow

    fun canNavigateBack(): Boolean = !_nodeSettingsFlow.value.isLoading

    fun onNodeChanged(activatedNode: Node) {
        viewModelScope.launch(Dispatchers.IO) {
            showLoading()
            val previousSelectedNode = getActiveNode()
            setSelectedNode(activatedNode)
            unregisterNodeDeviceId(previousSelectedNode)
            clearAppSessionCache()
            refreshAccountCacheManager()
            pushTokenManager.refreshPushToken()
            updateUiWithSelectedNode(activatedNode)
        }
    }

    private fun showLoading() {
        _nodeSettingsFlow.update {
            it.copy(isLoading = true)
        }
    }

    private fun updateUiWithSelectedNode(node: Node) {
        _nodeSettingsFlow.update {
            it.copy(
                isLoading = false,
                nodeList = it.nodeList.map { nodeItem ->
                    nodeItem.copy(isActive = nodeItem.node == node)
                }
            )
        }
    }

    private suspend fun unregisterNodeDeviceId(node: Node) {
        val deviceId = getNodeDeviceId(node) ?: return
        unregisterDeviceId(deviceId)
    }
}
