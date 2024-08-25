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
import com.algorand.android.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.accountinfo.component.domain.usecase.GetAccountInformation
import com.algorand.android.designsystem.AnnotatedString
import com.algorand.android.models.SignedTransactionDetail
import com.algorand.android.modules.accounticon.ui.usecase.CreateAccountOriginalStateIconDrawableUseCase
import com.algorand.android.modules.rekey.undorekey.confirmation.ui.mapper.UndoRekeyConfirmationPreviewMapper
import com.algorand.android.modules.rekey.undorekey.confirmation.ui.model.UndoRekeyConfirmationPreview
import com.algorand.android.transaction.domain.usecase.CalculateRekeyFee
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
    private val calculateRekeyFee: CalculateRekeyFee
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

    fun sendUndoRekeyTransaction(
        preview: UndoRekeyConfirmationPreview,
        transactionDetail: SignedTransactionDetail
    ): Flow<UndoRekeyConfirmationPreview> = flow {
//        emit(preview.copy(isLoading = true)) TODO
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
    }

//    fun createUndoRekeyTransaction(accountAddress: String): TransactionData? {
//        return null
    // TODO
//        val account = accountDetailUseCase.getCachedAccountDetail(accountAddress)?.data?.account ?: return null
//        return when (account.type) {
//            Account.Type.STANDARD, Account.Type.LEDGER, Account.Type.WATCH, null -> null
//            Account.Type.REKEYED -> createRekeyToStandardAccountTransaction(accountAddress)
//            Account.Type.REKEYED_AUTH -> {
//                val hasAccountValidSecretKey = accountStateHelperUseCase.hasAccountValidSecretKey(account)
//                if (hasAccountValidSecretKey) {
//                    createRekeyToStandardAccountTransaction(accountAddress)
//                } else {
//                    createRekeyTransaction(accountAddress) ?: createRekeyToStandardAccountTransaction(accountAddress)
//                }
//            }
//        }
//}

//    private fun createRekeyTransaction(accountAddress: String): TransactionData.Rekey? {
//        val senderAccountDetail = accountDetailUseCase.getCachedAccountDetail(accountAddress)?.data ?: return null
//        val senderAccountAccountDetail = senderAccountDetail.account.detail
//        if (senderAccountAccountDetail !is Account.Detail.RekeyedAuth) {
//            return null
//        }
//        val authAccountAddress = senderAccountDetail.accountInformation.rekeyAdminAddress.orEmpty()
//        val ledgerDetail = senderAccountAccountDetail.rekeyedAuthDetail[authAccountAddress] ?: return null
//        return TransactionData.Rekey(
//            senderAccountAddress = senderAccountDetail.account.address,
//            senderAccountDetail = senderAccountDetail.account.detail,
//            senderAccountType = senderAccountDetail.account.type,
//            senderAuthAddress = senderAccountDetail.accountInformation.rekeyAdminAddress,
//            senderAccountName = senderAccountDetail.account.name,
//            isSenderRekeyedToAnotherAccount = senderAccountDetail.accountInformation.isRekeyed(),
//            rekeyAdminAddress = accountAddress,
//            ledgerDetail = ledgerDetail,
//            senderAccountAuthTypeAndDetail = senderAccountDetail.account.getAuthTypeAndDetail()
//        )
//    }

//    private fun createRekeyToStandardAccountTransaction(
//        accountAddress: String
//    ): TransactionData.RekeyToStandardAccount? {
//        val senderAccountDetail = accountDetailUseCase.getCachedAccountDetail(accountAddress)?.data ?: return null
//        return TransactionData.RekeyToStandardAccount(
//            senderAccountAddress = senderAccountDetail.account.address,
//            senderAccountDetail = senderAccountDetail.account.detail,
//            senderAccountType = senderAccountDetail.account.type,
//            senderAuthAddress = senderAccountDetail.accountInformation.rekeyAdminAddress,
//            senderAccountName = senderAccountDetail.account.name,
//            isSenderRekeyedToAnotherAccount = senderAccountDetail.accountInformation.isRekeyed(),
//            rekeyAdminAddress = accountAddress,
//            senderAccountAuthTypeAndDetail = senderAccountDetail.account.getAuthTypeAndDetail()
//        )
//    }
}
