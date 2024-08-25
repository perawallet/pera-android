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

package com.algorand.android.ledger.domain.model

import android.bluetooth.BluetoothDevice
import com.algorand.android.accountinfo.component.domain.model.AccountInformation

sealed class LedgerOperation {

    abstract val bluetoothDevice: BluetoothDevice
    var nextIndex: Int = 0

    data class VerifyAddressOperation(
        override val bluetoothDevice: BluetoothDevice,
        val indexOfAddress: Int,
        val address: String
    ) : LedgerOperation()

    data class AccountFetchAllOperation(
        override val bluetoothDevice: BluetoothDevice,
        val accounts: MutableList<AccountInformation>
    ) : LedgerOperation()

    data class TransactionOperation(
        override val bluetoothDevice: BluetoothDevice,
        val transactionByteArray: ByteArray?,
        val accountAddress: String,
        val isRekeyedToAnotherAccount: Boolean,
        val accountAuthAddress: String?,
        var isAddressVerified: Boolean = false
    ) : LedgerOperation()
}
