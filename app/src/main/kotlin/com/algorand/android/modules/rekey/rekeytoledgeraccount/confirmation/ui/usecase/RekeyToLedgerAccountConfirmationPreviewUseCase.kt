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

package com.algorand.android.modules.rekey.rekeytoledgeraccount.confirmation.ui.usecase

import com.algorand.android.R
import com.algorand.android.models.SignedTransactionDetail
import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.modules.accounticon.ui.mapper.AccountIconDrawablePreviewMapper
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.android.modules.rekey.domain.usecase.SendSignedTransactionUseCase
import com.algorand.android.modules.rekey.rekeytoledgeraccount.confirmation.ui.decider.RekeyToLedgerAccountConfirmationPreviewDecider
import com.algorand.android.modules.rekey.rekeytoledgeraccount.confirmation.ui.mapper.RekeyToLedgerAccountConfirmationPreviewMapper
import com.algorand.android.modules.rekey.rekeytoledgeraccount.confirmation.ui.model.RekeyToLedgerAccountConfirmationPreview
import com.algorand.android.repository.TransactionsRepository
import com.algorand.android.utils.Event
import com.algorand.android.utils.MIN_FEE
import com.algorand.android.utils.calculateRekeyFee
import com.algorand.android.utils.emptyString
import com.algorand.android.utils.formatAsAlgoAmount
import com.algorand.android.utils.formatAsAlgoString
import com.algorand.android.utils.toShortenedAddress
import com.algorand.wallet.account.info.domain.usecase.GetAccountRekeyAdminAddress
import com.algorand.wallet.account.local.domain.usecase.IsThereAnyAccountWithAddress
import javax.inject.Inject
import kotlinx.coroutines.flow.flow

@SuppressWarnings("LongParameterList")
class RekeyToLedgerAccountConfirmationPreviewUseCase @Inject constructor(
    private val rekeyToLedgerAccountConfirmationPreviewMapper: RekeyToLedgerAccountConfirmationPreviewMapper,
    private val transactionsRepository: TransactionsRepository,
    private val sendSignedTransactionUseCase: SendSignedTransactionUseCase,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    private val rekeyToLedgerAccountConfirmationPreviewDecider: RekeyToLedgerAccountConfirmationPreviewDecider,
    private val accountIconDrawablePreviewMapper: AccountIconDrawablePreviewMapper,
    private val isThereAnyAccountWithAddress: IsThereAnyAccountWithAddress,
    private val getAccountRekeyAdminAddress: GetAccountRekeyAdminAddress
) {

    suspend fun getInitialRekeyToStandardAccountConfirmationPreview(
        accountAddress: String,
        authAccountAddress: String
    ): RekeyToLedgerAccountConfirmationPreview {
        val rekeyAdminAddress = getAccountRekeyAdminAddress(accountAddress)
        val accountDisplayName = getAccountDisplayName(accountAddress)
        val accountIconResource = getAccountIconDrawablePreview(accountAddress)
        val (authAccountDisplayName, authAccountIconResource) = createAccountDisplayNameAndDrawablePair(
            accountAddress = authAccountAddress
        )

        val currentlyRekeyedAccountDisplayName = rekeyAdminAddress?.let { getAccountDisplayName(it) }
        val currentlyRekeyAccountIconDrawable = rekeyAdminAddress?.let { getAccountIconDrawablePreview(it) }

        return rekeyToLedgerAccountConfirmationPreviewMapper.mapToRekeyToLedgerAccountConfirmationPreview(
            isLoading = false,
            descriptionAnnotatedString = rekeyToLedgerAccountConfirmationPreviewDecider
                .decideDescriptionAnnotatedString(rekeyAdminAddress != null),
            rekeyedAccountDisplayName = accountDisplayName,
            rekeyedAccountIconResource = accountIconResource,
            authAccountDisplayName = authAccountDisplayName,
            authAccountIconResource = authAccountIconResource,
            currentlyRekeyedAccountDisplayName = currentlyRekeyedAccountDisplayName,
            currentlyRekeyedAccountIconDrawable = currentlyRekeyAccountIconDrawable,
            formattedTransactionFee = emptyString(),
            titleTextResId = R.string.confirm_rekeying,
            subtitleTextResId = R.string.summary_of_rekey
        )
    }

    fun updatePreviewWithTransactionFee(preview: RekeyToLedgerAccountConfirmationPreview) = flow {
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

    fun sendRekeyToLedgerAccountTransaction(
        preview: RekeyToLedgerAccountConfirmationPreview,
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

    fun updatePreviewWithLoadingState(
        preview: RekeyToLedgerAccountConfirmationPreview
    ): RekeyToLedgerAccountConfirmationPreview {
        return preview.copy(isLoading = true)
    }

    fun updatePreviewWithClearLoadingState(
        preview: RekeyToLedgerAccountConfirmationPreview
    ): RekeyToLedgerAccountConfirmationPreview {
        return preview.copy(isLoading = false)
    }

    suspend fun updatePreviewWithRekeyConfirmationClick(
        accountAddress: String,
        preview: RekeyToLedgerAccountConfirmationPreview
    ): RekeyToLedgerAccountConfirmationPreview {
        val rekeyAdminAddress = getAccountRekeyAdminAddress(accountAddress)
        return if (rekeyAdminAddress != null) {
            preview.copy(navToRekeyedAccountConfirmationBottomSheetEvent = Event(Unit))
        } else {
            preview.copy(onSendTransactionEvent = Event(Unit))
        }
    }

    private suspend fun createAccountDisplayNameAndDrawablePair(
        accountAddress: String
    ): Pair<AccountDisplayName, AccountIconDrawablePreview> {
        val isThereAnyAccountWithAddress = isThereAnyAccountWithAddress(accountAddress)
        return if (isThereAnyAccountWithAddress) {
            val accountDisplayName = getAccountDisplayName(accountAddress)
            val accountIconDrawablePreview = getAccountIconDrawablePreview(accountAddress)
            accountDisplayName to accountIconDrawablePreview
        } else {
            val accountDisplayName = AccountDisplayName(
                accountAddress = accountAddress,
                primaryDisplayName = accountAddress.toShortenedAddress(),
                secondaryDisplayName = accountAddress.toShortenedAddress()
            )
            val accountIconDrawablePreview = accountIconDrawablePreviewMapper.mapToAccountIconDrawablePreview(
                backgroundColorResId = R.color.wallet_3,
                iconTintResId = R.color.wallet_3_icon,
                iconResId = R.drawable.ic_ledger
            )
            accountDisplayName to accountIconDrawablePreview
        }
    }
}
