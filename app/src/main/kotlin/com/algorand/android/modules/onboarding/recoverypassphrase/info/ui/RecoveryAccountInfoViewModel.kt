package com.algorand.android.modules.onboarding.recoverypassphrase.info.ui

import androidx.lifecycle.ViewModel
import com.algorand.android.usecase.GetIsActiveNodeTestnetUseCase
import com.algorand.android.usecase.GetIsProductionReleaseUseCase
import com.algorand.wallet.remoteconfig.domain.usecase.HD_WALLET_BUTTON_TOGGLE
import com.algorand.wallet.remoteconfig.domain.usecase.IsFeatureToggleEnabled
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecoveryAccountInfoViewModel @Inject constructor(
    private val getIsProductionReleaseUseCase: GetIsProductionReleaseUseCase,
    private val getIsActiveNodeTestnetUseCase: GetIsActiveNodeTestnetUseCase,
    private val isFeatureToggleEnabled: IsFeatureToggleEnabled
) : ViewModel() {

    fun isHdWalletToggleEnabled(): Boolean {
        val isHdWalletToggleEnabled = isFeatureToggleEnabled(HD_WALLET_BUTTON_TOGGLE) &&
                !isProdReleaseVariant()
        return isHdWalletToggleEnabled
    }

    fun isConnectedToTestnet(): Boolean {
        return getIsActiveNodeTestnetUseCase.invoke()
    }

    fun isProdReleaseVariant(): Boolean {
        return getIsProductionReleaseUseCase.invoke()
    }
}
