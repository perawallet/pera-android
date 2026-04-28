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

import androidx.lifecycle.ViewModel
import com.algorand.android.ui.backup.restore.passphrase.RestoreBackupPassphraseViewModel.ViewState
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

internal const val WORD_COUNT = 12

@HiltViewModel
class RestoreBackupPassphraseViewModel @Inject constructor(
    private val stateDelegate: StateDelegate<ViewState>
) : ViewModel(), StateViewModel<ViewState> by stateDelegate {

    init {
        stateDelegate.setDefaultState(ViewState(words = List(WORD_COUNT) { "" }))
    }

    fun updateWord(index: Int, word: String) {
        if (index !in 0 until WORD_COUNT) return
        val sanitized = word.trim().lowercase()
        stateDelegate.updateState { current ->
            current.copy(
                words = current.words.toMutableList().apply { this[index] = sanitized }
            )
        }
    }

    fun joinedPassphrase(): String = state.value.words.joinToString(separator = " ")

    data class ViewState(val words: List<String>) {
        val isProceedEnabled: Boolean
            get() = words.size == WORD_COUNT && words.all { it.isNotBlank() }
    }
}
