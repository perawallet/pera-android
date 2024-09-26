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

package com.algorand.android.ui.accountoptions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.algorand.android.core.BaseViewModel
import com.algorand.android.module.account.core.component.domain.usecase.DeleteAccount
import com.algorand.android.database.NotificationFilterDao
import com.algorand.android.module.notification.domain.usecase.SetNotificationFilter
import com.algorand.android.usecase.SecurityUseCase
import com.algorand.android.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AccountOptionsViewModel @Inject constructor(
    private val notificationFilterDao: NotificationFilterDao,
    private val deleteAccount: DeleteAccount,
    private val securityUseCase: SecurityUseCase,
    private val accountOptionsPreviewUseCase: AccountOptionsPreviewUseCase,
    private val setNotificationFilter: SetNotificationFilter,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val publicKey by lazy { savedStateHandle.get<String>(ACCOUNT_PUBLIC_KEY).orEmpty() }

    val notificationFilterOperationFlow = MutableStateFlow<Resource<Unit>?>(null)
    val notificationFilterCheckFlow = MutableStateFlow<Boolean?>(null)
    private val _accountOptionsPreviewFlow = MutableStateFlow<AccountOptionsPreview?>(null)
    val accountOptionsPreviewFlow: StateFlow<AccountOptionsPreview?>
        get() = _accountOptionsPreviewFlow

    init {
        checkIfNotificationFiltered()
        initAccountOptionsPreview()
    }

    private fun initAccountOptionsPreview() {
        viewModelScope.launch(Dispatchers.IO) {
            accountOptionsPreviewUseCase.getPreview(publicKey)?.let {
                _accountOptionsPreviewFlow.value = it
            }
        }
    }

    private fun checkIfNotificationFiltered() {
        viewModelScope.launch(Dispatchers.IO) {
            notificationFilterCheckFlow.value =
                notificationFilterDao.getNotificationFilterForUser(publicKey).isNotEmpty()
        }
    }

    fun startFilterOperation(isFiltered: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            notificationFilterOperationFlow.value = Resource.Loading
            notificationFilterOperationFlow.value = setNotificationFilter(publicKey, isFiltered).use(
                onSuccess = {
                    Resource.Success(Unit)
                },
                onFailed = { exception, _ ->
                    Resource.Error.Api(exception)
                }
            )
        }
    }

    fun getAccountAddress(): String {
        return publicKey
    }

    fun canDisplayPassphrases(): Boolean {
        return _accountOptionsPreviewFlow.value?.isPassphraseButtonVisible == true
    }

    fun getAccountName(): String {
        return _accountOptionsPreviewFlow.value?.accountDisplayName?.primaryDisplayName.orEmpty()
    }

    fun removeAccount(address: String) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteAccount(address)
        }
    }

    fun isPinCodeEnabled(): Boolean {
        return securityUseCase.isPinCodeEnabled()
    }

    companion object {
        private const val ACCOUNT_PUBLIC_KEY = "publicKey"
    }
}
