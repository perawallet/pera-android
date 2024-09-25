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

package com.algorand.android.module.ledger.manager

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.Lifecycle
import com.algorand.android.foundation.Event
import com.algorand.android.module.ledger.domain.model.LedgerBleResult
import com.algorand.android.module.ledger.domain.model.LedgerOperation
import kotlinx.coroutines.flow.StateFlow

interface LedgerBleOperationManager {

    val connectedBluetoothDevice: BluetoothDevice?

    val ledgerBleResultFlow: StateFlow<Event<LedgerBleResult>?>

    fun setup(lifecycle: Lifecycle)

    fun startVerifyAddressOperation(operation: LedgerOperation.VerifyAddressOperation)

    fun startFetchAllAccountsOperation(operation: LedgerOperation.AccountFetchAllOperation)

    fun startTransactionOperation(operation: LedgerOperation.TransactionOperation)

    fun stopAllProcess()
}
