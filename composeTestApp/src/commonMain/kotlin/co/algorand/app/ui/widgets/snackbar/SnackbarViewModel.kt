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

package co.algorand.app.ui.widgets.snackbar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.common.viewmodel.EventDelegate
import com.algorand.common.viewmodel.EventViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

class SnackbarViewModel(
    private val eventDelegate: EventDelegate<ViewEvent>
) : ViewModel(), EventViewModel<SnackbarViewModel.ViewEvent> by eventDelegate {

    fun setSnackBarMessage(resource: StringResource) {
        viewModelScope.launch {
            eventDelegate.sendEvent(ViewEvent.DisplaySnackBar(getString(resource = resource)))
        }
    }

    fun setSnackBarMessage(str: String) {
        viewModelScope.launch {
            eventDelegate.sendEvent(ViewEvent.DisplaySnackBar(str))
        }
    }

    sealed interface ViewEvent {
        data object Idle : ViewEvent
        data class DisplaySnackBar(val message: String) : ViewEvent
    }
}
