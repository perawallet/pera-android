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

package com.algorand.android.module.swap.ui.confirmswap.mapper

import com.algorand.android.module.swap.ui.confirmswap.model.SignSwapTransactionResult
import com.algorand.android.module.swap.ui.confirmswap.model.SignSwapTransactionResult.Error.Api
import com.algorand.android.module.swap.ui.confirmswap.model.SignSwapTransactionResult.Error.Defined
import com.algorand.android.module.transaction.component.domain.sign.model.SignTransactionResult
import com.algorand.android.module.transaction.ui.core.mapper.SignTransactionUiResultMapper
import com.algorand.android.module.transaction.ui.core.model.SignTransactionUiResult.BluetoothNotEnabled
import com.algorand.android.module.transaction.ui.core.model.SignTransactionUiResult.BluetoothPermissionsAreNotGranted
import com.algorand.android.module.transaction.ui.core.model.SignTransactionUiResult.Error
import com.algorand.android.module.transaction.ui.core.model.SignTransactionUiResult.LedgerDisconnected
import com.algorand.android.module.transaction.ui.core.model.SignTransactionUiResult.LedgerOperationCancelled
import com.algorand.android.module.transaction.ui.core.model.SignTransactionUiResult.LedgerScanFailed
import com.algorand.android.module.transaction.ui.core.model.SignTransactionUiResult.LedgerWaitingForApproval
import com.algorand.android.module.transaction.ui.core.model.SignTransactionUiResult.LocationNotEnabled
import javax.inject.Inject

internal class SignSwapTransactionResultMapperImpl @Inject constructor(
    private val signTransactionUiResultMapper: SignTransactionUiResultMapper
) : SignSwapTransactionResultMapper {

    override fun invoke(result: SignTransactionResult): SignSwapTransactionResult? {
        return when (val uiResult = signTransactionUiResultMapper(result)) {
            BluetoothNotEnabled -> SignSwapTransactionResult.BluetoothNotEnabled
            BluetoothPermissionsAreNotGranted -> SignSwapTransactionResult.BluetoothPermissionsAreNotGranted
            is Error.GlobalWarningError.Api -> Api(uiResult.errorMessage)
            is Error.GlobalWarningError.Defined -> Defined(uiResult.description, uiResult.titleResId)
            LedgerDisconnected -> SignSwapTransactionResult.LedgerDisconnected
            LedgerOperationCancelled -> SignSwapTransactionResult.LedgerOperationCancelled
            LedgerScanFailed -> SignSwapTransactionResult.LedgerScanFailed
            is LedgerWaitingForApproval -> SignSwapTransactionResult.LedgerWaitingForApproval(uiResult.ledgerName)
            LocationNotEnabled -> SignSwapTransactionResult.LocationNotEnabled
            else -> {
                // Other cases should be handled by the caller.
                null
            }
        }
    }
}
