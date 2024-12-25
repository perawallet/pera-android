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

package com.algorand.common.cache

import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch

internal class LifecycleAwareCacheManagerImpl : LifecycleAwareCacheManager {

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var currentJob: Job? = null
    private var initializationJob: Job? = null

    private var listener: LifecycleAwareCacheManager.CacheManagerListener? = null

    override fun onResume(owner: LifecycleOwner) {
        initializationJob = coroutineScope.launch(Dispatchers.IO) {
            listener?.onInitializeManager(this)
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        stop()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        clearResources()
        listener?.onClearResources()
    }

    override fun launchScope(action: suspend CoroutineScope.() -> Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            action()
        }
    }

    override fun startJob() {
        currentJob = coroutineScope.launch(Dispatchers.IO) {
            listener?.onStartJob(this)
        }
    }

    override fun setListener(listener: LifecycleAwareCacheManager.CacheManagerListener) {
        this.listener = listener
    }

    override fun stopCurrentJob() {
        if (currentJob?.isActive == true) {
            currentJob?.cancel()
        }
    }

    private fun stop() {
        currentJob?.cancel()
        currentJob = null
        initializationJob?.cancel()
        initializationJob = null
        coroutineScope.coroutineContext.cancelChildren()
    }

    private fun clearResources() {
        currentJob = null
        initializationJob = null
        coroutineScope.coroutineContext.cancelChildren()
    }
}
