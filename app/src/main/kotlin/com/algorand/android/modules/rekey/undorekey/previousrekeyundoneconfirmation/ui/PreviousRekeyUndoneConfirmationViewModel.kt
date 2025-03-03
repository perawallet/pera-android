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

package com.algorand.android.modules.rekey.undorekey.previousrekeyundoneconfirmation.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.algorand.android.core.BaseViewModel
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.rekey.undorekey.resultinfo.ui.model.PreviousRekeyUndoneConfirmationPreview
import com.algorand.android.utils.emptyString
import com.algorand.android.utils.launchIO
import com.algorand.wallet.account.info.domain.usecase.GetAccountRekeyAdminAddress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PreviousRekeyUndoneConfirmationViewModel @Inject constructor(
    private val getAccountRekeyAdminAddress: GetAccountRekeyAdminAddress,
    private val getAccountDisplayName: GetAccountDisplayName,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val _undoRekeyVerifyInfoPreviewFlow = MutableStateFlow<PreviousRekeyUndoneConfirmationPreview?>(null)
    val undoRekeyVerifyInfoPreviewFlow = _undoRekeyVerifyInfoPreviewFlow.asStateFlow()

    private val navArgs = PreviousRekeyUndoneConfirmationBottomSheetArgs.fromSavedStateHandle(savedStateHandle)
    private val accountAddress = navArgs.accountAddress

    init {
        getAccountNames()
    }

    private fun getAccountNames() {
        viewModelScope.launchIO {
            val accountDisplayName = getAccountDisplayName(accountAddress).primaryDisplayName
            val rekeyAdminAddress = getAccountRekeyAdminAddress(accountAddress)
            val authAccountDisplayName = rekeyAdminAddress?.let {
                getAccountDisplayName(it).primaryDisplayName
            } ?: emptyString()

            _undoRekeyVerifyInfoPreviewFlow.update {
                PreviousRekeyUndoneConfirmationPreview(accountDisplayName, authAccountDisplayName)
            }
        }
    }
}
