/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.transaction.domain.usecase.implementation

import com.algorand.android.accountinfo.component.domain.model.AccountInformation
import com.algorand.android.accountinfo.component.domain.usecase.FetchAccountInformation
import com.algorand.android.accountinfo.component.domain.usecase.GetAccountInformation
import com.algorand.android.transaction.domain.model.ReceiverAccountValidationResult
import com.algorand.android.transaction.domain.model.ReceiverAccountValidationResult.CloseTransactionToSameAccount
import com.algorand.android.transaction.domain.model.ReceiverAccountValidationResult.InvalidAlgorandAddress
import com.algorand.android.transaction.domain.model.ReceiverAccountValidationResult.NetworkError
import com.algorand.android.transaction.domain.model.ReceiverAccountValidationResult.NewAccountBalanceNotValid
import com.algorand.android.transaction.domain.model.ReceiverAccountValidationResult.ReceiverNotOptedInToAsset
import com.algorand.android.transaction.domain.model.ReceiverAccountValidationResult.SenderNotOptedInToAsset
import com.algorand.android.transaction.domain.model.ReceiverAccountValidationResult.SendingLessAmountThanRequired
import com.algorand.android.transaction.domain.model.ReceiverAccountValidationResult.SendingMaxAmountToSameAccount
import com.algorand.android.transaction.domain.model.ReceiverAccountValidationResult.Valid
import com.algorand.android.transaction.domain.model.ValidateReceiverAccountPayload
import com.algorand.android.transaction.domain.model.ValidationResult
import com.algorand.android.transaction.domain.usecase.ValidateReceiverAccount
import com.algorand.android.transaction.domain.validator.CloseTransactionToSameAccountValidator
import com.algorand.android.transaction.domain.validator.NewAccountBalanceValidator
import com.algorand.android.transaction.domain.validator.ReceiverOptedInToAssetValidator
import com.algorand.android.transaction.domain.validator.SenderOptedInToAssetValidator
import com.algorand.android.transaction.domain.validator.SendingLessAmountThanRequiredValidator
import com.algorand.android.transaction.domain.validator.SendingMaxAmountToSameAccountValidator
import com.algorand.android.transaction.domain.validator.TransactionValidator
import java.math.BigInteger

internal class ValidateReceiverAccountUseCase(
    private val receiverOptedInToAssetValidator: TransactionValidator<ReceiverOptedInToAssetValidator.Payload, Unit>,
    private val sendingLessAmountThanRequiredValidator: TransactionValidator<SendingLessAmountThanRequiredValidator.Payload, BigInteger>,
    private val sendingMaxAmountToSameAccountValidator: TransactionValidator<SendingMaxAmountToSameAccountValidator.Payload, Unit>,
    private val closeTransactionToSameAccountValidator: TransactionValidator<CloseTransactionToSameAccountValidator.Payload, Unit>,
    private val newAccountBalanceValidator: TransactionValidator<NewAccountBalanceValidator.Payload, Unit>,
    private val senderOptedInToAssetValidator: TransactionValidator<SenderOptedInToAssetValidator.Payload, Unit>,
    private val accountAddressValidator: TransactionValidator<String, Unit>,
    private val getAccountInformation: GetAccountInformation,
    private val fetchAccountInformation: FetchAccountInformation
) : ValidateReceiverAccount {

    override suspend fun invoke(payload: ValidateReceiverAccountPayload): ReceiverAccountValidationResult {
        with(payload) {

            isValidAlgorandAddress(receiverAddress).run {
                if (!this.isValid) return InvalidAlgorandAddress
            }

            val senderInfo = getAccountInformation(senderAddress) ?: return NetworkError
            val receiverInfo = fetchAccountInformation(receiverAddress).getOrNull() ?: return NetworkError

            isSenderOptedInToAsset(senderInfo, payload).run {
                if (!this.isValid) return SenderNotOptedInToAsset
            }

            isReceiverOptedInToAsset(receiverInfo, assetId).run {
                if (!this.isValid) return ReceiverNotOptedInToAsset
            }

            isSendingLessAmountThanRequired(receiverInfo, payload).run {
                if (!this.isValid) return SendingLessAmountThanRequired(this.payload ?: BigInteger.ZERO)
            }

            isSendingMaxAmountToSameAccount(senderInfo, receiverInfo, payload).run {
                if (!this.isValid) return SendingMaxAmountToSameAccount
            }

            isCloseTransactionToSameAccount(senderInfo, receiverInfo, payload).run {
                if (!this.isValid) return CloseTransactionToSameAccount
            }

            isNewAccountBalanceValid(receiverInfo, assetId, amount).run {
                if (!this.isValid) return NewAccountBalanceNotValid
            }

            return Valid(senderInfo, receiverInfo)
        }
    }

    private suspend fun isValidAlgorandAddress(receiverAddress: String): ValidationResult<Unit> {
        return accountAddressValidator(receiverAddress)
    }

    private suspend fun isSenderOptedInToAsset(
        senderInfo: AccountInformation,
        payload: ValidateReceiverAccountPayload
    ): ValidationResult<Unit> {
        val validatorPayload = SenderOptedInToAssetValidator.Payload(senderInfo, payload.assetId)
        return senderOptedInToAssetValidator(validatorPayload)
    }

    private suspend fun isReceiverOptedInToAsset(
        receiverInfo: AccountInformation,
        assetId: Long
    ): ValidationResult<Unit> {
        val payload = ReceiverOptedInToAssetValidator.Payload(receiverInfo, assetId)
        return receiverOptedInToAssetValidator(payload)
    }

    private suspend fun isSendingLessAmountThanRequired(
        receiverInfo: AccountInformation,
        payload: ValidateReceiverAccountPayload
    ): ValidationResult<BigInteger> {
        return with(payload) {
            val validatorPayload = SendingLessAmountThanRequiredValidator.Payload(receiverInfo, assetId, amount)
            sendingLessAmountThanRequiredValidator(validatorPayload)
        }
    }

    private suspend fun isSendingMaxAmountToSameAccount(
        senderInfo: AccountInformation,
        receiverInfo: AccountInformation,
        payload: ValidateReceiverAccountPayload
    ): ValidationResult<Unit> {
        val validatorPayload = SendingMaxAmountToSameAccountValidator.Payload(
            senderInfo,
            receiverInfo,
            payload.assetId,
            payload.amount
        )
        return sendingMaxAmountToSameAccountValidator(validatorPayload)
    }

    private suspend fun isCloseTransactionToSameAccount(
        senderInfo: AccountInformation,
        receiverInfo: AccountInformation,
        payload: ValidateReceiverAccountPayload
    ): ValidationResult<Unit> {
        val validatorPayload = CloseTransactionToSameAccountValidator.Payload(
            senderInfo,
            receiverInfo.address,
            payload.assetId,
            payload.amount
        )
        return closeTransactionToSameAccountValidator(validatorPayload)
    }

    private suspend fun isNewAccountBalanceValid(
        receiverInfo: AccountInformation,
        assetId: Long,
        amount: BigInteger
    ): ValidationResult<Unit> {
        val validatorPayload = NewAccountBalanceValidator.Payload(receiverInfo, assetId, amount)
        return newAccountBalanceValidator(validatorPayload)
    }
}
