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

package com.algorand.android.modules.assets.manage.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.algorand.android.core.BaseViewModel
import com.algorand.android.core.component.detail.domain.model.AccountDetail
import com.algorand.android.core.component.detail.domain.usecase.GetAccountDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ManageAssetsViewModel @Inject constructor(
    private val getAccountDetail: GetAccountDetail,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val navArgs = ManageAssetsBottomSheetArgs.fromSavedStateHandle(savedStateHandle)
    val publicKey = navArgs.publicKey

    private val _accountDetailFlow = MutableStateFlow<AccountDetail?>(null)
    val accountDetailFlow: StateFlow<AccountDetail?>
        get() = _accountDetailFlow

    fun initAccountDetail() {
        viewModelScope.launch {
            _accountDetailFlow.value = getAccountDetail(publicKey)
        }
    }
}
