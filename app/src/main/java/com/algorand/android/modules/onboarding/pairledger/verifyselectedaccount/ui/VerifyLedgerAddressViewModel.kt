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

package com.algorand.android.modules.onboarding.pairledger.verifyselectedaccount.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.algorand.android.core.BaseViewModel
import com.algorand.android.foundation.Event
import com.algorand.android.models.CreateAccount
import com.algorand.android.models.CreateAccount.LedgerBle
import com.algorand.android.modules.onboarding.pairledger.verifyselectedaccount.ui.model.VerifiableLedgerAddressItemStatus
import com.algorand.android.modules.onboarding.pairledger.verifyselectedaccount.ui.model.VerifyLedgerAddressListItem
import com.algorand.android.modules.onboarding.pairledger.verifyselectedaccount.util.VerifyLedgerAddressQueueManager
import com.algorand.android.modules.rekey.model.SelectedLedgerAccount.LedgerAccount
import com.algorand.android.modules.rekey.model.SelectedLedgerAccount.RekeyedAccount
import com.algorand.android.modules.rekey.model.SelectedLedgerAccounts
import com.algorand.android.usecase.AccountAdditionUseCase
import com.algorand.android.utils.analytics.CreationType
import com.algorand.android.utils.launchIO
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow

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

    val accountCreationCompletionFlow: MutableStateFlow<Event<Int>?> = MutableStateFlow(null)

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

    fun createListAuthLedgerAccounts(selectedLedgerAccounts: SelectedLedgerAccounts) {
        val authLedgerAccounts = mutableListOf<LedgerAccount>().apply {
            addAll(selectedLedgerAccounts.ledgerAccounts)
            selectedLedgerAccounts.rekeyedAccounts.forEach { rekeyedAccount ->
                val isAuthAlreadyInList = selectedLedgerAccounts.ledgerAccounts.any {
                    it.address == rekeyedAccount.authDetail.address
                }
                if (!isAuthAlreadyInList) {
                    add(rekeyedAccount.authDetail)
                }
            }
        }

        val verifiableLedgerAddress: List<VerifyLedgerAddressListItem> = authLedgerAccounts.map { ledgerAccount ->
            VerifyLedgerAddressListItem.VerifiableLedgerAddressItem(ledgerAccount.address)
        }
        verifiableLedgerAddress.toMutableList().add(0, VerifyLedgerAddressListItem.VerifyLedgerHeaderItem)
        currentLedgerAddressesListLiveData.value = verifiableLedgerAddress
        verifyLedgerAddressQueueManager.fillQueue(authLedgerAccounts)
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

    fun addSelectedVerifiedAccounts(selectedLedgerAccounts: SelectedLedgerAccounts) {
        val allSelectedAccounts = getSelectedVerifiedAccounts(selectedLedgerAccounts)
        val verifiedRekeyedAccounts = allSelectedAccounts?.rekeyedAccounts.orEmpty()
        val verifiedLedgerAccounts = allSelectedAccounts?.ledgerAccounts.orEmpty()

        viewModelScope.launchIO {
            verifiedRekeyedAccounts.forEach {
                val account = CreateAccount.NoAuth(it.address, null, isBackedUp = true, CreationType.REKEYED)
                accountAdditionUseCase.addNewAccount(account)
            }
            verifiedLedgerAccounts.forEach {
                val account = LedgerBle(
                    address = it.address,
                    customName = null,
                    isBackedUp = true,
                    deviceMacAddress = it.bleAddress,
                    indexInLedger = it.indexInLedger,
                    creationType = CreationType.LEDGER
                )
                accountAdditionUseCase.addNewAccount(account)
            }
            accountCreationCompletionFlow.emit(Event(verifiedRekeyedAccounts.size + verifiedLedgerAccounts.size))
        }
    }

    private fun getSelectedVerifiedAccounts(allSelectedAccounts: SelectedLedgerAccounts?): SelectedLedgerAccounts? {
        val approvedLedgerAuths = getAllApprovedAuths()
        if (approvedLedgerAuths.isEmpty() || allSelectedAccounts == null) {
            return null
        }
        val selectedRekeyedAccounts = mutableListOf<RekeyedAccount>()
        val selectedAuthAccounts = mutableListOf<LedgerAccount>()
        allSelectedAccounts.rekeyedAccounts.forEach { selectedRekeyedAccount ->
            if (approvedLedgerAuths.any { it.address == selectedRekeyedAccount.authDetail.address }) {
                selectedRekeyedAccounts.add(selectedRekeyedAccount)
            }
        }
        allSelectedAccounts.ledgerAccounts.forEach { selectedAuthAccount ->
            if (approvedLedgerAuths.any { it.address == selectedAuthAccount.address }) {
                selectedAuthAccounts.add(selectedAuthAccount)
            }
        }
        return SelectedLedgerAccounts(
            rekeyedAccounts = selectedRekeyedAccounts,
            ledgerAccounts = selectedAuthAccounts
        )
    }

    private fun getAllApprovedAuths(): List<VerifyLedgerAddressListItem.VerifiableLedgerAddressItem> {
        return currentLedgerAddressesListLiveData.value
            ?.filterIsInstance<VerifyLedgerAddressListItem.VerifiableLedgerAddressItem>()
            ?.filter { it.status == VerifiableLedgerAddressItemStatus.APPROVED }
            .orEmpty()
    }
}
