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

package com.algorand.android.module.transaction.component.domain.creation.send.payment.usecase

import com.algorand.android.module.account.info.domain.model.AccountInformation
import com.algorand.android.module.account.info.domain.usecase.GetAccountInformation
import com.algorand.android.module.algosdk.transaction.AlgoSdkTransaction
import com.algorand.android.module.algosdk.transaction.model.Transaction
import com.algorand.android.module.algosdk.transaction.model.payload.AlgoTransactionPayload
import com.algorand.android.assetdetail.component.AssetConstants.ALGO_ASSET_ID
import com.algorand.android.module.account.core.component.assetdata.usecase.GetAccountBaseOwnedAssetData
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData
import com.algorand.android.module.account.core.component.domain.usecase.GetAccountMinBalance
import com.algorand.android.module.transaction.component.domain.creation.CreatePaymentTransaction
import com.algorand.android.module.transaction.component.domain.creation.model.CreateTransactionResult
import com.algorand.android.module.transaction.component.domain.creation.model.CreateTransactionResult.AccountNotFound
import com.algorand.android.module.transaction.component.domain.creation.model.CreateTransactionResult.MinBalanceViolated
import com.algorand.android.module.transaction.component.domain.creation.send.payment.model.CreatePaymentTransactionPayload
import com.algorand.android.module.transaction.component.domain.mapper.SuggestedTransactionParamsMapper
import com.algorand.android.module.transaction.component.domain.model.ValidationResult
import com.algorand.android.module.transaction.component.domain.usecase.CalculateTransactionFee
import com.algorand.android.module.transaction.component.domain.validator.SendAssetMinRequiredBalanceValidator.Payload
import com.algorand.android.module.transaction.component.domain.validator.TransactionValidator
import java.math.BigInteger

internal class CreatePaymentTransactionUseCase(
    private val getAccountInformation: GetAccountInformation,
    private val calculateTransactionFee: CalculateTransactionFee,
    private val getAccountMinBalance: GetAccountMinBalance,
    private val algoSdkTransaction: AlgoSdkTransaction,
    private val suggestedTransactionParamsMapper: SuggestedTransactionParamsMapper,
    private val minRequiredBalanceValidator: TransactionValidator<Payload, Unit>,
    private val getAccountBaseOwnedAssetData: GetAccountBaseOwnedAssetData,
) : CreatePaymentTransaction {

    override suspend fun invoke(payload: CreatePaymentTransactionPayload): CreateTransactionResult {
        val (accountInfo, ownedAssetData) = getAccountData(payload.senderAddress)
        if (accountInfo == null || ownedAssetData == null) return AccountNotFound(payload.senderAddress)

        val minRequiredBalance = getAccountMinBalance(accountInfo)

        var isMaxTransaction = accountInfo.amount == payload.amount
        val amount = payload.getCalculatedAmount(isMaxTransaction, accountInfo, minRequiredBalance)

        isMinimumBalanceViolated(accountInfo, ownedAssetData, amount, minRequiredBalance).run {
            if (!this.isValid) return MinBalanceViolated(minRequiredBalance)
        }

        if (accountInfo.isRekeyed()) {
            // if account is rekeyed to another account, min balance should be deducted from the amount.
            // after it'll be deducted, isMax will be false to not write closeToAddress.
            isMaxTransaction = false
        }

        if (payload.isCloseToSameAccount(isMaxTransaction)) {
            return CreateTransactionResult.CloseTransactionToSameAccount
        }

        val algoTransaction = payload.createAlgoTransaction(isMaxTransaction, amount)
        return CreateTransactionResult.TransactionCreated(algoTransaction)
    }

    private fun CreatePaymentTransactionPayload.createAlgoTransaction(
        isMaxTransaction: Boolean,
        amount: BigInteger
    ): Transaction.AlgoTransaction {
        val algoPayload = getAlgoTransactionPayload(this, amount, isMaxTransaction)
        return algoSdkTransaction.createAlgoTransaction(algoPayload, suggestedTransactionParamsMapper(params))
    }

    private suspend fun CreatePaymentTransactionPayload.getCalculatedAmount(
        isMaxTransaction: Boolean,
        accountInfo: AccountInformation,
        minRequiredBalance: BigInteger
    ): BigInteger {
        return if (isMaxTransaction) getCalculatedAmountForMax(
            accountInfo,
            minRequiredBalance
        ) else amount
    }

    private fun getAlgoTransactionPayload(
        payload: CreatePaymentTransactionPayload,
        amount: BigInteger,
        isMaxTransaction: Boolean
    ): AlgoTransactionPayload {
        return AlgoTransactionPayload(
            senderAddress = payload.senderAddress,
            receiverAddress = payload.receiverAddress,
            amount = amount,
            noteInByteArray = payload.note,
            isMaxAmount = isMaxTransaction
        )
    }

    private suspend fun getAccountData(senderAddress: String): Pair<AccountInformation?, BaseOwnedAssetData?> {
        return getAccountInformation(senderAddress) to getAccountBaseOwnedAssetData(senderAddress, ALGO_ASSET_ID)
    }

    private suspend fun CreatePaymentTransactionPayload.getCalculatedAmountForMax(
        accountInfo: AccountInformation,
        senderMinRequiredBalance: BigInteger,
    ): BigInteger {
        val fee = calculateTransactionFee(params, null)
        return if (accountInfo.isRekeyed()) {
            amount - fee - senderMinRequiredBalance
        } else {
            amount - fee
        }
    }

    private fun CreatePaymentTransactionPayload.isCloseToSameAccount(isMax: Boolean): Boolean {
        return isMax && senderAddress == receiverAddress
    }

    private suspend fun isMinimumBalanceViolated(
        senderInfo: AccountInformation,
        ownedAssetData: BaseOwnedAssetData,
        amount: BigInteger,
        minRequiredBalance: BigInteger
    ): ValidationResult<Unit> {
        val payload = Payload(senderInfo, ownedAssetData, minRequiredBalance, amount)
        return minRequiredBalanceValidator(payload)
    }
}
