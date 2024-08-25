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

package com.algorand.android.module_new.walletconnect

import com.algorand.android.models.WalletConnectSignResult.Error
import com.algorand.android.transaction.domain.sign.model.SignTransactionResult
import com.algorand.android.transactionui.core.mapper.SignTransactionUiResultMapper
import com.algorand.android.transactionui.core.model.SignTransactionUiResult.BluetoothNotEnabled
import com.algorand.android.transactionui.core.model.SignTransactionUiResult.BluetoothPermissionsAreNotGranted
import com.algorand.android.transactionui.core.model.SignTransactionUiResult.Error.GlobalWarningError
import com.algorand.android.transactionui.core.model.SignTransactionUiResult.LedgerDisconnected
import com.algorand.android.transactionui.core.model.SignTransactionUiResult.LedgerOperationCancelled
import com.algorand.android.transactionui.core.model.SignTransactionUiResult.LedgerScanFailed
import com.algorand.android.transactionui.core.model.SignTransactionUiResult.LedgerWaitingForApproval
import com.algorand.android.transactionui.core.model.SignTransactionUiResult.LocationNotEnabled
import javax.inject.Inject

internal class SignWalletConnectTransactionResultMapperImpl @Inject constructor(
    private val signTransactionUiResultMapper: SignTransactionUiResultMapper
) : SignWalletConnectTransactionResultMapper {

    override fun invoke(result: SignTransactionResult): SignWalletConnectTransactionResult? {
        return when (val uiResult = signTransactionUiResultMapper(result)) {
            BluetoothNotEnabled -> SignWalletConnectTransactionResult.BluetoothNotEnabled
            BluetoothPermissionsAreNotGranted -> SignWalletConnectTransactionResult.BluetoothPermissionsAreNotGranted
            is GlobalWarningError.Api -> SignWalletConnectTransactionResult.Error.Api(uiResult.errorMessage)
            is GlobalWarningError.Defined -> {
                SignWalletConnectTransactionResult.Error.Defined(uiResult.description, uiResult.titleResId)
            }
            LedgerDisconnected -> SignWalletConnectTransactionResult.LedgerDisconnected
            LedgerOperationCancelled -> SignWalletConnectTransactionResult.LedgerOperationCancelled
            LedgerScanFailed -> SignWalletConnectTransactionResult.LedgerScanFailed
            is LedgerWaitingForApproval -> {
                SignWalletConnectTransactionResult.LedgerWaitingForApproval(uiResult.ledgerName)
            }
            LocationNotEnabled -> SignWalletConnectTransactionResult.LocationNotEnabled
            else -> {
                // Other cases should be handled by the caller.
                null
            }
        }
    }

    override fun invoke(error: Error): SignWalletConnectTransactionResult {
        return when (error) {
            is Error.Api -> SignWalletConnectTransactionResult.Error.Api(error.errorMessage, error.titleResId)
            is Error.Defined -> SignWalletConnectTransactionResult.Error.Defined(error.description, error.titleResId)
        }
    }
}
