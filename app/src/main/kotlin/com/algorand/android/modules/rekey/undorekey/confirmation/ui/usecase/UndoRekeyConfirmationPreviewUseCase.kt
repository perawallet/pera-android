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
import com.algorand.android.models.AnnotatedString
import com.algorand.android.models.SignedTransactionDetail
import com.algorand.android.models.TransactionSignData
import com.algorand.android.modules.accounticon.ui.usecase.CreateAccountIconDrawableUseCase
import com.algorand.android.modules.accounticon.ui.usecase.CreateAccountOriginalStateIconDrawableUseCase
import com.algorand.android.modules.accounts.domain.usecase.AccountDisplayNameUseCase
import com.algorand.android.modules.rekey.domain.usecase.SendSignedTransactionUseCase
import com.algorand.android.modules.rekey.undorekey.confirmation.ui.mapper.UndoRekeyConfirmationPreviewMapper
import com.algorand.android.modules.rekey.undorekey.confirmation.ui.model.UndoRekeyConfirmationPreview
import com.algorand.android.modules.transaction.refactor.usecase.CreateRekeyTransactionData
import com.algorand.android.repository.TransactionsRepository
import com.algorand.android.usecase.AccountDetailUseCase
import com.algorand.android.utils.Event
import com.algorand.android.utils.MIN_FEE
import com.algorand.android.utils.calculateRekeyFee
import com.algorand.android.utils.emptyString
import com.algorand.android.utils.formatAsAlgoAmount
import com.algorand.android.utils.formatAsAlgoString
import com.algorand.wallet.account.detail.domain.model.AccountType.Rekeyed
import com.algorand.wallet.account.detail.domain.model.AccountType.RekeyedAuth
import com.algorand.wallet.account.detail.domain.usecase.GetAccountState
import javax.inject.Inject
import kotlinx.coroutines.flow.flow

class UndoRekeyConfirmationPreviewUseCase @Inject constructor(
    private val undoRekeyConfirmationPreviewMapper: UndoRekeyConfirmationPreviewMapper,
    private val accountDetailUseCase: AccountDetailUseCase,
    private val transactionsRepository: TransactionsRepository,
    private val sendSignedTransactionUseCase: SendSignedTransactionUseCase,
    private val accountDisplayNameUseCase: AccountDisplayNameUseCase,
    private val createAccountIconDrawableUseCase: CreateAccountIconDrawableUseCase,
    private val createAccountOriginalStateIconDrawableUseCase: CreateAccountOriginalStateIconDrawableUseCase,
    private val createRekeyTransactionData: CreateRekeyTransactionData,
    private val getAccountState: GetAccountState
) {

    fun getInitialUndoRekeyConfirmationPreview(accountAddress: String): UndoRekeyConfirmationPreview {
        val accountDetail = accountDetailUseCase.getCachedAccountDetail(accountAddress)?.data
        val accountDisplayName = accountDisplayNameUseCase.invoke(accountAddress)

        val authAccountAddress = accountDetail?.accountInformation?.rekeyAdminAddress.orEmpty()
        val authAccountDisplayName = accountDisplayNameUseCase.invoke(authAccountAddress)
        val authAccountIconDrawablePreview = createAccountIconDrawableUseCase.invoke(authAccountAddress)

        return undoRekeyConfirmationPreviewMapper.mapToUndoRekeyConfirmationPreview(
            isLoading = false,
            descriptionAnnotatedString = AnnotatedString(stringResId = R.string.you_are_about_to_undo_this),
            rekeyedAccountDisplayName = accountDisplayName,
            rekeyedAccountIconResource = createAccountIconDrawableUseCase.invoke(accountAddress),
            authAccountDisplayName = accountDisplayName,
            authAccountIconResource = createAccountOriginalStateIconDrawableUseCase.invoke(accountAddress),
            currentlyRekeyedAccountDisplayName = authAccountDisplayName,
            currentlyRekeyedAccountIconDrawable = authAccountIconDrawablePreview,
            formattedTransactionFee = emptyString(),
            titleTextResId = R.string.undo_rekey,
            subtitleTextResId = R.string.undo_rekey
        )
    }

    suspend fun updatePreviewWithTransactionFee(preview: UndoRekeyConfirmationPreview) = flow {
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
        val accountType = getAccountState(accountAddress).accountType
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

    fun updatePreviewWithRekeyConfirmationClick(
        accountAddress: String,
        preview: UndoRekeyConfirmationPreview
    ): UndoRekeyConfirmationPreview {
        val accountDetail = accountDetailUseCase.getCachedAccountDetail(accountAddress)?.data ?: return preview
        return if (accountDetail.accountInformation.isRekeyed()) {
            preview.copy(navToRekeyedAccountConfirmationBottomSheetEvent = Event(Unit))
        } else {
            preview.copy(onSendTransactionEvent = Event(Unit))
        }
    }

    fun getAccountAuthAddress(accountAddress: String): String {
        return accountDetailUseCase.getAuthAddress(accountAddress).orEmpty()
    }
}
