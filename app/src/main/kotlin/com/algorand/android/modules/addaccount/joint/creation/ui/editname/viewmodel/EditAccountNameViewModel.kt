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

package com.algorand.android.modules.addaccount.joint.creation.ui.editname.viewmodel

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.modules.addaccount.joint.creation.model.SelectedJointAccountItem
import com.algorand.android.repository.ContactRepository
import com.algorand.android.utils.toShortenedAddress
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditAccountNameViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val stateDelegate: StateDelegate<ViewState>,
    private val contactRepository: ContactRepository,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview
) : ViewModel(),
    StateViewModel<EditAccountNameViewModel.ViewState> by stateDelegate {

    private val accountAddress: String = savedStateHandle.get<String>(ACCOUNT_ADDRESS_KEY).orEmpty()

    init {
        stateDelegate.setDefaultState(ViewState.Loading)
        loadAccountInfo()
    }

    private fun loadAccountInfo() {
        viewModelScope.launch {
            val contact = contactRepository.getContactByAddress(accountAddress)
            val imageUri = contact?.imageUriAsString?.let { Uri.parse(it) }
            val iconDrawablePreview = getAccountIconDrawablePreview(accountAddress)

            val account = SelectedJointAccountItem(
                accountDisplayName = AccountDisplayName(
                    accountAddress = accountAddress,
                    primaryDisplayName = contact?.name ?: accountAddress.toShortenedAddress(),
                    secondaryDisplayName = accountAddress.toShortenedAddress()
                ),
                iconDrawablePreview = iconDrawablePreview,
                imageUri = imageUri,
                isContact = contact != null
            )
            stateDelegate.updateState { ViewState.Content(account) }
        }
    }

    sealed interface ViewState {
        data object Loading : ViewState
        data class Content(val account: SelectedJointAccountItem) : ViewState
    }

    companion object {
        private const val ACCOUNT_ADDRESS_KEY = "accountAddress"
    }
}
