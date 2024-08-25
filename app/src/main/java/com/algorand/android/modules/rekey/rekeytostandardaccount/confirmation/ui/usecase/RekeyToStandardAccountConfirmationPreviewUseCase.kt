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

package com.algorand.android.modules.rekey.rekeytostandardaccount.confirmation.ui.usecase

import com.algorand.android.R
import com.algorand.android.accountcore.ui.model.AccountDisplayName
import com.algorand.android.accountcore.ui.model.AccountIconDrawablePreview
import com.algorand.android.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.accountinfo.component.domain.model.AccountInformation
import com.algorand.android.accountinfo.component.domain.usecase.GetAccountInformation
import com.algorand.android.modules.rekey.rekeytostandardaccount.confirmation.ui.decider.RekeyToStandardAccountPreviewDecider
import com.algorand.android.modules.rekey.rekeytostandardaccount.confirmation.ui.mapper.RekeyToStandardAccountConfirmationPreviewMapper
import com.algorand.android.modules.rekey.rekeytostandardaccount.confirmation.ui.model.RekeyToStandardAccountConfirmationPreview
import com.algorand.android.transaction.domain.usecase.CalculateRekeyFee
import com.algorand.android.utils.emptyString
import com.algorand.android.utils.formatAsAlgoAmount
import com.algorand.android.utils.formatAsAlgoString
import javax.inject.Inject
import kotlinx.coroutines.flow.flow

class RekeyToStandardAccountConfirmationPreviewUseCase @Inject constructor(
    private val rekeyToStandardAccountConfirmationPreviewMapper: RekeyToStandardAccountConfirmationPreviewMapper,
    private val rekeyToStandardAccountPreviewDecider: RekeyToStandardAccountPreviewDecider,
    private val calculateRekeyFee: CalculateRekeyFee,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    private val getAccountInformation: GetAccountInformation
) {

//    fun sendRekeyToStandardAccountTransaction(
//        preview: RekeyToStandardAccountConfirmationPreview,
//        transactionDetail: SignedTransactionDetail.RekeyToStandardAccountOperation
//    ) = flow {
//        emit(preview.copy(isLoading = true))
//        sendSignedTransactionUseCase.invoke(transactionDetail).useSuspended(
//            onSuccess = {
//                emit(
//                    preview.copy(
//                        isLoading = false,
//                        navToRekeyResultInfoFragmentEvent = Event(Unit)
//                    )
//                )
//            },
//            onFailed = {
//                val title = R.string.error
//                val description = it.exception?.message.orEmpty()
//                emit(preview.copy(showGlobalErrorEvent = Event(title to description), isLoading = false))
//            }
//        )
//    }
//
//    fun createRekeyToStandardAccountTransaction(
//        accountAddress: String,
//        authAccountAddress: String
//    ): TransactionData.RekeyToStandardAccount? {
//        val senderAccountDetail = accountDetailUseCase.getCachedAccountDetail(accountAddress)?.data ?: return null
//        return TransactionData.RekeyToStandardAccount(
//            senderAccountAddress = senderAccountDetail.account.address,
//            senderAccountDetail = senderAccountDetail.account.detail,
//            senderAccountType = senderAccountDetail.account.type,
//            senderAuthAddress = senderAccountDetail.accountInformation.rekeyAdminAddress,
//            senderAccountName = senderAccountDetail.account.name,
//            isSenderRekeyedToAnotherAccount = senderAccountDetail.accountInformation.isRekeyed(),
//            rekeyAdminAddress = authAccountAddress,
//            senderAccountAuthTypeAndDetail = senderAccountDetail.account.getAuthTypeAndDetail()
//        )
//    }

    suspend fun getInitialRekeyToStandardAccountConfirmationPreview(
        accountAddress: String,
        authAccountAddress: String
    ): RekeyToStandardAccountConfirmationPreview {
        val accountInfo = getAccountInformation(accountAddress)

        return rekeyToStandardAccountConfirmationPreviewMapper.mapToRekeyToStandardAccountConfirmationPreview(
            isLoading = false,
            descriptionAnnotatedString = rekeyToStandardAccountPreviewDecider.decideDescriptionAnnotatedString(
                accountInfo
            ),
            rekeyedAccountDisplayName = getAccountDisplayName(accountAddress),
            rekeyedAccountIconResource = getAccountIconDrawablePreview(accountAddress),
            authAccountDisplayName = getAccountDisplayName(authAccountAddress),
            authAccountIconResource = getAccountIconDrawablePreview(authAccountAddress),
            currentlyRekeyedAccountDisplayName = accountInfo?.getCurrentlyRekeyedAccountDisplayName(),
            currentlyRekeyedAccountIconDrawable = accountInfo?.getCurrentlyRekeyedAccountIconDrawable(),
            formattedTransactionFee = emptyString(),
            titleTextResId = R.string.confirm_rekeying,
            subtitleTextResId = R.string.summary_of_rekey,
            accountInformation = accountInfo
        )
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

    suspend fun updatePreviewWithTransactionFee(preview: RekeyToStandardAccountConfirmationPreview) = flow {
        val rekeyFee = calculateRekeyFee()
        val formattedFee = rekeyFee.formatAsAlgoString().formatAsAlgoAmount()
        emit(preview.copy(formattedTransactionFee = formattedFee))
    }
}
