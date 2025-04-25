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

package com.algorand.android.usecase

import com.algorand.wallet.remoteconfig.domain.usecase.HD_WALLET_BUTTON_TOGGLE
import com.algorand.wallet.remoteconfig.domain.usecase.IsFeatureToggleEnabled
import javax.inject.Inject

class IsOnHdWalletUseCase @Inject constructor(
    private val getIsProductionReleaseUseCase: GetIsProductionReleaseUseCase,
    private val getIsActiveNodeTestnetUseCase: GetIsActiveNodeTestnetUseCase,
    private val isFeatureToggleEnabled: IsFeatureToggleEnabled,
) {

    operator fun invoke(): Boolean {
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
