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

package com.algorand.android.ui.backup.restore.passphrase

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.R
import com.algorand.android.ui.backup.restore.passphrase.RestoreBackupPassphraseViewModel.ViewEvent
import com.algorand.android.ui.backup.restore.passphrase.RestoreBackupPassphraseViewModel.ViewState
import com.algorand.android.utils.splitMnemonic
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

internal const val WORD_COUNT = 12

@HiltViewModel
class RestoreBackupPassphraseViewModel @Inject constructor(
    private val stateDelegate: StateDelegate<ViewState>,
    private val eventDelegate: EventDelegate<ViewEvent>
) : ViewModel(),
    StateViewModel<ViewState> by stateDelegate,
    EventViewModel<ViewEvent> by eventDelegate {

    init {
        stateDelegate.setDefaultState(ViewState(words = List(WORD_COUNT) { "" }))
    }

    fun updateWord(index: Int, word: String) {
        if (index !in 0 until WORD_COUNT) return
        if (word.containsWhitespaceOrComma()) {
            val tokens = word.splitMnemonic().filter { it.isNotBlank() }
            if (tokens.size > 1) {
                handlePastedMnemonic(tokens)
                return
            }
        }
        val sanitized = word.trim().lowercase()
        stateDelegate.updateState { current ->
            current.copy(
                words = current.words.toMutableList().apply { this[index] = sanitized }
            )
        }
    }

    private fun handlePastedMnemonic(tokens: List<String>) {
        if (tokens.size != WORD_COUNT) {
            eventDelegate.sendEvent(viewModelScope, ViewEvent.ShowError(R.string.the_last_copied_text))
            return
        }
        stateDelegate.updateState { current ->
            current.copy(words = tokens.map { it.trim().lowercase() })
        }
    }

    fun joinedPassphrase(): String = state.value.words.joinToString(separator = " ")

    private fun String.containsWhitespaceOrComma(): Boolean = any { it.isWhitespace() || it == ',' }

    data class ViewState(val words: List<String>) {
        val isProceedEnabled: Boolean
            get() = words.size == WORD_COUNT && words.all { it.isNotBlank() }
    }

    sealed interface ViewEvent {
        data class ShowError(@param:StringRes val messageResId: Int) : ViewEvent
    }
}
