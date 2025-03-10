/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License
 *
 */

package com.algorand.android.ui.accounts

sealed class ViewPassphraseContract {

    data class State(
        val mnemonic: String? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    )

    sealed class Intent {
        object LoadMnemonic : Intent()
    }

    sealed class Effect {
        object NavigateBack : Effect()
    }
}
