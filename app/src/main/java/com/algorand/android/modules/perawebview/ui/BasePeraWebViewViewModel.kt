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

package com.algorand.android.modules.perawebview.ui

import androidx.lifecycle.viewModelScope
import com.algorand.android.discover.common.ui.model.WebViewError
import com.algorand.android.modules.basewebview.ui.BaseWebViewViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

abstract class BasePeraWebViewViewModel : BaseWebViewViewModel() {

    private val _lastErrorFlow: MutableStateFlow<WebViewError?> = MutableStateFlow(null)

    open fun onPageRequestedShouldOverrideUrlLoading(url: String): Boolean {
        return false
    }

    open fun onPageStarted() {}
    open fun onPageFinished(title: String? = null, url: String? = null) {}
    open fun onError() {}
    open fun onHttpError() {}
    open fun onPageUrlChanged() {}

    fun saveLastError(error: WebViewError?) {
        viewModelScope.launch {
            _lastErrorFlow
                .emit(error)
        }
    }

    fun getLastError(): WebViewError? {
        return _lastErrorFlow.value
    }

    fun clearLastError() {
        viewModelScope.launch {
            _lastErrorFlow.emit(null)
        }
    }
}
