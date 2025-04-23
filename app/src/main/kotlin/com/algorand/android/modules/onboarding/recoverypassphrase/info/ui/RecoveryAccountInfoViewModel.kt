package com.algorand.android.modules.onboarding.recoverypassphrase.info.ui

import com.algorand.android.core.BaseViewModel
import com.algorand.android.models.OnboardingAccountType
import com.algorand.android.modules.tracking.core.PeraClickEvent
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

    fun logRecoverAccountTypeClickEvent(onboardingAccountType: OnboardingAccountType) {
        val clickEvent = when (onboardingAccountType) {
            OnboardingAccountType.HdKey -> PeraClickEvent.TAP_ONBOARDING_RECOVER_UNIVERSAL
            OnboardingAccountType.Algo25 -> PeraClickEvent.TAP_ONBOARDING_RECOVER_ALGO25
        }
        logEvent(clickEvent)
    }
}
