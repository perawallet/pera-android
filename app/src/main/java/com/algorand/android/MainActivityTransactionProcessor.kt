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

package com.algorand.android

import androidx.lifecycle.Lifecycle
import com.algorand.android.module.account.core.ui.usecase.GetAssetName
import com.algorand.android.module.algosdk.transaction.model.Transaction
import com.algorand.android.module.algosdk.transaction.model.Transaction.AddAssetTransaction
import com.algorand.android.module.algosdk.transaction.model.Transaction.RemoveAssetTransaction
import com.algorand.android.assetdetail.component.asset.domain.usecase.GetAsset
import com.algorand.android.foundation.PeraResult
import com.algorand.android.models.AssetOperationResult
import com.algorand.android.module.transaction.component.domain.model.SendSignedAddAssetTransactionPayload
import com.algorand.android.module.transaction.component.domain.model.SendSignedRemoveAssetTransactionPayload
import com.algorand.android.module.transaction.component.domain.model.SignedTransaction
import com.algorand.android.module.transaction.component.domain.sign.SignTransactionManager
import com.algorand.android.module.transaction.component.domain.usecase.SendSignedAddAssetTransaction
import com.algorand.android.module.transaction.component.domain.usecase.SendSignedRemoveAssetTransaction
import com.algorand.android.module.transaction.ui.addasset.model.AddAssetTransactionPayload
import com.algorand.android.module.transaction.ui.core.mapper.SignTransactionUiResultMapper
import com.algorand.android.module.transaction.ui.core.model.SignTransactionUiResult
import com.algorand.android.utils.Event
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainActivityTransactionProcessor @Inject constructor(
    private val signTransactionManager: SignTransactionManager,
    private val signTransactionUiResultMapper: SignTransactionUiResultMapper,
    private val sendSignedAddAssetTransaction: SendSignedAddAssetTransaction,
    private val sendSignedRemoveAssetTransaction: SendSignedRemoveAssetTransaction,
    private val getAsset: GetAsset,
    private val getAssetName: GetAssetName
) {

    private var address: String? = null
    private var assetId: Long? = null
    private var waitForConfirmation: Boolean? = null
    private var transaction: Transaction? = null
    private var assetName: String? = null

    private val _signTransactionUiResultFlow = MutableStateFlow<Event<SignTransactionUiResult>?>(null)
    val signTransactionUiResultFlow: StateFlow<Event<SignTransactionUiResult>?>
        get() = _signTransactionUiResultFlow.asStateFlow()

    fun cacheTransaction(address: String, assetId: Long, waitForConfirmation: Boolean) {
        this.address = address
        this.assetId = assetId
        this.waitForConfirmation = waitForConfirmation
    }

    fun cacheTransaction(payload: AddAssetTransactionPayload) {
        this.address = payload.address
        this.assetId = payload.assetId
        this.waitForConfirmation = payload.shouldWaitForConfirmation
        this.assetName = payload.assetName
    }

    fun processTransaction(lifecycle: Lifecycle, transaction: Transaction, coroutineScope: CoroutineScope) {
        this.transaction = transaction
        signTransactionManager.setup(lifecycle)
        coroutineScope.launch {
            _signTransactionUiResultFlow.value = Event(SignTransactionUiResult.Loading)
            signTransactionManager.signTransactionResultFlow.collect { result ->
                result?.consume()?.let {
                    _signTransactionUiResultFlow.value = Event(signTransactionUiResultMapper(it))
                }
            }
        }
        signTransactionManager.sign(transaction.signerAddress, transaction.value)
    }

    fun stopSignTransactionResources() {
        signTransactionManager.stopAllResources()
    }

    suspend fun sendSignedTransaction(signedTransaction: SignedTransaction): PeraResult<AssetOperationResult>? {
        val txn = transaction ?: return null
        return when (txn) {
            is AddAssetTransaction -> sendAddAssetTransaction(txn, signedTransaction)
            is RemoveAssetTransaction -> sendRemoveAssetTransaction(txn, signedTransaction)
            else -> null
        }
    }

    private suspend fun sendAddAssetTransaction(
        transaction: AddAssetTransaction,
        signedTransaction: SignedTransaction
    ): PeraResult<AssetOperationResult.AssetAdditionOperationResult> {
        val safeAssetId = assetId ?: return PeraResult.Error(IllegalArgumentException())
        val payload = SendSignedAddAssetTransactionPayload(
            transaction = signedTransaction,
            address = transaction.signerAddress,
            assetId = safeAssetId,
            waitForConfirmation = waitForConfirmation ?: false
        )
        return sendSignedAddAssetTransaction(payload).map {
            AssetOperationResult.AssetAdditionOperationResult(
                resultTitleResId = R.string.asset_successfully_opted_in,
                assetName = getAssetName(assetName),
                assetId = safeAssetId
            )
        }
    }

    private suspend fun sendRemoveAssetTransaction(
        transaction: RemoveAssetTransaction,
        signedTransaction: SignedTransaction
    ): PeraResult<AssetOperationResult.AssetRemovalOperationResult> {
        val safeAssetId = assetId ?: return PeraResult.Error(IllegalArgumentException())
        val payload = SendSignedRemoveAssetTransactionPayload(
            transaction = signedTransaction,
            address = transaction.signerAddress,
            assetId = safeAssetId,
            waitForConfirmation = false
        )
        return sendSignedRemoveAssetTransaction(payload).map {
            val asset = getAsset(safeAssetId)
            AssetOperationResult.AssetRemovalOperationResult(
                resultTitleResId = R.string.asset_successfully_opted_out_from_your,
                assetName = getAssetName(asset?.fullName),
                assetId = safeAssetId
            )
        }
    }
}
