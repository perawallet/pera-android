/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.onboarding.pairledger.verifyselectedaccount.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.algorand.android.core.BaseViewModel
import com.algorand.android.models.AccountCreation
import com.algorand.android.modules.onboarding.pairledger.verifyselectedaccount.ui.model.VerifiableLedgerAddressItemStatus
import com.algorand.android.modules.onboarding.pairledger.verifyselectedaccount.ui.model.VerifyLedgerAddressListItem
import com.algorand.android.modules.onboarding.pairledger.verifyselectedaccount.util.VerifyLedgerAddressQueueManager
import com.algorand.android.modules.rekey.model.SelectedLedgerAccount
import com.algorand.android.modules.rekey.model.SelectedLedgerAccount.LedgerAccount
import com.algorand.android.modules.rekey.model.SelectedLedgerAccounts
import com.algorand.android.usecase.AccountAdditionUseCase
import com.algorand.android.utils.Event
import com.algorand.android.utils.analytics.CreationType
import com.algorand.android.utils.launchIO
import com.algorand.android.utils.toShortenedAddress
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VerifyLedgerAddressViewModel @Inject constructor(
    private val verifyLedgerAddressQueueManager: VerifyLedgerAddressQueueManager,
    private val accountAdditionUseCase: AccountAdditionUseCase
) : BaseViewModel() {

    val currentLedgerAddressesListLiveData = MutableLiveData<List<VerifyLedgerAddressListItem>>()

    val awaitingLedgerAccountLiveData = MutableLiveData<LedgerAccount?>()

    val awaitingLedgerAccount
        get() = awaitingLedgerAccountLiveData.value

    val isVerifyOperationsDoneLiveData = MutableLiveData<Event<Boolean>?>()

    private val listLock = Any()

    private val verifyLedgerAddressQueueManagerListener = object : VerifyLedgerAddressQueueManager.Listener {
        override fun onNextQueueItem(ledgerDetail: LedgerAccount) {
            awaitingLedgerAccountLiveData.value = ledgerDetail
            changeCurrentOperatedAddressStatus(VerifiableLedgerAddressItemStatus.AWAITING_VERIFICATION)
        }

        override fun onQueueCompleted() {
            awaitingLedgerAccountLiveData.postValue(null)
            isVerifyOperationsDoneLiveData.postValue(Event(true))
        }
    }

    init {
        verifyLedgerAddressQueueManager.setListener(verifyLedgerAddressQueueManagerListener)
    }

    fun createListAuthLedgerAccounts(selectedAccounts: SelectedLedgerAccounts?) {
        if (selectedAccounts == null) return
        val ledgerAddresses = getVerifyRequiredLedgerAddresses(selectedAccounts)
        val verifiableLedgerAddress: List<VerifyLedgerAddressListItem> = ledgerAddresses.map { account ->
            VerifyLedgerAddressListItem.VerifiableLedgerAddressItem(account.address)
        }
        verifiableLedgerAddress.toMutableList().add(0, VerifyLedgerAddressListItem.VerifyLedgerHeaderItem)
        currentLedgerAddressesListLiveData.value = verifiableLedgerAddress
        verifyLedgerAddressQueueManager.fillQueue(ledgerAddresses)
    }

    private fun getVerifyRequiredLedgerAddresses(selectedAccounts: SelectedLedgerAccounts): List<LedgerAccount> {
        val addressDetailMap = mutableMapOf<String, LedgerAccount>()
        selectedAccounts.ledgerAccounts.forEach {
            addressDetailMap[it.address] = it
        }
        selectedAccounts.rekeyedAccounts.forEach {
            addressDetailMap[it.authDetail.address] = it.authDetail
        }
        return addressDetailMap.values.toList()
    }

    fun onCurrentOperationDone(isVerified: Boolean) {
        changeCurrentOperatedAddressStatus(
            if (isVerified) {
                VerifiableLedgerAddressItemStatus.APPROVED
            } else {
                VerifiableLedgerAddressItemStatus.REJECTED
            }
        )
        moveToNextVerification()
    }

    private fun moveToNextVerification() {
        verifyLedgerAddressQueueManager.moveQueue()
    }

    fun changeCurrentOperatedAddressStatus(newStatus: VerifiableLedgerAddressItemStatus) {
        synchronized(listLock) {
            val currentList = currentLedgerAddressesListLiveData.value
            val currentOperatedAddress = awaitingLedgerAccount?.address
            if (currentList != null && currentOperatedAddress != null) {
                val newList = mutableListOf<VerifyLedgerAddressListItem>().apply {
                    add(VerifyLedgerAddressListItem.VerifyLedgerHeaderItem)
                }
                currentList
                    .filterIsInstance<VerifyLedgerAddressListItem.VerifiableLedgerAddressItem>()
                    .forEach {
                        val changedStatus = if (it.address == currentOperatedAddress) newStatus else it.status
                        val copyItem = it.copy(status = changedStatus)
                        newList.add(copyItem)
                    }
                currentLedgerAddressesListLiveData.value = newList
            }
        }
    }

    private fun getAllApprovedAuths(): List<VerifyLedgerAddressListItem.VerifiableLedgerAddressItem> {
        return currentLedgerAddressesListLiveData.value
            ?.filterIsInstance<VerifyLedgerAddressListItem.VerifiableLedgerAddressItem>()
            ?.filter { it.status == VerifiableLedgerAddressItemStatus.APPROVED }
            .orEmpty()
    }

    fun getSelectedVerifiedAccounts(allSelectedAccounts: SelectedLedgerAccounts?): List<SelectedLedgerAccount> {
        val approvedLedgerAuths = getAllApprovedAuths()
        if (approvedLedgerAuths.isEmpty()) {
            return emptyList()
        }
        val accountList = mutableListOf<SelectedLedgerAccount>()
        allSelectedAccounts?.rekeyedAccounts?.forEach { rekeyedAccount ->
            if (approvedLedgerAuths.any { it.address == rekeyedAccount.authDetail.address }) {
                accountList.add(rekeyedAccount)
            }
        }
        allSelectedAccounts?.ledgerAccounts?.forEach { ledgerAccount ->
            if (approvedLedgerAuths.any { it.address == ledgerAccount.address }) {
                accountList.add(ledgerAccount)
            }
        }
        return accountList
    }

    fun addNewAccount(accounts: List<SelectedLedgerAccount>) {
        viewModelScope.launchIO {
            accounts.forEach { selectedAccount ->
                val accountCreation = when (selectedAccount) {
                    is SelectedLedgerAccount.RekeyedAccount -> createNoAuthAccount(selectedAccount)
                    is LedgerAccount -> createLedgerAccount(selectedAccount)
                }
                accountAdditionUseCase.addNewAccount(accountCreation)
            }
        }
    }

    private fun createLedgerAccount(selectedAccount: LedgerAccount): AccountCreation {
        return AccountCreation(
            address = selectedAccount.address,
            customName = selectedAccount.address.toShortenedAddress(),
            isBackedUp = true,
            type = AccountCreation.Type.LedgerBle(
                deviceMacAddress = selectedAccount.bleAddress,
                bluetoothName = selectedAccount.bleName,
                indexInLedger = selectedAccount.indexInLedger
            ),
            creationType = CreationType.LEDGER
        )
    }

    private fun createNoAuthAccount(selectedAccount: SelectedLedgerAccount.RekeyedAccount): AccountCreation {
        return AccountCreation(
            address = selectedAccount.address,
            customName = selectedAccount.address.toShortenedAddress(),
            isBackedUp = true,
            type = AccountCreation.Type.NoAuth,
            creationType = CreationType.REKEYED
        )
    }
}
