/*
 * Copyright 2025 Pera Wallet, LDA
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
import com.algorand.android.models.AnnotatedString
import com.algorand.android.models.SignedTransactionDetail
import com.algorand.android.models.TransactionSignData
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.rekey.domain.usecase.SendSignedTransactionUseCase
import com.algorand.android.modules.rekey.undorekey.confirmation.ui.mapper.UndoRekeyConfirmationPreviewMapper
import com.algorand.android.modules.rekey.undorekey.confirmation.ui.model.UndoRekeyConfirmationPreview
import com.algorand.android.modules.transaction.refactor.usecase.CreateRekeyTransactionData
import com.algorand.android.repository.TransactionsRepository
import com.algorand.android.utils.Event
import com.algorand.android.utils.MIN_FEE
import com.algorand.android.utils.calculateRekeyFee
import com.algorand.android.utils.emptyString
import com.algorand.android.utils.formatAsAlgoAmount
import com.algorand.android.utils.formatAsAlgoString
import com.algorand.wallet.account.detail.domain.model.AccountType.Rekeyed
import com.algorand.wallet.account.detail.domain.model.AccountType.RekeyedAuth
import com.algorand.wallet.account.detail.domain.usecase.GetAccountType
import com.algorand.wallet.account.detail.domain.usecase.IsAccountRekeyedToAnotherAccount
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountOriginalStateIconDrawablePreview
import com.algorand.wallet.account.info.domain.usecase.GetAccountRekeyAdminAddress
import javax.inject.Inject
import kotlinx.coroutines.flow.flow

@Suppress("LongParameterList")
class UndoRekeyConfirmationPreviewUseCase @Inject constructor(
    private val undoRekeyConfirmationPreviewMapper: UndoRekeyConfirmationPreviewMapper,
    private val isAccountRekeyedToAnotherAccount: IsAccountRekeyedToAnotherAccount,
    private val getAccountRekeyAdminAddress: GetAccountRekeyAdminAddress,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    private val getAccountOriginalStateIconDrawablePreview: GetAccountOriginalStateIconDrawablePreview,
    private val getAccountType: GetAccountType,
    private val transactionsRepository: TransactionsRepository,
    private val sendSignedTransactionUseCase: SendSignedTransactionUseCase,
    private val createRekeyTransactionData: CreateRekeyTransactionData,
) {

    suspend fun getInitialUndoRekeyConfirmationPreview(accountAddress: String): UndoRekeyConfirmationPreview {
        val accountDisplayName = getAccountDisplayName(accountAddress)

        val authAccountAddress = getAccountRekeyAdminAddress(accountAddress).orEmpty()
        val authAccountDisplayName = getAccountDisplayName(authAccountAddress)
        val authAccountIconDrawablePreview = getAccountIconDrawablePreview(authAccountAddress)

        return undoRekeyConfirmationPreviewMapper.mapToUndoRekeyConfirmationPreview(
            isLoading = false,
            descriptionAnnotatedString = AnnotatedString(stringResId = R.string.you_are_about_to_undo_this),
            rekeyedAccountDisplayName = accountDisplayName,
            rekeyedAccountIconResource = getAccountIconDrawablePreview(accountAddress),
            authAccountDisplayName = accountDisplayName,
            authAccountIconResource = getAccountOriginalStateIconDrawablePreview(accountAddress),
            currentlyRekeyedAccountDisplayName = authAccountDisplayName,
            currentlyRekeyedAccountIconDrawable = authAccountIconDrawablePreview,
            formattedTransactionFee = emptyString(),
            titleTextResId = R.string.undo_rekey,
            subtitleTextResId = R.string.undo_rekey
        )
    }

    suspend fun updatePreviewWithAccountIcon(
        accountAddress: String,
        preview: UndoRekeyConfirmationPreview
    ): UndoRekeyConfirmationPreview {
        return preview.copy(
            rekeyedAccountIconResource = getAccountIconDrawablePreview(accountAddress)
        )
    }

    fun updatePreviewWithTransactionFee(preview: UndoRekeyConfirmationPreview) = flow {
        transactionsRepository.getTransactionParams().use(
            onSuccess = { params ->
                val calculatedFee = calculateRekeyFee(params.fee, params.minFee)
                val formattedFee = calculatedFee.formatAsAlgoString().formatAsAlgoAmount()
                emit(preview.copy(formattedTransactionFee = formattedFee))
            },
            onFailed = { _, _ ->
                val formattedFee = MIN_FEE.formatAsAlgoString().formatAsAlgoAmount()
                emit(preview.copy(formattedTransactionFee = formattedFee))
            }
        )
    }

    fun sendUndoRekeyTransaction(
        preview: UndoRekeyConfirmationPreview,
        transactionDetail: SignedTransactionDetail
    ) = flow {
        emit(preview.copy(isLoading = true))
        sendSignedTransactionUseCase.invoke(transactionDetail).useSuspended(
            onSuccess = {
                emit(
                    preview.copy(
                        isLoading = false,
                        navToRekeyResultInfoFragmentEvent = Event(Unit)
                    )
                )
            },
            onFailed = {
                val title = R.string.error
                val description = it.exception?.message.orEmpty()
                emit(preview.copy(showGlobalErrorEvent = Event(title to description), isLoading = false))
            }
        )
    }

    suspend fun createUndoRekeyTransaction(accountAddress: String): TransactionSignData.Rekey? {
        val accountType = getAccountType(accountAddress)
        return when (accountType) {
            Rekeyed, RekeyedAuth -> createRekeyTransactionData(accountAddress, accountAddress)
            else -> null
        }
    }

    fun updatePreviewWithLoadingState(preview: UndoRekeyConfirmationPreview): UndoRekeyConfirmationPreview {
        return preview.copy(isLoading = true)
    }

    fun updatePreviewWithClearLoadingState(preview: UndoRekeyConfirmationPreview): UndoRekeyConfirmationPreview {
        return preview.copy(isLoading = false)
    }

    suspend fun updatePreviewWithRekeyConfirmationClick(
        accountAddress: String,
        preview: UndoRekeyConfirmationPreview
    ): UndoRekeyConfirmationPreview {
        val isRekeyed = isAccountRekeyedToAnotherAccount(accountAddress)
        return if (isRekeyed) {
            preview.copy(navToRekeyedAccountConfirmationBottomSheetEvent = Event(Unit))
        } else {
            preview.copy(onSendTransactionEvent = Event(Unit))
        }
    }
}
