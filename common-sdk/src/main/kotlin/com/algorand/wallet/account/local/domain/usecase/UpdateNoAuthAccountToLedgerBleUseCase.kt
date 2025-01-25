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

package com.algorand.wallet.account.local.domain.usecase

import com.algorand.wallet.account.local.domain.model.LocalAccount
import javax.inject.Inject

internal class UpdateNoAuthAccountToLedgerBleUseCase @Inject constructor(
    private val deleteLocalAccount: DeleteLocalAccount,
    private val saveLedgerBleAccount: SaveLedgerBleAccount
) : UpdateNoAuthAccountToLedgerBle {

    override suspend fun invoke(address: String, deviceMacAddress: String, bluetoothName: String, indexInLedger: Int) {
        deleteLocalAccount(address)
        saveLedgerBleAccount(LocalAccount.LedgerBle(address, deviceMacAddress, bluetoothName, indexInLedger))
    }
}
