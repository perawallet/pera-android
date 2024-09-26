/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License
 *
 */

package com.algorand.android.ui.datepicker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.module.date.ui.model.DateFilter
import com.algorand.android.module.date.ui.model.DateFilter.AllTime
import com.algorand.android.module.date.ui.usecase.GetDateFilterList
import com.algorand.android.utils.getOrElse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class DateFilterListViewModel @Inject constructor(
    private val getDateFilterList: GetDateFilterList,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val selectedDateFilter: DateFilter = savedStateHandle.getOrElse(SELECTED_DATE_FILTER_KEY, AllTime)

    private val dateFilterList = DateFilter.getDateFilterList()

    private val _selectedDateFilterFlow = MutableStateFlow(selectedDateFilter)

    private val _dateFilterListLiveData = MutableLiveData<List<DateFilter>>()
    val dateFilterListLiveData: LiveData<List<DateFilter>> = _dateFilterListLiveData

    init {
        viewModelScope.launch {
            _selectedDateFilterFlow.collectLatest {
                getDateFilterListPreview(it)
            }
        }
    }

    private fun getDateFilterListPreview(dateFilter: DateFilter) {
        _dateFilterListLiveData.postValue(getDateFilterList(dateFilter, dateFilterList))
    }

    fun updateSelectedDate(dateFilter: DateFilter) {
        viewModelScope.launch {
            _selectedDateFilterFlow.emit(dateFilter)
        }
    }

    companion object {
        private const val SELECTED_DATE_FILTER_KEY = "selectedDateFilter"
    }
}
