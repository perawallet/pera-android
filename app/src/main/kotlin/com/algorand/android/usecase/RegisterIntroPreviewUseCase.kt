/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.algorand.android.usecase

import com.algorand.android.mapper.RegisterIntroPreviewMapper
import com.algorand.android.modules.tracking.onboarding.register.registerintro.RegisterIntroFragmentEventTracker
import com.algorand.wallet.account.local.domain.usecase.GetHasAnyHdSeedId
import com.algorand.wallet.account.local.domain.usecase.IsThereAnyLocalAccount
import javax.inject.Inject
import kotlinx.coroutines.flow.flow

class RegisterIntroPreviewUseCase @Inject constructor(
    private val registerIntroPreviewMapper: RegisterIntroPreviewMapper,
    private val registerIntroFragmentEventTracker: RegisterIntroFragmentEventTracker,
    private val hasAnyHdSeedId: GetHasAnyHdSeedId,
    private val isThereAnyLocalAccount: IsThereAnyLocalAccount
) {

    fun getRegisterIntroPreview(isShowingCloseButton: Boolean) = flow {
        val hasHdWallet = hasAnyHdSeedId.invoke()
        val isSkipButtonVisible = !isShowingCloseButton
        val registerIntroPreview = registerIntroPreviewMapper.mapTo(
            isSkipButtonVisible = isSkipButtonVisible,
            isCloseButtonVisible = isShowingCloseButton,
            hasAccount = isThereAnyLocalAccount(),
            hasHdWallet = hasHdWallet
        )
        emit(registerIntroPreview)
    }

    suspend fun logOnboardingCreateNewAccountClickEvent() {
        registerIntroFragmentEventTracker.logOnboardingCreateNewAccountEventTracker()
    }

    suspend fun logOnboardingWelcomeAccountRecoverClickEvent() {
        registerIntroFragmentEventTracker.logOnboardingWelcomeAccountRecoverEvent()
    }
}
