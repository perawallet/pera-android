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

package com.algorand.android

import android.content.SharedPreferences
import com.algorand.android.core.BaseViewModel
import com.algorand.android.utils.preference.getRegisterSkip
import com.algorand.wallet.account.local.domain.usecase.IsThereAnyLocalAccount
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class CoreMainViewModel @Inject constructor(
    private val isThereAnyLocalAccount: IsThereAnyLocalAccount,
    private val eventDelegate: EventDelegate<ViewEvent>,
    private val sharedPref: SharedPreferences
) : BaseViewModel(), EventViewModel<CoreMainViewModel.ViewEvent> by eventDelegate {

    fun startNavigation() {
        runBlocking {
            eventDelegate.sendEvent(
                ViewEvent.StartNavigation(
                    if (isThereAnyLocalAccount() || sharedPref.getRegisterSkip()) {
                        R.id.homeNavigation
                    } else {
                        R.id.loginNavigation
                    }
                )
            )
        }
    }

    sealed interface ViewEvent {
        data class StartNavigation(val startDestinationFragmentId: Int) : ViewEvent
    }
}
