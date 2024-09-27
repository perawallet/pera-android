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

package com.algorand.android.module.foundation.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

object CoroutineExtensions {

    fun CoroutineScope.launchIfInactive(job: Job?, block: suspend CoroutineScope.() -> Unit): Job {
        if (job?.isActive == true) {
            return job
        }
        return launch { block() }
    }

    fun CoroutineScope.launchIO(call: suspend () -> Unit): Job {
        return launch(Dispatchers.IO) { call() }
    }

    fun CoroutineScope.launchMain(call: suspend () -> Unit): Job {
        return launch(Dispatchers.Main) { call() }
    }

    fun CoroutineScope.launchDefault(call: suspend () -> Unit): Job {
        return launch(Dispatchers.Default) { call() }
    }

    fun CoroutineScope.launchUnconfined(call: suspend () -> Unit): Job {
        return launch(Dispatchers.Unconfined) { call() }
    }
}
