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

package com.algorand.android.module.ledger.domain.model

import android.bluetooth.BluetoothDevice
import com.algorand.android.accountinfo.component.domain.model.AccountInformation

sealed interface LedgerBleResult {

    data class LedgerWaitingForApproval(val bluetoothName: String?) : LedgerBleResult

    data class SignedTransactionResult(
        val transactionByteArray: ByteArray
    ) : LedgerBleResult

    data class AccountResult(
        val accountList: List<AccountInformation>,
        val bluetoothDevice: BluetoothDevice
    ) : LedgerBleResult

    data class VerifyPublicKeyResult(
        val isVerified: Boolean,
        val ledgerPublicKey: String,
        val originalPublicKey: String
    ) : LedgerBleResult

    sealed interface ErrorResult : LedgerBleResult {

        data class LedgerError(val errorMessage: String) : ErrorResult

        // LedgerBleResult.AppErrorResult(R.string.error_receiving_message, R.string.error_transmission_title)
        data object TransmissionError : ErrorResult

        // LedgerBleResult.AppErrorResult(R.string.error_connection, R.string.error_connection_title)
        data object ConnectionError : ErrorResult

        // LedgerBleResult.AppErrorResult(R.string.error_unsupported_message,R.string.error_unsupported_title)
        data object UnsupportedDeviceError : ErrorResult

        // LedgerBleResult.AppErrorResult(R.string.a_network_error, R.string.error_connection_title)
        data object NetworkError : ErrorResult

        // LedgerBleResult.AppErrorResult(R.string.it_appears_this, R.string.error)
        data object ReconnectLedger : ErrorResult
    }

    data object OnLedgerDisconnected : LedgerBleResult

    data object OperationCancelledResult : LedgerBleResult

    data object OnBondingFailed : LedgerBleResult

    data class OnMissingBytes(val device: BluetoothDevice) : LedgerBleResult
}
