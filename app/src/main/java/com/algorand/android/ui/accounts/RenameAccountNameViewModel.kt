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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.module.custominfo.domain.usecase.SetCustomName
import com.algorand.android.utils.getOrThrow
import com.algorand.android.utils.launchIO
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RenameAccountNameViewModel @Inject constructor(
    private val setCustomName: SetCustomName,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val accountPublicKey: String = savedStateHandle.getOrThrow(PUBLIC_KEY)

    fun changeAccountName(accountName: String) {
        viewModelScope.launchIO {
            setCustomName(accountPublicKey, accountName)
        }
    }

    companion object {
        private const val PUBLIC_KEY = "publicKey"
    }
}
