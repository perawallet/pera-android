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

import androidx.annotation.StringRes
import com.algorand.android.R
import javax.inject.Inject

class AddAccountIntroPreviewDecider @Inject constructor() {

    @StringRes
    fun decideTitleRes(hasLocalAccount: Boolean): Int {
        return if (hasLocalAccount) {
            R.string.add_an_account
        } else {
            R.string.welcome_to_pera
        }
    }

    fun decideIsSkipButtonVisible(hasLocalAccount: Boolean): Boolean {
        return !hasLocalAccount
    }
}
