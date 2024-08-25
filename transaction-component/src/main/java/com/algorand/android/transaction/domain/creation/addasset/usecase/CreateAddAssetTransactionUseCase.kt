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

package com.algorand.android.transaction.domain.creation.addasset.usecase

import com.algorand.android.accountinfo.component.domain.usecase.GetAccountInformation
import com.algorand.android.algosdk.component.transaction.AlgoSdkTransaction
import com.algorand.android.algosdk.component.transaction.model.payload.AddAssetTransactionPayload
import com.algorand.android.assetdetail.component.asset.domain.usecase.GetAsset
import com.algorand.android.transaction.domain.creation.CreateAddAssetTransaction
import com.algorand.android.transaction.domain.creation.addasset.mapper.AddAssetCreateTransactionResultMapper
import com.algorand.android.transaction.domain.creation.addasset.model.CreateAddAssetTransactionResult
import com.algorand.android.transaction.domain.creation.addasset.model.CreateAddAssetTransactionResult.AccountNotFound
import com.algorand.android.transaction.domain.creation.addasset.model.CreateAddAssetTransactionResult.AlreadyOptedIn
import com.algorand.android.transaction.domain.creation.addasset.model.CreateAddAssetTransactionResult.MinBalanceViolated
import com.algorand.android.transaction.domain.creation.addasset.model.CreateAddAssetTransactionResult.NetworkError
import com.algorand.android.transaction.domain.creation.addasset.model.CreateAddAssetTransactionResult.TransactionCreated
import com.algorand.android.transaction.domain.creation.model.CreateTransactionResult
import com.algorand.android.transaction.domain.mapper.SuggestedTransactionParamsMapper
import com.algorand.android.transaction.domain.usecase.GetTransactionParams
import com.algorand.android.transaction.domain.validator.AddAssetAssetStatusValidator
import com.algorand.android.transaction.domain.validator.AddAssetMinRequiredBalanceValidator.Payload
import com.algorand.android.transaction.domain.validator.TransactionValidator
import java.math.BigInteger

internal class CreateAddAssetTransactionUseCase(
    private val getTransactionParams: GetTransactionParams,
    private val getAccountInformation: GetAccountInformation,
    private val minRequiredBalanceValidator: TransactionValidator<Payload, BigInteger>,
    private val addAssetAssetStatusValidator: TransactionValidator<AddAssetAssetStatusValidator.Payload, Unit>,
    private val algoSdkTransaction: AlgoSdkTransaction,
    private val suggestedTransactionParamsMapper: SuggestedTransactionParamsMapper,
    private val addAssetCreateTransactionResultMapper: AddAssetCreateTransactionResultMapper,
    private val getAsset: GetAsset
) : CreateAddAssetTransaction {

    override suspend fun invoke(address: String, assetId: Long): CreateTransactionResult {
        val result = createTransaction(address, assetId)
        return addAssetCreateTransactionResultMapper(result)
    }

    private suspend fun createTransaction(address: String, assetId: Long): CreateAddAssetTransactionResult {
        val accountInfo = getAccountInformation(address) ?: return AccountNotFound(address)

        if (!addAssetAssetStatusValidator(AddAssetAssetStatusValidator.Payload(accountInfo, assetId)).isValid) {
            val asset = getAsset(assetId)
            val name = asset?.fullName ?: asset?.shortName ?: assetId.toString()
            return AlreadyOptedIn(name)
        }

        val params = getTransactionParams().run {
            getDataOrNull() ?: return NetworkError(getExceptionOrNull()?.message)
        }

        val suggestedTransactionParams = suggestedTransactionParamsMapper(params)
        val payload = AddAssetTransactionPayload(address, assetId)
        val transaction = algoSdkTransaction.createAddAssetTransaction(payload, suggestedTransactionParams)
        return with(minRequiredBalanceValidator(Payload(params, transaction, accountInfo))) {
            if (isValid) {
                TransactionCreated(transaction)
            } else {
                MinBalanceViolated(this.payload ?: BigInteger.ZERO)
            }
        }
    }
}
