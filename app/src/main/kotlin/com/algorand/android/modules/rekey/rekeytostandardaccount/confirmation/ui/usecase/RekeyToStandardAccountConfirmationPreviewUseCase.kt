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

package com.algorand.android.modules.rekey.rekeytostandardaccount.confirmation.ui.usecase

import com.algorand.android.R
import com.algorand.android.models.AnnotatedString
import com.algorand.android.models.SignedTransactionDetail
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.modules.rekey.domain.usecase.SendSignedTransactionUseCase
import com.algorand.android.modules.rekey.rekeytostandardaccount.confirmation.ui.mapper.RekeyToStandardAccountConfirmationPreviewMapper
import com.algorand.android.modules.rekey.rekeytostandardaccount.confirmation.ui.model.RekeyToStandardAccountConfirmationPreview
import com.algorand.android.repository.TransactionsRepository
import com.algorand.android.utils.Event
import com.algorand.android.utils.MIN_FEE
import com.algorand.android.utils.calculateRekeyFee
import com.algorand.android.utils.emptyString
import com.algorand.android.utils.formatAsAlgoAmount
import com.algorand.android.utils.formatAsAlgoString
import com.algorand.wallet.account.info.domain.usecase.GetAccountInformation
import javax.inject.Inject
import kotlinx.coroutines.flow.flow

class RekeyToStandardAccountConfirmationPreviewUseCase @Inject constructor(
    private val rekeyToStandardAccountConfirmationPreviewMapper: RekeyToStandardAccountConfirmationPreviewMapper,
    private val transactionsRepository: TransactionsRepository,
    private val sendSignedTransactionUseCase: SendSignedTransactionUseCase,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    private val getAccountInformation: GetAccountInformation
) {

    fun sendRekeyToStandardAccountTransaction(
        preview: RekeyToStandardAccountConfirmationPreview,
        transactionDetail: SignedTransactionDetail.RekeyOperation
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

    suspend fun getInitialRekeyToStandardAccountConfirmationPreview(
        accountAddress: String,
        authAccountAddress: String
    ): RekeyToStandardAccountConfirmationPreview {
        val accountInfo = getAccountInformation(accountAddress)
        val isAccountRekeyed = accountInfo?.isRekeyed() == true
        val accountDisplayName = getAccountDisplayName(accountAddress)

        val authAccountDisplayName = getAccountDisplayName(authAccountAddress)

        val currentlyRekeyedAccountDisplayName = if (isAccountRekeyed) {
            getAccountDisplayName(accountInfo?.rekeyAdminAddress.orEmpty())
        } else {
            null
        }
        val currentlyRekeyAccountIconDrawable = if (isAccountRekeyed) {
            getAccountIconDrawablePreview(accountInfo?.rekeyAdminAddress.orEmpty())
        } else {
            null
        }

        return rekeyToStandardAccountConfirmationPreviewMapper.mapToRekeyToStandardAccountConfirmationPreview(
            isLoading = false,
            descriptionAnnotatedString = getDescriptionAnnotatedString(isAccountRekeyed = isAccountRekeyed),
            rekeyedAccountDisplayName = accountDisplayName,
            rekeyedAccountIconResource = getAccountIconDrawablePreview(accountAddress),
            authAccountDisplayName = authAccountDisplayName,
            authAccountIconResource = getAccountIconDrawablePreview(authAccountAddress),
            currentlyRekeyedAccountDisplayName = currentlyRekeyedAccountDisplayName,
            currentlyRekeyedAccountIconDrawable = currentlyRekeyAccountIconDrawable,
            formattedTransactionFee = emptyString(),
            titleTextResId = R.string.confirm_rekeying,
            subtitleTextResId = R.string.summary_of_rekey
        )
    }

    suspend fun updatePreviewWithTransactionFee(preview: RekeyToStandardAccountConfirmationPreview) = flow {
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

    fun updatePreviewWithLoadingState(
        preview: RekeyToStandardAccountConfirmationPreview
    ): RekeyToStandardAccountConfirmationPreview {
        return preview.copy(isLoading = true)
    }

    fun updatePreviewWithClearLoadingState(
        preview: RekeyToStandardAccountConfirmationPreview
    ): RekeyToStandardAccountConfirmationPreview {
        return preview.copy(isLoading = false)
    }

    suspend fun updatePreviewWithRekeyConfirmationClick(
        accountAddress: String,
        preview: RekeyToStandardAccountConfirmationPreview
    ): RekeyToStandardAccountConfirmationPreview {
        val accountInfo = getAccountInformation(accountAddress) ?: return preview
        return if (accountInfo.isRekeyed()) {
            preview.copy(navToRekeyedAccountConfirmationBottomSheetEvent = Event(Unit))
        } else {
            preview.copy(onSendTransactionEvent = Event(Unit))
        }
    }

    private fun getDescriptionAnnotatedString(isAccountRekeyed: Boolean): AnnotatedString {
        return if (isAccountRekeyed) {
            AnnotatedString(R.string.you_are_about_to_rekey_this)
        } else {
            AnnotatedString(R.string.you_are_about_to_rekey)
        }
    }
}
