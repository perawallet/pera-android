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

package com.algorand.android.modules.swap.slippagetolerance.ui.usecase

import android.content.res.Resources
import com.algorand.android.R
import com.algorand.android.customviews.PeraChipGroup
import com.algorand.android.mapper.PeraChipItemMapper
import com.algorand.android.models.PeraFloatChipItem
import com.algorand.android.modules.swap.slippagetolerance.ui.mapper.SlippageTolerancePreviewMapper
import com.algorand.android.modules.swap.slippagetolerance.ui.model.SlippageTolerancePreview
import com.algorand.android.modules.swap.slippagetolerance.ui.util.MAX_SWAP_SLIPPAGE_TOLERANCE
import com.algorand.android.modules.swap.slippagetolerance.ui.util.MIN_SWAP_SLIPPAGE_TOLERANCE
import com.algorand.android.utils.Event
import com.algorand.android.utils.toFloatOrDefault
import javax.inject.Inject

class SlippageTolerancePreviewUseCase @Inject constructor(
    private val slippageTolerancePreviewMapper: SlippageTolerancePreviewMapper,
    private val peraChipItemMapper: PeraChipItemMapper
) {

    fun getSlippageTolerancePreview(resources: Resources, previousSelectedTolerance: Float): SlippageTolerancePreview {
        val dividedSelectedTolerance = previousSelectedTolerance / SLIPPAGE_TOLERANCE_DIVIDER
        val slippageToleranceOptions = getSlippageToleranceOptionList(resources, dividedSelectedTolerance)
        val defaultSelectedOptionIndex = slippageToleranceOptions.indexOfFirst {
            it.value == dividedSelectedTolerance
        }
        return slippageTolerancePreviewMapper.mapToSlippageTolerancePreview(
            slippageToleranceList = slippageToleranceOptions,
            checkedToleranceOption = slippageToleranceOptions[defaultSelectedOptionIndex],
            returnResultEvent = null,
            errorString = null,
            requestFocusToInputEvent = null,
            prefilledAmountInputValue = previousSelectedTolerance.takeIf {
                defaultSelectedOptionIndex == CUSTOM_OPTION_CHIP_INDEX
            }?.run { Event(toString()) }
        )
    }

    fun getChipItemSelectedUpdatedPreview(
        chipIndex: Int,
        peraChipItem: PeraChipGroup.PeraChipItem,
        previousState: SlippageTolerancePreview
    ): SlippageTolerancePreview {
        return (peraChipItem as? PeraFloatChipItem)?.run {
            if (chipIndex == CUSTOM_OPTION_CHIP_INDEX) {
                previousState.copy(
                    requestFocusToInputEvent = Event(Unit),
                    checkedOption = peraChipItem
                )
            } else {
                previousState.copy(returnResultEvent = Event(value))
            }
        } ?: previousState
    }

    fun getDoneClickUpdatedPreview(
        inputValue: String,
        previousState: SlippageTolerancePreview
    ): SlippageTolerancePreview {
        return with(previousState) {
            val selectedChipIndex = chipOptionList.indexOfFirst { it.value == checkedOption?.value }
            val isSelectedChipPredefined = selectedChipIndex != CUSTOM_OPTION_CHIP_INDEX && selectedChipIndex != -1

            if (isSelectedChipPredefined) {
                copy(returnResultEvent = Event(chipOptionList[selectedChipIndex].value))
            } else {
                val inputAsFloat = inputValue.toFloatOrNull() ?: -1f
                if (!isInputValid(inputAsFloat)) {
                    previousState
                } else {
                    copy(returnResultEvent = Event(inputAsFloat / SLIPPAGE_TOLERANCE_DIVIDER))
                }
            }
        }
    }

    fun getCustomItemUpdatedPreview(
        resources: Resources,
        inputValue: String,
        previousPreview: SlippageTolerancePreview
    ): SlippageTolerancePreview {
        if (inputValue.isBlank()) return previousPreview.copy(errorString = null)
        val inputAsFloat = inputValue.toFloatOrDefault()
        val errorString = if (!isInputValid(inputAsFloat)) {
            resources.getString(
                R.string.percentage_must_be_between,
                MIN_SWAP_SLIPPAGE_TOLERANCE,
                MAX_SWAP_SLIPPAGE_TOLERANCE
            )
        } else {
            null
        }
        return previousPreview.copy(
            checkedOption = previousPreview.chipOptionList[CUSTOM_OPTION_CHIP_INDEX],
            errorString = errorString
        )
    }

    private fun isInputValid(value: Float): Boolean {
        return value in MIN_SWAP_SLIPPAGE_TOLERANCE..MAX_SWAP_SLIPPAGE_TOLERANCE
    }

    private fun getSlippageToleranceOptionList(
        resources: Resources,
        previousSelectedTolerance: Float
    ): MutableList<PeraFloatChipItem> {
        return mutableListOf<PeraFloatChipItem>().apply {
            add(CUSTOM_OPTION_CHIP_INDEX, createCustomItem(resources, previousSelectedTolerance))
            add(
                peraChipItemMapper.mapToPeraChipItem(
                    labelText = resources.getString(R.string.zero_point_five_percent),
                    value = ZERO_POINT_FIVE_PERCENTAGE_SLIPPAGE
                )
            )
            add(
                peraChipItemMapper.mapToPeraChipItem(
                    labelText = resources.getString(R.string.one_percent),
                    value = TEN_PERCENTAGE_SLIPPAGE
                )
            )
            add(
                peraChipItemMapper.mapToPeraChipItem(
                    labelText = resources.getString(R.string.two_percent),
                    value = TWENTY_PERCENTAGE_SLIPPAGE
                )
            )
            add(
                peraChipItemMapper.mapToPeraChipItem(
                    labelText = resources.getString(R.string.five_percent),
                    value = FIFTY_PERCENTAGE_SLIPPAGE
                )
            )
        }
    }

    private fun createCustomItem(resources: Resources, customSlippageTolerance: Float): PeraFloatChipItem {
        val customInputValue = if (isCustomInputAlsoPredefinedValue(customSlippageTolerance)) {
            CUSTOM_OPTION_DEFAULT_VALUE
        } else {
            customSlippageTolerance
        }
        return peraChipItemMapper.mapToPeraChipItem(
            labelText = resources.getString(R.string.custom),
            value = customInputValue
        )
    }

    private fun isCustomInputAlsoPredefinedValue(customSlippageTolerance: Float): Boolean {
        return customSlippageTolerance == ZERO_POINT_FIVE_PERCENTAGE_SLIPPAGE ||
            customSlippageTolerance == TEN_PERCENTAGE_SLIPPAGE ||
            customSlippageTolerance == TWENTY_PERCENTAGE_SLIPPAGE ||
            customSlippageTolerance == FIFTY_PERCENTAGE_SLIPPAGE
    }

    companion object {
        private const val CUSTOM_OPTION_CHIP_INDEX = 0
        private const val CUSTOM_OPTION_DEFAULT_VALUE = -1f
        private const val ZERO_POINT_FIVE_PERCENTAGE_SLIPPAGE = 0.005f
        private const val TEN_PERCENTAGE_SLIPPAGE = 0.01f
        private const val TWENTY_PERCENTAGE_SLIPPAGE = 0.02f
        private const val FIFTY_PERCENTAGE_SLIPPAGE = 0.05f
        private const val SLIPPAGE_TOLERANCE_DIVIDER = 100f
    }
}
