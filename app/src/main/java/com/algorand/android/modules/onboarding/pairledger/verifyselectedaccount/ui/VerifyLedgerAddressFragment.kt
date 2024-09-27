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

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navGraphViewModels
import com.algorand.android.R
import com.algorand.android.core.DaggerBaseFragment
import com.algorand.android.databinding.FragmentVerifyLedgerAddressBinding
import com.algorand.android.module.foundation.Event
import com.algorand.android.module.ledger.domain.model.LedgerBleResult
import com.algorand.android.module.ledger.domain.model.LedgerOperation.VerifyAddressOperation
import com.algorand.android.module.ledger.manager.LedgerBleOperationManager
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.modules.onboarding.pairledger.PairLedgerNavigationViewModel
import com.algorand.android.modules.onboarding.pairledger.verifyselectedaccount.ui.adapter.VerifiableLedgerAddressesAdapter
import com.algorand.android.modules.onboarding.pairledger.verifyselectedaccount.ui.model.VerifyLedgerAddressListItem
import com.algorand.android.modules.rekey.model.SelectedLedgerAccount
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.sendErrorLog
import com.algorand.android.utils.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VerifyLedgerAddressFragment : DaggerBaseFragment(R.layout.fragment_verify_ledger_address) {

    @Inject
    lateinit var ledgerBleOperationManager: LedgerBleOperationManager

    private val toolbarConfiguration = ToolbarConfiguration(
        startIconResId = R.drawable.ic_left_arrow,
        startIconClick = ::navBack
    )

    override val fragmentConfiguration = FragmentConfiguration(toolbarConfiguration = toolbarConfiguration)

    private val binding by viewBinding(FragmentVerifyLedgerAddressBinding::bind)

    private val adapter = VerifiableLedgerAddressesAdapter()

    private val verifyLedgerAddressViewModel: VerifyLedgerAddressViewModel by viewModels()

    private val pairLedgerNavigationViewModel: PairLedgerNavigationViewModel by navGraphViewModels(
        R.id.pairLedgerNavigation
    ) {
        defaultViewModelProviderFactory
    }

    // <editor-fold defaultstate="collapsed" desc="Observers">

    private val listObserver = Observer<List<VerifyLedgerAddressListItem>> { list ->
        adapter.submitList(list)
    }

    private val isAllOperationDoneObserver = Observer<Event<Boolean>?> { isAllOperationDoneEvent ->
        isAllOperationDoneEvent?.consume()?.let { isAllOperationDone ->
            binding.confirmationButton.isVisible = isAllOperationDone
        }
    }

    private val accountCreationCompletionObserver: suspend (Event<Int>?) -> Unit = {
        it?.consume()?.let { createdAccountCount ->
            val direction = VerifyLedgerAddressFragmentDirections
                .actionVerifyLedgerAddressFragmentToVerifyLedgerInfoFragment(createdAccountCount)
            nav(direction)
        }
    }

    private val ledgerResultCollector: suspend (Event<LedgerBleResult>?) -> Unit = { ledgerBleResultEvent ->
        ledgerBleResultEvent?.consume()?.let { ledgerBleResult ->
            when (ledgerBleResult) {
                is LedgerBleResult.OnLedgerDisconnected -> {
                    retryCurrentOperation()
                }
                is LedgerBleResult.OperationCancelledResult -> {
                    verifyLedgerAddressViewModel.onCurrentOperationDone(isVerified = false)
                }
                is LedgerBleResult.VerifyPublicKeyResult -> {
                    verifyLedgerAddressViewModel.onCurrentOperationDone(isVerified = ledgerBleResult.isVerified)
                }
                is LedgerBleResult.ErrorResult.TransmissionError -> {
                    showLedgerGlobalErrorAndRetry(R.string.error_receiving_message, R.string.error_transmission_title)
                }
                is LedgerBleResult.ErrorResult.ConnectionError -> {
                    showLedgerGlobalErrorAndRetry(R.string.error_connection_message, R.string.error_connection_title)
                }
                is LedgerBleResult.ErrorResult.UnsupportedDeviceError -> {
                    showLedgerGlobalErrorAndRetry(R.string.error_unsupported_message, R.string.error_unsupported_title)
                }
                is LedgerBleResult.ErrorResult.NetworkError -> {
                    showLedgerGlobalErrorAndRetry(R.string.a_network_error, R.string.error_connection_title)
                }
                is LedgerBleResult.ErrorResult.ReconnectLedger -> {
                    showLedgerGlobalErrorAndRetry(R.string.it_appears_this, R.string.error)
                }
                is LedgerBleResult.ErrorResult.LedgerError -> {
                    showGlobalError(errorMessage = ledgerBleResult.errorMessage)
                    retryCurrentOperation()
                }
                else -> {
                    sendErrorLog("Unhandled else case in ledgerResultCollector")
                }
            }
        }
    }
    // </editor-fold>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLedgerBleOperationManager()
        setupViewModel()
        setupLedgerAddressesRecyclerView()
        initObservers()
        binding.confirmationButton.setOnClickListener { onConfirmationClick() }
    }

    private fun setupLedgerBleOperationManager() {
        ledgerBleOperationManager.setup(viewLifecycleOwner.lifecycle)
    }

    private fun setupLedgerAddressesRecyclerView() {
        binding.ledgerAddressesRecyclerView.adapter = adapter
    }

    private fun setupViewModel() {
        val selectedLedgerAccounts = pairLedgerNavigationViewModel.selectedLedgerAccounts
        if (selectedLedgerAccounts == null) {
            navBack()
            return
        }
        verifyLedgerAddressViewModel.createListAuthLedgerAccounts(selectedLedgerAccounts)
    }

    private fun showLedgerGlobalErrorAndRetry(@StringRes errorResId: Int, @StringRes titleResId: Int) {
        showGlobalError(errorMessage = getString(errorResId), title = getString(titleResId))
        retryCurrentOperation()
    }

    private fun startVerifyOperation(account: SelectedLedgerAccount.LedgerAccount?) {
        if (account == null) {
            return
        }
        val currentOperatedLedger = pairLedgerNavigationViewModel.pairedLedger
        if (currentOperatedLedger == null) {
            sendErrorLog("Ledger is not found while operating startVerifyOperation function.")
            return
        }
        val ledgerOperation = VerifyAddressOperation(currentOperatedLedger, account.indexInLedger, account.address)
        ledgerBleOperationManager.startVerifyAddressOperation(ledgerOperation)
    }

    private fun retryCurrentOperation() {
        viewLifecycleOwner.lifecycleScope.launch {
            delay(RETRY_DELAY)
            startVerifyOperation(verifyLedgerAddressViewModel.awaitingLedgerAccount)
        }
    }

    private fun initObservers() {
        verifyLedgerAddressViewModel.currentLedgerAddressesListLiveData.observe(viewLifecycleOwner, listObserver)

        verifyLedgerAddressViewModel.isVerifyOperationsDoneLiveData.observe(
            viewLifecycleOwner,
            isAllOperationDoneObserver
        )

        viewLifecycleOwner.collectLatestOnLifecycle(
            ledgerBleOperationManager.ledgerBleResultFlow,
            ledgerResultCollector
        )

        viewLifecycleOwner.collectLatestOnLifecycle(
            verifyLedgerAddressViewModel.accountCreationCompletionFlow,
            accountCreationCompletionObserver
        )

        verifyLedgerAddressViewModel.awaitingLedgerAccountLiveData.observe(viewLifecycleOwner) {
            startVerifyOperation(it)
        }
    }

    private fun onConfirmationClick() {
        val selectedLedgerAccounts = pairLedgerNavigationViewModel.selectedLedgerAccounts ?: return
        verifyLedgerAddressViewModel.addSelectedVerifiedAccounts(selectedLedgerAccounts)
    }

    companion object {
        private const val RETRY_DELAY = 1000L
    }
}
