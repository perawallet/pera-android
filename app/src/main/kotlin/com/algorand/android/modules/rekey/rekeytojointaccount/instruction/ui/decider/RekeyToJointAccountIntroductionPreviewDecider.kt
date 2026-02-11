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

package com.algorand.android.modules.rekey.rekeytojointaccount.instruction.ui.decider

import com.algorand.android.R
import com.algorand.android.models.AnnotatedString
import com.algorand.wallet.account.detail.domain.model.AccountType
import javax.inject.Inject

class RekeyToJointAccountIntroductionPreviewDecider @Inject constructor() {

    fun decideBannerDrawableResId(accountType: AccountType?): Int {
        return R.drawable.ic_rekey_from_rekeyed_banner
    }

    fun decideDescriptionAnnotatedString(accountType: AccountType?): AnnotatedString? {
        return null
    }

    fun decideExpectationListItems(accountType: AccountType?): List<AnnotatedString> {
        return mutableListOf<AnnotatedString>().apply {
            add(AnnotatedString(stringResId = R.string.future_transactions_can_only))
            add(AnnotatedString(stringResId = R.string.your_account_s_public_key))
        }
    }
}
