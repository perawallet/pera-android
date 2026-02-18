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

package com.algorand.android.core.transaction

import android.bluetooth.BluetoothDevice
import com.algorand.algosdk.transaction.SignedTransaction
import com.algorand.algosdk.util.Encoder
import com.algorand.android.ledger.CustomScanCallback
import com.algorand.android.ledger.LedgerBleOperationManager
import com.algorand.android.ledger.LedgerBleSearchManager
import com.algorand.android.ledger.operations.ExternalTransaction
import com.algorand.android.ledger.operations.ExternalTransactionOperation
import com.algorand.android.models.TransactionManagerResult
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class JointAccountLedgerSignDelegate @Inject constructor(
    private val ledgerBleSearchManager: LedgerBleSearchManager,
    private val ledgerBleOperationManager: LedgerBleOperationManager,
    private val jointAccountTransactionSignHelper: JointAccountTransactionSignHelper
) {

    private var pendingSign: PendingJointAccountLedgerSign? = null
    private var signedTransactions = mutableListOf<ByteArray>()
    private var currentIndex = 0

    val hasPendingSign: Boolean get() = pendingSign != null

    fun createScanCallback(
        onError: (TransactionManagerResult) -> Unit
    ): CustomScanCallback = object : CustomScanCallback() {
        override fun onLedgerScanned(
            device: BluetoothDevice,
            currentTransactionIndex: Int?,
            totalTransactionCount: Int?
        ) {
            ledgerBleSearchManager.stop()
            pendingSign?.let { pending ->
                val rawTxBytes = pending.rawTransactionBytes.getOrNull(currentIndex)
                if (rawTxBytes != null) {
                    val transaction = JointAccountLedgerTransaction(
                        transactionByteArray = rawTxBytes,
                        accountAddress = pending.proposal.proposerAddress,
                        accountAuthAddress = null,
                        isRekeyedToAnotherAccount = false
                    )
                    ledgerBleOperationManager.startLedgerOperation(
                        ExternalTransactionOperation(device, transaction),
                        currentIndex,
                        pending.rawTransactionBytes.size
                    )
                }
            }
        }

        override fun onScanError(errorMessageResId: Int, titleResId: Int) {
            clear()
            onError(TransactionManagerResult.LedgerScanFailed)
        }
    }

    fun startLedgerSign(
        proposal: JointAccountTransactionSignHelper.PendingJointAccountProposal,
        scanCallback: CustomScanCallback,
        coroutineScope: CoroutineScope,
        onError: () -> Unit
    ) {
        val rawTransactionBytes = proposal.rawTransactionLists.flatten().map { base64 ->
            runCatching { android.util.Base64.decode(base64, android.util.Base64.DEFAULT) }.getOrNull()
        }

        if (rawTransactionBytes.any { it == null }) {
            onError()
            return
        }

        currentIndex = 0
        signedTransactions.clear()
        pendingSign = PendingJointAccountLedgerSign(
            proposal = proposal,
            rawTransactionBytes = rawTransactionBytes.filterNotNull()
        )

        val bluetoothAddress = proposal.ledgerAccount.deviceMacAddress
        val currentConnectedDevice = ledgerBleOperationManager.connectedBluetoothDevice
        if (currentConnectedDevice != null && currentConnectedDevice.address == bluetoothAddress) {
            scanCallback.onLedgerScanned(currentConnectedDevice, 0, rawTransactionBytes.size)
        } else {
            ledgerBleSearchManager.scan(
                newScanCallback = scanCallback,
                filteredAddress = bluetoothAddress,
                coroutineScope = coroutineScope
            )
        }
    }

    suspend fun handleSignResult(
        signedTransactionData: ByteArray,
        scanCallback: CustomScanCallback,
        onResult: (TransactionManagerResult) -> Unit,
        onError: () -> Unit
    ) {
        signedTransactions.add(signedTransactionData)
        currentIndex++

        val pending = pendingSign ?: run {
            onError()
            return
        }

        if (currentIndex < pending.rawTransactionBytes.size) {
            val currentConnectedDevice = ledgerBleOperationManager.connectedBluetoothDevice
            if (currentConnectedDevice != null) {
                scanCallback.onLedgerScanned(
                    currentConnectedDevice,
                    currentIndex,
                    pending.rawTransactionBytes.size
                )
            } else {
                clear()
                onError()
            }
        } else {
            completeProposalAfterLedgerSign(pending, onResult, onError)
        }
    }

    fun clear() {
        pendingSign = null
        signedTransactions.clear()
        currentIndex = 0
    }

    private suspend fun completeProposalAfterLedgerSign(
        pending: PendingJointAccountLedgerSign,
        onResult: (TransactionManagerResult) -> Unit,
        onError: () -> Unit
    ) {
        val signatureBase64List = signedTransactions.mapNotNull { signedTx ->
            extractSignatureFromSignedTransaction(signedTx)?.let { Encoder.encodeToBase64(it) }
        }

        if (signatureBase64List.size != pending.rawTransactionBytes.size) {
            clear()
            onError()
            return
        }

        val result = jointAccountTransactionSignHelper.completeJointAccountProposal(
            pendingProposal = pending.proposal,
            signatureBase64List = signatureBase64List
        )

        clear()

        when (result) {
            is JointAccountTransactionSignHelper.JointSignResult.Success -> {
                onResult(TransactionManagerResult.Success.TransactionRequestSigned(result.signRequestId))
            }

            else -> onError()
        }
    }

    private fun extractSignatureFromSignedTransaction(signedTx: ByteArray): ByteArray? {
        return runCatching {
            val signedTransaction = Encoder.decodeFromMsgPack(signedTx, SignedTransaction::class.java)
            signedTransaction.sig?.bytes
        }.getOrNull()
    }

    private data class PendingJointAccountLedgerSign(
        val proposal: JointAccountTransactionSignHelper.PendingJointAccountProposal,
        val rawTransactionBytes: List<ByteArray>
    )

    private class JointAccountLedgerTransaction(
        override val transactionByteArray: ByteArray,
        override val accountAddress: String,
        override val accountAuthAddress: String?,
        override val isRekeyedToAnotherAccount: Boolean
    ) : ExternalTransaction
}
