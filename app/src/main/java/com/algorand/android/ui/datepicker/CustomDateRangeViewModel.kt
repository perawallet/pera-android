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

package com.algorand.android.ui.datepicker

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.algorand.android.core.BaseViewModel
import com.algorand.android.module.date.ui.model.CustomDateRangePreview
import com.algorand.android.module.date.ui.model.DateFilter.CustomRange
import com.algorand.android.module.date.ui.usecase.GetCustomDateRangePreview
import com.algorand.android.module.date.ui.usecase.GetDefaultCustomRange
import com.algorand.android.module.date.ui.usecase.GetUpdatedCustomRange
import com.algorand.android.utils.getOrElse
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.ZonedDateTime
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@HiltViewModel
class CustomDateRangeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCustomDateRangePreview: GetCustomDateRangePreview,
    private val getUpdatedCustomRange: GetUpdatedCustomRange,
    getDefaultCustomRange: GetDefaultCustomRange
) : BaseViewModel() {

    private val initialCustomRange = savedStateHandle.getOrElse(CUSTOM_RANGE_KEY, getDefaultCustomRange())

    private val _customDateRangeFlow = MutableStateFlow(initialCustomRange)
    private val _isFromFocusedFlow = MutableStateFlow(true)

    fun getInitialZonedDateTime(): ZonedDateTime {
        return initialCustomRange.customDateRange?.from ?: throw Exception("Initial zoned date time cannot be null")
    }

    fun getCustomRange(): CustomRange {
        return _customDateRangeFlow.value
    }

    fun setIsFromFocused(isFromFocused: Boolean) {
        viewModelScope.launch {
            _isFromFocusedFlow.emit(isFromFocused)
        }
    }

    fun updateCustomRange(datePickerYear: Int, datePickerMonth: Int, datePickerDay: Int) {
        viewModelScope.launch {
            val customRange = getUpdatedCustomRange(
                customRange = _customDateRangeFlow.value,
                isFromFocused = _isFromFocusedFlow.value,
                datePickerYear = datePickerYear,
                datePickerMonth = datePickerMonth,
                datePickerDay = datePickerDay,
            )
            _customDateRangeFlow.emit(customRange)
        }
    }

    fun getCustomDateRangePreviewFlow(): Flow<CustomDateRangePreview> {
        return combine(_customDateRangeFlow, _isFromFocusedFlow) { customRange, isFromFocused ->
            createCustomDateRangePreview(customRange, isFromFocused)
        }
    }

    private fun createCustomDateRangePreview(
        latestCustomRange: CustomRange,
        isFromFocused: Boolean
    ): CustomDateRangePreview {
        return getCustomDateRangePreview(
            latestDateRange = latestCustomRange.customDateRange,
            isFromFocused = isFromFocused
        )
    }

    companion object {
        private const val CUSTOM_RANGE_KEY = "customRange"
    }
}
