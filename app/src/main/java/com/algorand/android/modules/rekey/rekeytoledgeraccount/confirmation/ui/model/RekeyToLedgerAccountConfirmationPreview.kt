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

package com.algorand.android.modules.rekey.rekeytoledgeraccount.confirmation.ui.model

import com.algorand.android.accountcore.ui.model.AccountDisplayName
import com.algorand.android.accountcore.ui.model.AccountIconDrawablePreview
import com.algorand.android.accountinfo.component.domain.model.AccountInformation
import com.algorand.android.designsystem.AnnotatedString
import com.algorand.android.foundation.Event
import com.algorand.android.modules.rekey.baserekeyconfirmation.ui.model.BaseRekeyConfirmationFields

data class RekeyToLedgerAccountConfirmationPreview(
    override val isLoading: Boolean,
    override val titleTextResId: Int,
    override val descriptionAnnotatedString: AnnotatedString,
    override val subtitleTextResId: Int,
    override val rekeyedAccountDisplayName: AccountDisplayName,
    override val rekeyedAccountIconResource: AccountIconDrawablePreview,
    override val authAccountDisplayName: AccountDisplayName,
    override val authAccountIconResource: AccountIconDrawablePreview,
    override val currentlyRekeyedAccountDisplayName: AccountDisplayName?,
    override val currentlyRekeyedAccountIconDrawable: AccountIconDrawablePreview?,
    override val formattedTransactionFee: String?,
    override val navToRekeyResultInfoFragmentEvent: Event<Unit>?,
    override val showGlobalErrorEvent: Event<Pair<Int, String>>?,
    override val navToRekeyedAccountConfirmationBottomSheetEvent: Event<Unit>?,
    override val onSendTransactionEvent: Event<Unit>?,
    override val accountInformation: AccountInformation?
) : BaseRekeyConfirmationFields
