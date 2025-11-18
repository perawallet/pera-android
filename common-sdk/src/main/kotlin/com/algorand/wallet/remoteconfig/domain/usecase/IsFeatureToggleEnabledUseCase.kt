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

package com.algorand.wallet.remoteconfig.domain.usecase

import com.algorand.wallet.devoptions.domain.usecase.GetOverriddenFeatureFlagStatus
import com.algorand.wallet.devoptions.domain.usecase.IsDeveloperOptionsEnabled
import com.algorand.wallet.remoteconfig.domain.repository.FeatureToggleRepository
import javax.inject.Inject

internal class IsFeatureToggleEnabledUseCase @Inject constructor(
    private val featureToggleRepository: FeatureToggleRepository,
    private val isDeveloperOptionsEnabled: IsDeveloperOptionsEnabled,
    private val getOverriddenFeatureFlagStatus: GetOverriddenFeatureFlagStatus
) : IsFeatureToggleEnabled {

    override fun invoke(featureToggleKey: String): Boolean {
        return if (isDeveloperOptionsEnabled()) {
            val overriddenStatus = getOverriddenFeatureFlagStatus(featureToggleKey)
            overriddenStatus ?: featureToggleRepository.isFeatureEnabled(featureToggleKey)
        } else {
            featureToggleRepository.isFeatureEnabled(featureToggleKey)
        }
    }
}
