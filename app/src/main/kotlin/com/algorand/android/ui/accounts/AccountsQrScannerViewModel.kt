package com.algorand.android.ui.accounts

import androidx.lifecycle.viewModelScope
import com.algorand.android.core.BaseViewModel
import com.algorand.android.usecase.IsAccountLimitExceedUseCase
import com.algorand.android.utils.launchIO
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AccountsQrScannerViewModel @Inject constructor(
    private val isAccountLimitExceedUseCase: IsAccountLimitExceedUseCase,
    private val eventDelegate: EventDelegate<ViewEvent>
) : BaseViewModel(), EventViewModel<AccountsQrScannerViewModel.ViewEvent> by eventDelegate {

    fun onImportAccountDeepLink(mnemonic: String) {
        viewModelScope.launchIO {
            eventDelegate.sendEvent(
                if (isAccountLimitExceedUseCase.isAccountLimitExceed()) {
                    ViewEvent.ShowMaxAccountLimitExceededError
                } else {
                    ViewEvent.NavToRecoverWithPassphraseNavigation(mnemonic)
                }
            )
        }
    }

    sealed interface ViewEvent {
        data class NavToRecoverWithPassphraseNavigation(val mnemonic: String) : ViewEvent
        data object ShowMaxAccountLimitExceededError : ViewEvent
    }
}
