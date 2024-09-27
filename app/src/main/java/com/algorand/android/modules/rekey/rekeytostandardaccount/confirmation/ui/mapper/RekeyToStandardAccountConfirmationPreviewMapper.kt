/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.rekey.rekeytostandardaccount.confirmation.ui.mapper

import com.algorand.android.module.account.core.ui.model.AccountDisplayName
import com.algorand.android.module.account.core.ui.model.AccountIconDrawablePreview
import com.algorand.android.module.account.info.domain.model.AccountInformation
import com.algorand.android.module.drawable.AnnotatedString
import com.algorand.android.foundation.Event
import com.algorand.android.modules.rekey.rekeytostandardaccount.confirmation.ui.model.RekeyToStandardAccountConfirmationPreview
import javax.inject.Inject

class RekeyToStandardAccountConfirmationPreviewMapper @Inject constructor() {

    @SuppressWarnings("LongParameterList")
    fun mapToRekeyToStandardAccountConfirmationPreview(
        isLoading: Boolean,
        titleTextResId: Int,
        descriptionAnnotatedString: AnnotatedString,
        subtitleTextResId: Int,
        rekeyedAccountDisplayName: AccountDisplayName,
        rekeyedAccountIconResource: AccountIconDrawablePreview,
        authAccountDisplayName: AccountDisplayName,
        authAccountIconResource: AccountIconDrawablePreview,
        currentlyRekeyedAccountDisplayName: AccountDisplayName?,
        currentlyRekeyedAccountIconDrawable: AccountIconDrawablePreview?,
        formattedTransactionFee: String?,
        navToRekeyResultInfoFragmentEvent: Event<Unit>? = null,
        showGlobalErrorEvent: Event<Pair<Int, String>>? = null,
        navToRekeyedAccountConfirmationBottomSheetEvent: Event<Unit>? = null,
        onSendTransactionEvent: Event<Unit>? = null,
        accountInformation: AccountInformation?
    ): RekeyToStandardAccountConfirmationPreview {
        return RekeyToStandardAccountConfirmationPreview(
            isLoading = isLoading,
            titleTextResId = titleTextResId,
            descriptionAnnotatedString = descriptionAnnotatedString,
            subtitleTextResId = subtitleTextResId,
            rekeyedAccountDisplayName = rekeyedAccountDisplayName,
            rekeyedAccountIconResource = rekeyedAccountIconResource,
            authAccountDisplayName = authAccountDisplayName,
            authAccountIconResource = authAccountIconResource,
            currentlyRekeyedAccountDisplayName = currentlyRekeyedAccountDisplayName,
            currentlyRekeyedAccountIconDrawable = currentlyRekeyedAccountIconDrawable,
            formattedTransactionFee = formattedTransactionFee,
            navToRekeyResultInfoFragmentEvent = navToRekeyResultInfoFragmentEvent,
            showGlobalErrorEvent = showGlobalErrorEvent,
            navToRekeyedAccountConfirmationBottomSheetEvent = navToRekeyedAccountConfirmationBottomSheetEvent,
            onSendTransactionEvent = onSendTransactionEvent,
            accountInformation = accountInformation
        )
    }
}
