package com.algorand.android.modules.onboarding.recoverypassphrase.info.ui

import com.algorand.android.core.BaseViewModel
import com.algorand.android.usecase.IsOnHdWalletUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecoveryAccountInfoViewModel @Inject constructor(
    private val isOnHdWalletUseCase: IsOnHdWalletUseCase,
) : BaseViewModel() {

    fun isHdWalletToggleEnabled(): Boolean {
        return isOnHdWalletUseCase.invoke()
    }
}
