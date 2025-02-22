package com.algorand.android.modules.onboarding.recoverypassphrase.info.ui

import androidx.lifecycle.ViewModel
import com.algorand.android.usecase.IsOnHdWalletUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecoveryAccountInfoViewModel @Inject constructor(
    private val isOnHdWalletUseCase: IsOnHdWalletUseCase,
) : ViewModel() {

    fun isHdWalletToggleEnabled(): Boolean {
        return isOnHdWalletUseCase.invoke()
    }
}
