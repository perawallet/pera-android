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

package co.algorand.app.ui.screens.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.algorand.app.ui.screens.accounts.AccountsViewModel.ViewState
import com.algorand.common.account.info.domain.model.AccountInformation
import com.algorand.common.account.info.domain.usecase.GetAllAccountInformationFlow
import com.algorand.common.account.local.domain.model.LocalAccount
import com.algorand.common.account.local.domain.usecase.AddAlgo25Account
import com.algorand.common.account.local.domain.usecase.AddBip39Account
import com.algorand.common.account.local.domain.usecase.DeleteLocalAccount
import com.algorand.common.account.local.domain.usecase.GetAllLocalAccountAddressesAsFlow
import com.algorand.common.algosdk.AlgoAccountSdk
import com.algorand.common.viewmodel.StateDelegate
import com.algorand.common.viewmodel.StateViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

class AccountsViewModel(
    private val getAllLocalAccountAddressesAsFlow: GetAllLocalAccountAddressesAsFlow,
    private val addBip39Account: AddBip39Account,
    private val addAlgo25Account: AddAlgo25Account,
    private val algoAccountSdk: AlgoAccountSdk,
    private val deleteLocalAccount: DeleteLocalAccount,
    private val stateDelegate: StateDelegate<ViewState>,
    private val getAllAccountInformationFlow: GetAllAccountInformationFlow
) : ViewModel(), StateViewModel<ViewState> by stateDelegate {

    private var accountObserveJob: Job? = null

    init {
        stateDelegate.setDefaultState(ViewState.Idle)
    }

    fun recoverAccount(mnemonic: String) {
        val account = algoAccountSdk.recoverAlgo25Account(mnemonic)
        if (account != null) {
            val localAccount = LocalAccount.Algo25(
                address = account.address,
                secretKey = account.secretKey
            )
            viewModelScope.launch {
                addAlgo25Account(localAccount)
            }
        }
    }

    fun initAccounts() {
        if (accountObserveJob != null) return
        accountObserveJob = viewModelScope.launch {
            combine(
                getAllLocalAccountAddressesAsFlow().distinctUntilChanged(),
                getAllAccountInformationFlow().distinctUntilChanged()
            ) { localAccounts, cachedAccounts ->
                val accounts = mutableMapOf<String, AccountInformation?>()
                localAccounts.forEach { address ->
                    accounts[address] = cachedAccounts[address]
                }
                stateDelegate.updateState { ViewState.Accounts(accounts) }
            }.launchIn(this)
        }
    }

    fun addAlgo25Account() {
        viewModelScope.launch {
            val account = algoAccountSdk.createAlgo25Account()
            val algo25Account = LocalAccount.Algo25(
                address = account.address,
                secretKey = account.secretKey
            )
            addAlgo25Account(algo25Account)
        }
    }

    fun addBip39Account() {
        viewModelScope.launch {
            val account = algoAccountSdk.createBip39Account()
            val bip39Account = LocalAccount.Bip39(
                address = account.address,
                secretKey = account.secretKey
            )
            addBip39Account(bip39Account)
        }
    }

    fun deleteAccount(address: String) {
        viewModelScope.launch {
            deleteLocalAccount(address)
        }
    }

    sealed interface ViewState {
        data object Idle : ViewState
        data class Accounts(val accounts: Map<String, AccountInformation?>) : ViewState
    }
}
