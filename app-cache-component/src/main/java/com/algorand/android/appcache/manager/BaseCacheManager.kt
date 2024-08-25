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

package com.algorand.android.appcache.manager

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch

internal abstract class BaseCacheManager : DefaultLifecycleObserver {

    protected val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var currentJob: Job? = null
    private var initializationJob: Job? = null

    protected val isCurrentJobActive: Boolean
        get() = currentJob?.isActive == true

    abstract suspend fun doJob(coroutineScope: CoroutineScope)

    protected open suspend fun initialize(coroutineScope: CoroutineScope) {
        // should be overridden if need
    }

    protected open fun doBeforeJobStarts() {
        // should be overridden if need
    }

    fun startJob() {
        doBeforeJobStarts()

        currentJob = coroutineScope.launch(Dispatchers.IO) { doJob(this) }
    }

    private fun stop() {
        currentJob?.cancel()
        currentJob = null
        initializationJob?.cancel()
        initializationJob = null
        coroutineScope.coroutineContext.cancelChildren()
    }

    protected open fun stopCurrentJob(cause: CancellationException? = null) {
        currentJob?.cancel(cause)
    }

    open fun clearResources() {
        currentJob = null
        initializationJob = null
        coroutineScope.coroutineContext.cancelChildren()
    }

    override fun onResume(owner: LifecycleOwner) {
        initializationJob = coroutineScope.launch(Dispatchers.IO) { initialize(this) }
    }

    override fun onPause(owner: LifecycleOwner) {
        stop()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        clearResources()
    }
}
