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

package com.algorand.android.module.transaction.ui.core.mapper

import android.bluetooth.BluetoothDevice
import com.algorand.android.R
import com.algorand.android.module.drawable.AnnotatedString
import com.algorand.android.module.transaction.component.domain.sign.model.SignTransactionResult
import com.algorand.android.module.transaction.component.domain.sign.model.SignTransactionResult.Error.AuthAccountNotFound
import com.algorand.android.module.transaction.component.domain.sign.model.SignTransactionResult.Error.BluetoothNotEnabled
import com.algorand.android.module.transaction.component.domain.sign.model.SignTransactionResult.Error.BluetoothPermissionsNotGranted
import com.algorand.android.module.transaction.component.domain.sign.model.SignTransactionResult.Error.ErrorWhileSigning
import com.algorand.android.module.transaction.component.domain.sign.model.SignTransactionResult.Error.LedgerConnectionError
import com.algorand.android.module.transaction.component.domain.sign.model.SignTransactionResult.Error.LedgerDisconnected
import com.algorand.android.module.transaction.component.domain.sign.model.SignTransactionResult.Error.LedgerError
import com.algorand.android.module.transaction.component.domain.sign.model.SignTransactionResult.Error.LocationNotEnabled
import com.algorand.android.module.transaction.component.domain.sign.model.SignTransactionResult.Error.MissingBytes
import com.algorand.android.module.transaction.component.domain.sign.model.SignTransactionResult.Error.NetworkError
import com.algorand.android.module.transaction.component.domain.sign.model.SignTransactionResult.Error.OperationCancelled
import com.algorand.android.module.transaction.component.domain.sign.model.SignTransactionResult.Error.ReconnectToLedgerError
import com.algorand.android.module.transaction.component.domain.sign.model.SignTransactionResult.Error.ScannedLedgerNotFoundError
import com.algorand.android.module.transaction.component.domain.sign.model.SignTransactionResult.Error.TransmissionError
import com.algorand.android.module.transaction.component.domain.sign.model.SignTransactionResult.Error.UnsupportedDeviceError
import com.algorand.android.module.transaction.component.domain.sign.model.SignTransactionResult.LedgerWaitingForApproval
import com.algorand.android.module.transaction.ui.core.model.SignTransactionUiResult
import com.algorand.android.module.transaction.ui.core.model.SignTransactionUiResult.Error.GlobalWarningError
import com.algorand.android.module.transaction.ui.core.model.SignTransactionUiResult.Error.GlobalWarningError.Defined
import com.algorand.android.module.transaction.ui.core.model.SignTransactionUiResult.Error.SnackbarError
import com.algorand.android.module.transaction.ui.core.model.SignTransactionUiResult.TransactionSigned
import javax.inject.Inject

internal class SignTransactionUiResultMapperImpl @Inject constructor() : SignTransactionUiResultMapper {

    override fun invoke(result: SignTransactionResult): SignTransactionUiResult {
        return when (result) {
            is SignTransactionResult.TransactionSigned -> TransactionSigned(result.signedTransaction)
            is SignTransactionResult.Error -> {
                when (result) {
                    BluetoothPermissionsNotGranted -> SignTransactionUiResult.BluetoothPermissionsAreNotGranted
                    LocationNotEnabled -> SignTransactionUiResult.LocationNotEnabled
                    SignTransactionResult.Error.BondingFailed -> getBondingFailedErrorResult()
                    BluetoothNotEnabled -> SignTransactionUiResult.BluetoothNotEnabled
                    ScannedLedgerNotFoundError -> SignTransactionUiResult.LedgerScanFailed
                    LedgerDisconnected -> SignTransactionUiResult.LedgerDisconnected
                    ErrorWhileSigning -> getGenericErrorResult()
                    is AuthAccountNotFound -> getAuthAccountNotFoundErrorResult()
                    UnsupportedDeviceError -> getUnsupportedDeviceErrorResult()
                    NetworkError -> getNetworkErrorResult()
                    OperationCancelled -> SignTransactionUiResult.LedgerOperationCancelled
                    LedgerConnectionError -> getLedgerConnectionErrorResult()
                    is MissingBytes -> getMissingBytesError(result.device)
                    is LedgerError -> getLedgerErrorResult(result.message)
                    ReconnectToLedgerError -> getReconnectToLedgerErrorResult()
                    TransmissionError -> getTransmissionErrorResult()
                }
            }
            is LedgerWaitingForApproval -> SignTransactionUiResult.LedgerWaitingForApproval(result.ledgerName)
        }
    }

    private fun getBondingFailedErrorResult(): SignTransactionUiResult {
        return Defined(AnnotatedString(R.string.pairing_failed))
    }

    private fun getTransmissionErrorResult(): SignTransactionUiResult {
        return Defined(AnnotatedString(R.string.error_receiving_message), R.string.error_transmission_title)
    }

    private fun getReconnectToLedgerErrorResult(): SignTransactionUiResult {
        return Defined(AnnotatedString(R.string.it_appears_this), R.string.error)
    }

    private fun getLedgerErrorResult(message: String): SignTransactionUiResult {
        return GlobalWarningError.Api(message, R.string.error_default_title)
    }

    private fun getMissingBytesError(device: BluetoothDevice): SignTransactionUiResult {
        return Defined(AnnotatedString(R.string.error_sending_message), R.string.error_bluetooth_title)
    }

    private fun getLedgerConnectionErrorResult(): SignTransactionUiResult {
        return Defined(AnnotatedString(R.string.error_connection_message), R.string.error_connection_title)
    }

    private fun getOperationCancelledResult(): SignTransactionUiResult {
        return Defined(AnnotatedString(R.string.error_cancelled_message), R.string.error_cancelled_title)
    }

    private fun getNetworkErrorResult(): SignTransactionUiResult {
        return SnackbarError.Retry(R.string.a_network_error, R.string.error_connection_title)
    }

    private fun getUnsupportedDeviceErrorResult(): SignTransactionUiResult {
        return Defined(AnnotatedString(R.string.error_unsupported_message), R.string.error_unsupported_title)
    }

    private fun getAuthAccountNotFoundErrorResult(): SignTransactionUiResult {
        return Defined(AnnotatedString(R.string.the_signing_account_has))
    }

    private fun getGenericErrorResult(): SignTransactionUiResult {
        return Defined(AnnotatedString(R.string.an_error_occured))
    }
}
