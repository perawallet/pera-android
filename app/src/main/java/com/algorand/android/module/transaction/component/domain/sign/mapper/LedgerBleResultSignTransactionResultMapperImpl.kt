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

package com.algorand.android.module.transaction.component.domain.sign.mapper

import com.algorand.android.module.ledger.domain.model.LedgerBleResult
import com.algorand.android.module.ledger.domain.model.LedgerBleResult.ErrorResult.ConnectionError
import com.algorand.android.module.ledger.domain.model.LedgerBleResult.ErrorResult.LedgerError
import com.algorand.android.module.ledger.domain.model.LedgerBleResult.ErrorResult.NetworkError
import com.algorand.android.module.ledger.domain.model.LedgerBleResult.ErrorResult.ReconnectLedger
import com.algorand.android.module.ledger.domain.model.LedgerBleResult.ErrorResult.TransmissionError
import com.algorand.android.module.ledger.domain.model.LedgerBleResult.ErrorResult.UnsupportedDeviceError
import com.algorand.android.module.ledger.domain.model.LedgerBleResult.LedgerWaitingForApproval
import com.algorand.android.module.ledger.domain.model.LedgerBleResult.OnBondingFailed
import com.algorand.android.module.ledger.domain.model.LedgerBleResult.OnLedgerDisconnected
import com.algorand.android.module.ledger.domain.model.LedgerBleResult.OnMissingBytes
import com.algorand.android.module.ledger.domain.model.LedgerBleResult.OperationCancelledResult
import com.algorand.android.module.ledger.domain.model.LedgerBleResult.SignedTransactionResult
import com.algorand.android.module.transaction.component.domain.model.SignedTransaction
import com.algorand.android.module.transaction.component.domain.sign.model.SignTransactionResult
import com.algorand.android.module.transaction.component.domain.sign.model.SignTransactionResult.TransactionSigned
import javax.inject.Inject

internal class LedgerBleResultSignTransactionResultMapperImpl @Inject constructor() :
    LedgerBleResultSignTransactionResultMapper {

    override fun invoke(result: LedgerBleResult): SignTransactionResult {
        return when (result) {
            ConnectionError -> SignTransactionResult.Error.LedgerConnectionError
            is LedgerError -> SignTransactionResult.Error.LedgerError(result.errorMessage)
            NetworkError -> SignTransactionResult.Error.NetworkError
            ReconnectLedger -> SignTransactionResult.Error.ReconnectToLedgerError
            TransmissionError -> SignTransactionResult.Error.TransmissionError
            UnsupportedDeviceError -> SignTransactionResult.Error.UnsupportedDeviceError
            is LedgerWaitingForApproval -> SignTransactionResult.LedgerWaitingForApproval(result.bluetoothName)
            OnBondingFailed -> SignTransactionResult.Error.BondingFailed
            OnLedgerDisconnected -> SignTransactionResult.Error.LedgerDisconnected
            is OnMissingBytes -> SignTransactionResult.Error.MissingBytes(result.device)
            OperationCancelledResult -> SignTransactionResult.Error.OperationCancelled
            is SignedTransactionResult -> TransactionSigned(SignedTransaction(result.transactionByteArray))
            else -> {
                throw IllegalArgumentException("Unknown result type: $result")
            }
        }
    }
}
