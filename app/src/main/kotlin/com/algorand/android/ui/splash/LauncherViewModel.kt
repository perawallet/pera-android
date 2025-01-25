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

package com.algorand.android.ui.splash

import android.content.SharedPreferences
import androidx.lifecycle.viewModelScope
import com.algorand.android.core.BaseViewModel
import com.algorand.android.database.NodeDao
import com.algorand.android.network.AlgodInterceptor
import com.algorand.android.network.IndexerInterceptor
import com.algorand.android.network.MobileHeaderInterceptor
import com.algorand.android.utils.findAllNodes
import com.algorand.common.remoteconfig.domain.usecase.InitializeOperationalToggles
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@HiltViewModel
class LauncherViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val mobileHeaderInterceptor: MobileHeaderInterceptor,
    private val algodInterceptor: AlgodInterceptor,
    private val indexerInterceptor: IndexerInterceptor,
    private val nodeDao: NodeDao,
    private val initializeOperationalToggles: InitializeOperationalToggles
) : BaseViewModel() {

    private val opsToggleInitializationFlow = MutableStateFlow(false)
    private val nodeInitializationFlow = MutableStateFlow(false)
    val appInitializationStatusFlow = combine(opsToggleInitializationFlow, nodeInitializationFlow) { opsToggle, node ->
        opsToggle && node
    }

    fun initApp() {
        initOpsToggle()
        initializeNodeInterceptor()
    }

    private fun initOpsToggle() {
        viewModelScope.launch {
            initializeOperationalToggles()
            opsToggleInitializationFlow.value = true
        }
    }

    private fun initializeNodeInterceptor() {
        viewModelScope.launch {
            if (indexerInterceptor.currentActiveNode == null) {
                val lastActivatedNode = findAllNodes(sharedPreferences, nodeDao).find { it.isActive }
                lastActivatedNode?.activate(indexerInterceptor, mobileHeaderInterceptor, algodInterceptor)
            }
            nodeInitializationFlow.value = true
        }
    }
}
