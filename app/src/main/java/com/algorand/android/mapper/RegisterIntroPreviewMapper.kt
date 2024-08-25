/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.algorand.android.mapper

import com.algorand.android.R
import com.algorand.android.models.RegisterIntroPreview
import javax.inject.Inject

class RegisterIntroPreviewMapper @Inject constructor() {

    fun mapTo(
        isSkipButtonVisible: Boolean,
        isCloseButtonVisible: Boolean,
        hasAccount: Boolean
    ): RegisterIntroPreview {
        val titleRes = if (hasAccount) R.string.add_an_account else R.string.welcome_to_pera
        return RegisterIntroPreview(
            titleRes = titleRes,
            isSkipButtonVisible = isSkipButtonVisible,
            isCloseButtonVisible = isCloseButtonVisible
        )
    }
}
