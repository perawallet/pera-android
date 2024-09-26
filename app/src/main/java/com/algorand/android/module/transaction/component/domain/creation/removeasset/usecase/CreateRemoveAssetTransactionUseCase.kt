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

package com.algorand.android.module.transaction.component.domain.creation.removeasset.usecase

import com.algorand.android.module.account.info.domain.usecase.GetAccountInformation
import com.algorand.android.module.algosdk.transaction.AlgoSdkTransaction
import com.algorand.android.module.algosdk.transaction.model.payload.RemoveAssetTransactionPayload
import com.algorand.android.module.asset.detail.component.asset.domain.usecase.GetAsset
import com.algorand.android.module.transaction.component.domain.creation.CreateRemoveAssetTransaction
import com.algorand.android.module.transaction.component.domain.creation.model.CreateTransactionResult
import com.algorand.android.module.transaction.component.domain.creation.removeasset.mapper.CreateRemoveAssetTransactionResultMapper
import com.algorand.android.module.transaction.component.domain.creation.removeasset.model.CreateRemoveAssetTransactionResult
import com.algorand.android.module.transaction.component.domain.creation.removeasset.model.CreateRemoveAssetTransactionResult.AccountHasAsset
import com.algorand.android.module.transaction.component.domain.creation.removeasset.model.CreateRemoveAssetTransactionResult.AccountNotFound
import com.algorand.android.module.transaction.component.domain.creation.removeasset.model.CreateRemoveAssetTransactionResult.AssetAlreadyWaitingForRemoval
import com.algorand.android.module.transaction.component.domain.creation.removeasset.model.CreateRemoveAssetTransactionResult.AssetCreatorCantOptOut
import com.algorand.android.module.transaction.component.domain.creation.removeasset.model.CreateRemoveAssetTransactionResult.MinBalanceViolated
import com.algorand.android.module.transaction.component.domain.creation.removeasset.model.CreateRemoveAssetTransactionResult.NetworkError
import com.algorand.android.module.transaction.component.domain.creation.removeasset.model.CreateRemoveAssetTransactionResult.TransactionCreated
import com.algorand.android.module.transaction.component.domain.mapper.SuggestedTransactionParamsMapper
import com.algorand.android.module.transaction.component.domain.usecase.GetTransactionParams
import com.algorand.android.module.transaction.component.domain.validator.OptOutAssetCreatorValidator
import com.algorand.android.module.transaction.component.domain.validator.OptOutZeroBalanceValidator
import com.algorand.android.module.transaction.component.domain.validator.RemoveAssetAssetStatusValidator
import com.algorand.android.module.transaction.component.domain.validator.RemoveAssetMinRequiredBalanceValidator
import com.algorand.android.module.transaction.component.domain.validator.TransactionValidator
import java.math.BigInteger
import javax.inject.Inject

internal typealias MinReqBalanceValidatorPayload = RemoveAssetMinRequiredBalanceValidator.Payload
internal typealias OptOutAddressValidatorPayload = OptOutAssetCreatorValidator.Payload
internal typealias AssetStatusValidatorPayload = RemoveAssetAssetStatusValidator.Payload

internal class CreateRemoveAssetTransactionUseCase @Inject constructor(
    private val getTransactionParams: GetTransactionParams,
    private val getAccountInformation: GetAccountInformation,
    private val minRequiredBalanceValidator: TransactionValidator<MinReqBalanceValidatorPayload, BigInteger>,
    private val optOutAssetCreatorValidator: TransactionValidator<OptOutAddressValidatorPayload, Unit>,
    private val optOutZeroBalanceValidator: TransactionValidator<OptOutZeroBalanceValidator.Payload, Unit>,
    private val removeAssetAssetStatusValidator: TransactionValidator<RemoveAssetAssetStatusValidator.Payload, Unit>,
    private val algoSdkTransaction: AlgoSdkTransaction,
    private val getAsset: GetAsset,
    private val suggestedTransactionParamsMapper: SuggestedTransactionParamsMapper,
    private val createRemoveAssetTransactionResultMapper: CreateRemoveAssetTransactionResultMapper
) : CreateRemoveAssetTransaction {

    override suspend fun invoke(address: String, assetId: Long): CreateTransactionResult {
        val result = createTransaction(address, assetId)
        return createRemoveAssetTransactionResultMapper(result)
    }

    private suspend fun createTransaction(address: String, assetId: Long): CreateRemoveAssetTransactionResult {
        val assetDetail = getAsset(assetId) ?: return NetworkError(null)
        val assetCreatorAddress = assetDetail.creatorAddress ?: return NetworkError(null)
        optOutAssetCreatorValidator(OptOutAddressValidatorPayload(address, assetCreatorAddress)).run {
            if (!this.isValid) {
                return AssetCreatorCantOptOut
            }
        }

        val accountInfo = getAccountInformation(address) ?: return AccountNotFound(address)
        removeAssetAssetStatusValidator(AssetStatusValidatorPayload(accountInfo, assetId)).run {
            if (!this.isValid) {
                return AssetAlreadyWaitingForRemoval
            }
        }

        optOutZeroBalanceValidator(OptOutZeroBalanceValidator.Payload(accountInfo, assetId)).run {
            if (!this.isValid) {
                val assetName = assetDetail.fullName ?: assetDetail.shortName ?: assetId.toString()
                return AccountHasAsset(assetName)
            }
        }

        val params = getTransactionParams().run {
            getDataOrNull() ?: return NetworkError(getExceptionOrNull()?.message)
        }
        val suggestedTxnParams = suggestedTransactionParamsMapper(params)
        val txnPayload = RemoveAssetTransactionPayload(address, assetCreatorAddress, assetId)
        val txn = algoSdkTransaction.createRemoveAssetTransaction(txnPayload, suggestedTxnParams)


        minRequiredBalanceValidator(MinReqBalanceValidatorPayload(params, txn, accountInfo)).run {
            if (!this.isValid) {
                return MinBalanceViolated(this.payload ?: BigInteger.ZERO)
            }
        }

        return TransactionCreated(txn)
    }
}
