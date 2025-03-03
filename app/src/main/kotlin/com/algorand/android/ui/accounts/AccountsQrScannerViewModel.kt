package com.algorand.android.ui.accounts

import com.algorand.android.core.BaseViewModel
import com.algorand.android.usecase.IsAccountLimitExceedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AccountsQrScannerViewModel @Inject constructor(
    private val isAccountLimitExceedUseCase: IsAccountLimitExceedUseCase
) : BaseViewModel() {

    fun isAccountLimitExceed(): Boolean {
        return isAccountLimitExceedUseCase.isAccountLimitExceed()
    }
}
