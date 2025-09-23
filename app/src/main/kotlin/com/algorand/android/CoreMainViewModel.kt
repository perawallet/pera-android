/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android

import android.content.SharedPreferences
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.CoreMainViewModel.ViewEvent
import com.algorand.android.usecase.IsOnHdWalletUseCase
import com.algorand.android.utils.preference.getRegisterSkip
import com.algorand.wallet.account.local.domain.usecase.IsThereAnyLocalAccount
import com.algorand.wallet.viewmodel.EventViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoreMainViewModel @Inject constructor(
    private val isThereAnyLocalAccount: IsThereAnyLocalAccount,
    private val sharedPref: SharedPreferences,
    private val isOnHdWalletUseCase: IsOnHdWalletUseCase,
) : ViewModel(), EventViewModel<ViewEvent> {

    private val _viewEvent = MutableSharedFlow<ViewEvent>(extraBufferCapacity = VIEW_EVENT_BUFFER_CAPACITY)
    override val viewEvent: Flow<ViewEvent> = _viewEvent.asSharedFlow()

    fun initialize(savedInstanceState: Bundle?) {
        viewModelScope.launch {
            if (isThereAnyLocalAccount() || sharedPref.getRegisterSkip()) {
                _viewEvent.emit(ViewEvent.InitializeHomeNavigation)
            } else {
                _viewEvent.emit(ViewEvent.InitializeLoginNavigation)
            }
            _viewEvent.emit(ViewEvent.InitializeMainActivity(savedInstanceState))
            _viewEvent.emit(ViewEvent.InitializeCoreManagers)
        }
    }

    fun isHdWalletToggleEnabled(): Boolean {
        return isOnHdWalletUseCase.invoke()
    }

    sealed interface ViewEvent {
        data class InitializeMainActivity(val savedInstanceState: Bundle?) : ViewEvent
        data object InitializeHomeNavigation : ViewEvent
        data object InitializeLoginNavigation : ViewEvent
        data object InitializeCoreManagers : ViewEvent
    }

    private companion object {
        const val VIEW_EVENT_BUFFER_CAPACITY = 3
    }
}
