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

package com.algorand.android.modules.accountdetail.history.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.algorand.android.core.BaseFragment
import com.algorand.android.models.DateFilter
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.ui.compose.extensions.createComposeView
import com.algorand.android.ui.transaction.csv.model.CreateCsvArgs
import com.algorand.android.ui.transaction.csv.viewmodel.CsvViewModel
import com.algorand.android.ui.transaction.history.viewmodel.TransactionHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountTransactionHistoryFragment : BaseFragment(0), AccountHistoryScreenListener {

    override val fragmentConfiguration: FragmentConfiguration = FragmentConfiguration(isBottomBarNeeded = true)

    private val accountTransactionHistoryViewModel: AccountTransactionHistoryViewModel by viewModels()
    private val transactionHistoryViewModel: TransactionHistoryViewModel by viewModels()
    private val csvViewModel: CsvViewModel by viewModels()

    private var listener: AccountTransactionHistoryListener? = null

    private val address: String
        get() = arguments?.getString(ADDRESS_KEY).orEmpty()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return createComposeView {
            AccountTransactionHistoryScreen(csvViewModel, transactionHistoryViewModel, this)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = parentFragment as? AccountTransactionHistoryListener
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        transactionHistoryViewModel.initViewState(address, assetId = null)
    }

    override fun onFilterClick() {
        accountTransactionHistoryViewModel.logAccountHistoryFilterEventTracker()
        val currentFilter = transactionHistoryViewModel.getDateFilter()
        listener?.onFilterTransactionClick(currentFilter)
    }

    override fun onCsvClick() {
        accountTransactionHistoryViewModel.logAccountHistoryExportCsvEventTracker()
        context?.cacheDir?.let { cacheDir ->
            val args = CreateCsvArgs(
                cacheDirectory = cacheDir,
                address = address,
                assetId = null,
                dateRange = transactionHistoryViewModel.getSelectedDateRange()
            )
            csvViewModel.createCsv(args)
        }
    }

    override fun onTransactionClick(id: String) {
        listener?.onStandardTransactionClick(id)
    }

    override fun onApplicationCallClick(id: String) {
        listener?.onApplicationCallTransactionClick(id)
    }

    override fun onSwapClick(groupId: String) {
        listener?.onSwapTransactionClick(groupId)
    }

    interface AccountTransactionHistoryListener {
        fun onStandardTransactionClick(id: String)
        fun onApplicationCallTransactionClick(id: String)
        fun onSwapTransactionClick(groupId: String)
        fun onFilterTransactionClick(dateFilter: DateFilter)
    }

    companion object Companion {

        private const val ADDRESS_KEY = "address"

        fun newInstance(address: String): AccountTransactionHistoryFragment {
            return AccountTransactionHistoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ADDRESS_KEY, address)
                }
            }
        }
    }
}
