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

package com.algorand.android.transaction.domain.sign.model

import android.bluetooth.BluetoothDevice
import com.algorand.android.transaction.domain.model.SignedTransaction

sealed interface SignTransactionResult {

    data class TransactionSigned(val signedTransaction: SignedTransaction) : SignTransactionResult

    sealed interface Error : SignTransactionResult {

        data object NetworkError : Error

        data object ReconnectToLedgerError : Error

        data object TransmissionError : Error

        data object UnsupportedDeviceError : Error

        data object BluetoothNotEnabled : Error

        data object BluetoothPermissionsNotGranted : Error

        data object LocationNotEnabled : Error

        data object ErrorWhileSigning : Error

        data class AuthAccountNotFound(val address: String) : Error

        data object ScannedLedgerNotFoundError : Error

        data object LedgerConnectionError : Error

        data class LedgerError(val message: String) : Error

        data object BondingFailed : Error

        data object LedgerDisconnected : Error

        data class MissingBytes(val device: BluetoothDevice) : Error

        data object OperationCancelled : Error
    }

    data class LedgerWaitingForApproval(val ledgerName: String?) : SignTransactionResult
}
