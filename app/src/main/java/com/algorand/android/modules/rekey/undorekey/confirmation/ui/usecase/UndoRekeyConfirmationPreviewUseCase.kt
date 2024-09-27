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

package com.algorand.android.modules.rekey.undorekey.confirmation.ui.usecase

import com.algorand.android.R
import com.algorand.android.module.account.core.ui.usecase.GetAccountDisplayName
import com.algorand.android.module.account.core.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.module.account.info.domain.usecase.GetAccountInformation
import com.algorand.android.module.drawable.AnnotatedString
import com.algorand.android.foundation.Event
import com.algorand.android.modules.accounticon.ui.usecase.CreateAccountOriginalStateIconDrawableUseCase
import com.algorand.android.modules.rekey.undorekey.confirmation.ui.mapper.UndoRekeyConfirmationPreviewMapper
import com.algorand.android.modules.rekey.undorekey.confirmation.ui.model.UndoRekeyConfirmationPreview
import com.algorand.android.module.transaction.component.domain.model.SignedTransaction
import com.algorand.android.module.transaction.component.domain.usecase.CalculateRekeyFee
import com.algorand.android.module.transaction.component.domain.usecase.SendSignedTransaction
import com.algorand.android.utils.emptyString
import com.algorand.android.utils.formatAsAlgoAmount
import com.algorand.android.utils.formatAsAlgoString
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UndoRekeyConfirmationPreviewUseCase @Inject constructor(
    private val undoRekeyConfirmationPreviewMapper: UndoRekeyConfirmationPreviewMapper,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    private val getAccountInformation: GetAccountInformation,
    private val createAccountOriginalStateIconDrawableUseCase: CreateAccountOriginalStateIconDrawableUseCase,
    private val calculateRekeyFee: CalculateRekeyFee,
    private val sendSignedTransaction: SendSignedTransaction
) {

    suspend fun getInitialUndoRekeyConfirmationPreview(accountAddress: String): UndoRekeyConfirmationPreview {
        val accountDisplayName = getAccountDisplayName(accountAddress)
        val authAccountAddress = getAccountInformation(accountAddress)?.rekeyAdminAddress.orEmpty()
        val authAccountDisplayName = getAccountDisplayName(authAccountAddress)
        val authAccountIconDrawablePreview = getAccountIconDrawablePreview(authAccountAddress)

        return undoRekeyConfirmationPreviewMapper.mapToUndoRekeyConfirmationPreview(
            isLoading = false,
            descriptionAnnotatedString = AnnotatedString(stringResId = R.string.you_are_about_to_undo_this),
            rekeyedAccountDisplayName = accountDisplayName,
            rekeyedAccountIconResource = getAccountIconDrawablePreview(accountAddress),
            authAccountDisplayName = accountDisplayName,
            authAccountIconResource = createAccountOriginalStateIconDrawableUseCase.invoke(accountAddress),
            currentlyRekeyedAccountDisplayName = authAccountDisplayName,
            currentlyRekeyedAccountIconDrawable = authAccountIconDrawablePreview,
            formattedTransactionFee = emptyString(),
            titleTextResId = R.string.undo_rekey,
            subtitleTextResId = R.string.undo_rekey,
            accountInformation = getAccountInformation(accountAddress)
        )
    }

    suspend fun updatePreviewWithTransactionFee(preview: UndoRekeyConfirmationPreview) = flow {
        val rekeyFee = calculateRekeyFee()
        val formattedFee = rekeyFee.formatAsAlgoString().formatAsAlgoAmount()
        emit(preview.copy(formattedTransactionFee = formattedFee))
    }

    fun sendSignedTransaction(
        preview: UndoRekeyConfirmationPreview,
        signedTransaction: SignedTransaction
    ): Flow<UndoRekeyConfirmationPreview> = flow {
        emit(preview.copy(isLoading = true))
        sendSignedTransaction(signedTransaction, false).use(
            onSuccess = {
                emit(
                    preview.copy(
                        isLoading = false,
                        navToRekeyResultInfoFragmentEvent = Event(Unit)
                    )
                )
            },
            onFailed = { exception, _ ->
                val title = R.string.error
                val description = exception.message.orEmpty()
                emit(preview.copy(showGlobalErrorEvent = Event(title to description), isLoading = false))
            }
        )
    }
}
