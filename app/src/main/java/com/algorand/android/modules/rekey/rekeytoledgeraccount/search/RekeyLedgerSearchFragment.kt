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

package com.algorand.android.modules.rekey.rekeytoledgeraccount.search

import android.bluetooth.BluetoothDevice
import androidx.navigation.fragment.navArgs
import com.algorand.android.R
import com.algorand.android.module.account.info.domain.model.AccountInformation
import com.algorand.android.modules.baseledgersearch.ledgersearch.ui.BaseLedgerSearchFragment
import com.algorand.android.modules.rekey.rekeytoledgeraccount.accountselection.ui.model.RekeyLedgerAccountSelectionNavArgs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RekeyLedgerSearchFragment : BaseLedgerSearchFragment() {

    override val fragmentId: Int = R.id.rekeyLedgerSearchFragment

    private val args: RekeyLedgerSearchFragmentArgs by navArgs()

    override fun onLedgerConnected(accountList: List<AccountInformation>, ledgerDevice: BluetoothDevice) {
        setLoadingVisibility(isVisible = false)
        val navArgs = getRekeyToLedgerAccountSelectionNavArgs(accountList, ledgerDevice)
        nav(
            RekeyLedgerSearchFragmentDirections
                .actionRekeyLedgerSearchFragmentToRekeyToLedgerAccountSelectionFragment(navArgs)
        )
    }

    override fun navToPairInstructionBottomSheet(bluetoothDevice: BluetoothDevice) {
        nav(
            RekeyLedgerSearchFragmentDirections.actionRekeyLedgerSearchFragmentToLedgerPairInstructionsBottomSheet(
                bluetoothDevice
            )
        )
    }

    private fun getRekeyToLedgerAccountSelectionNavArgs(
        accountList: List<AccountInformation>,
        device: BluetoothDevice
    ): RekeyLedgerAccountSelectionNavArgs {
        return RekeyLedgerAccountSelectionNavArgs(
            accountAddress = args.accountAddress,
            bluetoothAddress = device.address,
            bluetoothName = device.name,
            ledgerAccounts = accountList.map {
                RekeyLedgerAccountSelectionNavArgs.LedgerAccountsNavArgs(
                    isRekeyed = it.isRekeyed(),
                    address = it.address,
                    assetHoldingIds = it.getAssetHoldingIds(),
                    authAddress = it.rekeyAdminAddress
                )
            }
        )
    }
}
