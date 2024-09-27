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

package com.algorand.android.module.transaction.component.domain.creation.rekey.usecase

import com.algorand.android.module.account.core.component.domain.usecase.GetAccountMinBalance
import com.algorand.android.module.account.info.domain.usecase.GetAccountInformation
import com.algorand.android.module.algosdk.transaction.AlgoSdkTransaction
import com.algorand.android.module.algosdk.transaction.model.Transaction.RekeyTransaction
import com.algorand.android.module.algosdk.transaction.model.payload.RekeyTransactionPayload
import com.algorand.android.module.transaction.component.domain.creation.CreateRekeyTransaction
import com.algorand.android.module.transaction.component.domain.creation.model.CreateTransactionResult
import com.algorand.android.module.transaction.component.domain.creation.rekey.mapper.CreateRekeyTransactionResultMapper
import com.algorand.android.module.transaction.component.domain.creation.rekey.model.CreateRekeyTransactionResult
import com.algorand.android.module.transaction.component.domain.creation.rekey.model.CreateRekeyTransactionResult.AccountNotFound
import com.algorand.android.module.transaction.component.domain.creation.rekey.model.CreateRekeyTransactionResult.MinBalanceViolated
import com.algorand.android.module.transaction.component.domain.creation.rekey.model.CreateRekeyTransactionResult.NetworkError
import com.algorand.android.module.transaction.component.domain.creation.rekey.model.CreateRekeyTransactionResult.TransactionCreated
import com.algorand.android.module.transaction.component.domain.mapper.SuggestedTransactionParamsMapper
import com.algorand.android.module.transaction.component.domain.model.TransactionParams
import com.algorand.android.module.transaction.component.domain.usecase.GetTransactionParams
import com.algorand.android.module.transaction.component.domain.validator.SufficientBalanceToPayFeeValidator.Payload
import com.algorand.android.module.transaction.component.domain.validator.TransactionValidator

internal class CreateRekeyTransactionUseCase(
    private val getTransactionParams: GetTransactionParams,
    private val sufficientBalanceToPayFeeValidator: TransactionValidator<Payload, Unit>,
    private val suggestedTransactionParamsMapper: SuggestedTransactionParamsMapper,
    private val algoSdkTransaction: AlgoSdkTransaction,
    private val getAccountInformation: GetAccountInformation,
    private val getAccountMinBalance: GetAccountMinBalance,
    private val createRekeyTransactionResultMapper: CreateRekeyTransactionResultMapper
) : CreateRekeyTransaction {

    override suspend fun invoke(address: String, rekeyAuthAddress: String): CreateTransactionResult {
        val result = createTransaction(address, rekeyAuthAddress)
        return createRekeyTransactionResultMapper(result)
    }

    private suspend fun createTransaction(address: String, rekeyAuthAddress: String): CreateRekeyTransactionResult {
        val params = getTransactionParams().run {
            getDataOrNull() ?: return NetworkError(getExceptionOrNull()?.message)
        }

        val accountInfo = getAccountInformation(address) ?: return AccountNotFound(address)
        val txn = getTransaction(address, rekeyAuthAddress, params)

        val accountMinBalance = getAccountMinBalance(accountInfo)
        sufficientBalanceToPayFeeValidator(Payload(accountInfo, accountMinBalance)).run {
            if (!this.isValid) {
                return MinBalanceViolated(accountMinBalance)
            }
        }
        return TransactionCreated(txn)
    }

    private fun getTransaction(address: String, rekeyAuthAddress: String, params: TransactionParams): RekeyTransaction {
        val suggestedParams = suggestedTransactionParamsMapper(params)
        val payload = RekeyTransactionPayload(address, rekeyAuthAddress)
        return algoSdkTransaction.createRekeyTransaction(payload, suggestedParams)
    }
}
