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

package com.algorand.android.transaction.domain.sign.usecase.implementation

import com.algorand.android.foundation.common.PeraSystemServiceManager
import com.algorand.android.foundation.permission.PeraPermissionManager
import com.algorand.android.transaction.domain.sign.model.TransactionSigner
import com.algorand.android.transaction.domain.sign.model.TransactionSigner.Algo25
import com.algorand.android.transaction.domain.sign.model.TransactionSigner.LedgerBle
import com.algorand.android.transaction.domain.sign.model.TransactionSigner.SignerNotFound
import com.algorand.android.transaction.domain.sign.model.SignTransactionPreparednessResult
import com.algorand.android.transaction.domain.sign.model.SignTransactionPreparednessResult.NotReadyToSign
import com.algorand.android.transaction.domain.sign.model.SignTransactionPreparednessResult.NotReadyToSign.BluetoothNotEnabled
import com.algorand.android.transaction.domain.sign.model.SignTransactionPreparednessResult.NotReadyToSign.BluetoothPermissionsNotGranted
import com.algorand.android.transaction.domain.sign.model.SignTransactionPreparednessResult.NotReadyToSign.LocationNotEnabled
import com.algorand.android.transaction.domain.sign.model.SignTransactionPreparednessResult.ReadyToSign
import com.algorand.android.transaction.domain.sign.usecase.GetSignTransactionPreparedness
import javax.inject.Inject

internal class GetSignTransactionPreparednessUseCase @Inject constructor(
    private val systemServiceManager: PeraSystemServiceManager,
    private val permissionManager: PeraPermissionManager
) : GetSignTransactionPreparedness {

    override fun invoke(signer: TransactionSigner): SignTransactionPreparednessResult {
        return when (signer) {
            is Algo25 -> ReadyToSign
            is LedgerBle -> getLedgerPreparednessResult()
            is SignerNotFound -> NotReadyToSign.SignerNotFound(signer)
        }
    }

    private fun getLedgerPreparednessResult(): SignTransactionPreparednessResult {
        return when {
            !permissionManager.areBluetoothPermissionsGranted() -> BluetoothPermissionsNotGranted
            !systemServiceManager.isBluetoothEnabled() -> BluetoothNotEnabled
            !systemServiceManager.isLocationEnabled() -> LocationNotEnabled
            else -> ReadyToSign
        }
    }
}
