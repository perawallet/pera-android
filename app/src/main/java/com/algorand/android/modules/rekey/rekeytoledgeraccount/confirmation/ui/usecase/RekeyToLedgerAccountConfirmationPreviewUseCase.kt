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

package com.algorand.android.modules.rekey.rekeytoledgeraccount.confirmation.ui.usecase

import com.algorand.android.R
import com.algorand.android.account.localaccount.domain.usecase.IsThereAnyAccountWithAddress
import com.algorand.android.accountcore.ui.model.AccountDisplayName
import com.algorand.android.accountcore.ui.model.AccountIconDrawablePreview
import com.algorand.android.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.accountinfo.component.domain.model.AccountInformation
import com.algorand.android.accountinfo.component.domain.usecase.GetAccountInformation
import com.algorand.android.core.utils.toShortenedAddress
import com.algorand.android.foundation.Event
import com.algorand.android.modules.rekey.rekeytoledgeraccount.confirmation.ui.decider.RekeyToLedgerAccountConfirmationPreviewDecider
import com.algorand.android.modules.rekey.rekeytoledgeraccount.confirmation.ui.mapper.RekeyToLedgerAccountConfirmationPreviewMapper
import com.algorand.android.modules.rekey.rekeytoledgeraccount.confirmation.ui.model.RekeyToLedgerAccountConfirmationPreview
import com.algorand.android.module.transaction.component.domain.model.SignedTransaction
import com.algorand.android.module.transaction.component.domain.usecase.CalculateRekeyFee
import com.algorand.android.module.transaction.component.domain.usecase.SendSignedTransaction
import com.algorand.android.utils.formatAsAlgoAmount
import com.algorand.android.utils.formatAsAlgoString
import javax.inject.Inject
import kotlinx.coroutines.flow.flow

@SuppressWarnings("LongParameterList")
class RekeyToLedgerAccountConfirmationPreviewUseCase @Inject constructor(
    private val rekeyToLedgerAccountConfirmationPreviewMapper: RekeyToLedgerAccountConfirmationPreviewMapper,
    private val calculateRekeyFee: CalculateRekeyFee,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    private val rekeyToLedgerAccountConfirmationPreviewDecider: RekeyToLedgerAccountConfirmationPreviewDecider,
    private val getAccountInformation: GetAccountInformation,
    private val isThereAnyAccountWithAddress: IsThereAnyAccountWithAddress,
    private val sendSignedTransaction: SendSignedTransaction
) {

    suspend fun getInitialRekeyToStandardAccountConfirmationPreview(
        accountAddress: String,
        authAccountAddress: String
    ): RekeyToLedgerAccountConfirmationPreview {
        val accountInfo = getAccountInformation(accountAddress)
        val (authAccountDisplayName, authAccountIconResource) = createAccountDisplayNameAndDrawablePair(
            accountAddress = authAccountAddress
        )

        return rekeyToLedgerAccountConfirmationPreviewMapper.mapToRekeyToLedgerAccountConfirmationPreview(
            isLoading = false,
            descriptionAnnotatedString = rekeyToLedgerAccountConfirmationPreviewDecider
                .decideDescriptionAnnotatedString(accountInfo),
            rekeyedAccountDisplayName = getAccountDisplayName(accountAddress),
            rekeyedAccountIconResource = getAccountIconDrawablePreview(accountAddress),
            authAccountDisplayName = authAccountDisplayName,
            authAccountIconResource = authAccountIconResource,
            currentlyRekeyedAccountDisplayName = accountInfo?.getCurrentlyRekeyedAccountDisplayName(),
            currentlyRekeyedAccountIconDrawable = accountInfo?.getCurrentlyRekeyedAccountIconDrawable(),
            formattedTransactionFee = "",
            titleTextResId = R.string.confirm_rekeying,
            subtitleTextResId = R.string.summary_of_rekey,
            accountInformation = accountInfo
        )
    }

    suspend fun updatePreviewWithTransactionFee(preview: RekeyToLedgerAccountConfirmationPreview) = flow {
        val rekeyFee = calculateRekeyFee()
        val formattedFee = rekeyFee.formatAsAlgoString().formatAsAlgoAmount()
        emit(preview.copy(formattedTransactionFee = formattedFee))
    }

    fun sendRekeyToLedgerAccountTransaction(
        preview: RekeyToLedgerAccountConfirmationPreview,
        signedTransaction: SignedTransaction
    ) = flow {
        emit(preview.copy(isLoading = true))
        sendSignedTransaction.invoke(signedTransaction, waitForConfirmation = false).use(
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

    private suspend fun createAccountDisplayNameAndDrawablePair(
        accountAddress: String
    ): Pair<AccountDisplayName, AccountIconDrawablePreview> {
        return if (isThereAnyAccountWithAddress(accountAddress)) {
            val accountDisplayName = getAccountDisplayName(accountAddress)
            val accountIconDrawablePreview = getAccountIconDrawablePreview(accountAddress)
            accountDisplayName to accountIconDrawablePreview
        } else {
            val accountDisplayName = AccountDisplayName(
                accountAddress = accountAddress,
                primaryDisplayName = accountAddress.toShortenedAddress(),
                secondaryDisplayName = null
            )
            val accountIconDrawablePreview = AccountIconDrawablePreview(
                backgroundColorResId = R.color.wallet_3,
                iconTintResId = R.color.wallet_3_icon,
                iconResId = R.drawable.ic_ledger
            )
            accountDisplayName to accountIconDrawablePreview
        }
    }

    private suspend fun AccountInformation.getCurrentlyRekeyedAccountDisplayName(): AccountDisplayName? {
        return if (isRekeyed() && !rekeyAdminAddress.isNullOrBlank()) {
            getAccountDisplayName(rekeyAdminAddress.orEmpty())
        } else {
            null
        }
    }

    private suspend fun AccountInformation.getCurrentlyRekeyedAccountIconDrawable(): AccountIconDrawablePreview? {
        return if (isRekeyed() && !rekeyAdminAddress.isNullOrBlank()) {
            getAccountIconDrawablePreview(rekeyAdminAddress.orEmpty())
        } else {
            null
        }
    }
}
