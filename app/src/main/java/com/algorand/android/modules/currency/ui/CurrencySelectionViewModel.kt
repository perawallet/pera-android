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

package com.algorand.android.modules.currency.ui

import androidx.lifecycle.viewModelScope
import com.algorand.android.module.appcache.usecase.RefreshSelectedCurrencyDetailCache
import com.algorand.android.core.BaseViewModel
import com.algorand.android.module.currency.domain.model.SelectedCurrency
import com.algorand.android.module.currency.domain.usecase.GetSelectedCurrency
import com.algorand.android.module.currency.domain.usecase.SetPrimaryCurrencyId
import com.algorand.android.models.ui.CurrencySelectionPreview
import com.algorand.android.modules.currency.ui.usecase.CurrencySelectionPreviewUseCase
import com.algorand.android.ui.settings.selection.CurrencyListItem
import com.algorand.android.utils.analytics.logCurrencyChange
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class CurrencySelectionViewModel @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics,
    private val setPrimaryCurrencyId: SetPrimaryCurrencyId,
    private val getSelectedCurrency: GetSelectedCurrency,
    private val currencySelectionPreviewUseCase: CurrencySelectionPreviewUseCase,
    private val refreshSelectedCurrencyDetailCache: RefreshSelectedCurrencyDetailCache
) : BaseViewModel() {

    val currencySelectionPreviewFlow: Flow<CurrencySelectionPreview?>
        get() = _currencySelectionPreviewFlow
    private val _currencySelectionPreviewFlow = MutableStateFlow<CurrencySelectionPreview?>(null)

    val selectedCurrencyFlow: Flow<SelectedCurrency>
        get() = _selectedCurrencyFlow
    private val _selectedCurrencyFlow = MutableStateFlow(getSelectedCurrency())

    private var previewJob: Job? = null

    private var searchKeyword = ""

    init {
        initPreviewFlow()
    }

    private fun initPreviewFlow() {
        previewJob = getPreviewJob()
    }

    fun refreshPreview() {
        previewJob?.cancel()
        previewJob = getPreviewJob()
    }

    fun updateSearchKeyword(searchKeyword: String) {
        this.searchKeyword = searchKeyword
        refreshPreview()
    }

    private fun getPreviewJob(): Job {
        return viewModelScope.launch {
            currencySelectionPreviewUseCase.getCurrencySelectionPreviewFlow(searchKeyword).collectLatest {
                _currencySelectionPreviewFlow.value = it
            }
        }
    }

    fun setCurrencySelected(currencyListItem: CurrencyListItem) {
        setPrimaryCurrencyId(currencyListItem.currencyId)
        logCurrencyChange(currencyListItem.currencyId)
        _selectedCurrencyFlow.value = getSelectedCurrency()
        viewModelScope.launch {
            refreshSelectedCurrencyDetailCache()
        }
    }

    private fun logCurrencyChange(newCurrencyId: String) {
        firebaseAnalytics.logCurrencyChange(newCurrencyId)
    }
}
