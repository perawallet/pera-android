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

package com.algorand.android.modules.addaccount.intro.mapper

import com.algorand.android.modules.addaccount.intro.domain.model.AddAccountIntroPreview
import javax.inject.Inject

class AddAccountIntroPreviewMapper @Inject constructor(
    private val addAccountIntroPreviewDecider: AddAccountIntroPreviewDecider
) {

    operator fun invoke(
        isShowingCloseButton: Boolean,
        hasHdWallet: Boolean,
        hasLocalAccount: Boolean
    ): AddAccountIntroPreview {
        return AddAccountIntroPreview(
            titleRes = addAccountIntroPreviewDecider.decideTitleRes(hasLocalAccount),
            isSkipButtonVisible = addAccountIntroPreviewDecider.decideIsSkipButtonVisible(hasLocalAccount),
            isCloseButtonVisible = isShowingCloseButton,
            hasHdWallet = hasHdWallet
        )
    }
}
