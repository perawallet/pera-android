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

package com.algorand.wallet.devoptions.domain.model

import com.algorand.wallet.remoteconfig.domain.model.FeatureToggle

data class DeveloperOptionFeatureFlag(
    val featureToggle: FeatureToggle,
    val remoteValue: Boolean,
    val status: Status
) {

    val isEnabled: Boolean
        get() = when (status) {
            is Status.Overridden -> status.isEnabled
            Status.Remote -> remoteValue
        }

    sealed interface Status {
        data object Remote : Status
        data class Overridden(val isEnabled: Boolean) : Status
    }
}
