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

package com.algorand.android.modules.onboarding.pairledger.search.ui

import android.bluetooth.BluetoothDevice
import androidx.navigation.navGraphViewModels
import com.algorand.android.R
import com.algorand.android.module.account.info.domain.model.AccountInformation
import com.algorand.android.modules.baseledgersearch.ledgersearch.ui.BaseLedgerSearchFragment
import com.algorand.android.modules.onboarding.pairledger.PairLedgerNavigationViewModel
import com.algorand.android.modules.onboarding.pairledger.accountselection.ui.model.RegisterLedgerAccountSelectionNavArgs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterLedgerSearchFragment : BaseLedgerSearchFragment() {

    override val fragmentId: Int = R.id.registerLedgerSearchFragment

    private val pairLedgerNavigationViewModel: PairLedgerNavigationViewModel by navGraphViewModels(
        R.id.pairLedgerNavigation
    ) { defaultViewModelProviderFactory }

    override fun onLedgerConnected(accountList: List<AccountInformation>, ledgerDevice: BluetoothDevice) {
        setLoadingVisibility(isVisible = false)
        pairLedgerNavigationViewModel.pairedLedger = ledgerDevice
        val navArgs = getLedgerAccountSelectionNavArgs(accountList, ledgerDevice)
        nav(
            RegisterLedgerSearchFragmentDirections
                .actionRegisterLedgerSearchFragmentToLedgerAccountSelectionFragment(navArgs)
        )
    }

    private fun getLedgerAccountSelectionNavArgs(
        accounts: List<AccountInformation>,
        device: BluetoothDevice
    ): RegisterLedgerAccountSelectionNavArgs {
        return RegisterLedgerAccountSelectionNavArgs(
            bluetoothAddress = device.address,
            bluetoothName = device.name,
            ledgerAccounts = accounts.map {
                RegisterLedgerAccountSelectionNavArgs.LedgerAccountsNavArgs(
                    isRekeyed = it.isRekeyed(),
                    address = it.address,
                    assetHoldingIds = it.getAssetHoldingIds(),
                    authAddress = it.rekeyAdminAddress
                )
            }
        )
    }

    override fun navToPairInstructionBottomSheet(bluetoothDevice: BluetoothDevice) {
        nav(
            RegisterLedgerSearchFragmentDirections
                .actionRegisterLedgerSearchFragmentToLedgerPairInstructionsBottomSheet(
                    bluetoothDevice
                )
        )
    }
}
