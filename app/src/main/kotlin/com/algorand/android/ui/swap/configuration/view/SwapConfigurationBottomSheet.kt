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

@file:OptIn(ExperimentalMaterial3Api::class)
@file:Suppress("MagicNumber")

package com.algorand.android.ui.swap.configuration.view

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.PeraSwitch
import com.algorand.android.ui.compose.widget.PeraToolbar
import com.algorand.android.ui.compose.widget.PeraToolbarIcon
import com.algorand.android.ui.compose.widget.PeraToolbarTextButton
import com.algorand.android.ui.compose.widget.bottomsheet.PeraModalBottomSheet
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple
import com.algorand.android.ui.compose.widget.textfield.PeraTextField
import com.algorand.android.ui.compose.widget.textfield.PeraTextFieldColors
import com.algorand.android.ui.swap.configuration.model.SwapConfigurationResult
import com.algorand.android.ui.swap.configuration.view.ChipOption.Companion.CUSTOM_SLIPPAGE_VALUE
import com.algorand.android.ui.swap.viewmodel.SwapViewModel
import com.algorand.android.utils.emptyString
import com.algorand.android.utils.extensions.capitalizeWords
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

private const val MAX_BALANCE = 100
private const val MIN_BALANCE = 1
private const val MAX_SLIPPAGE = 10f
private const val MIN_SLIPPAGE = 0.01f

@Composable
fun SwapConfigurationBottomSheet(
    sheetState: SheetState,
    swapViewModel: SwapViewModel,
    scope: CoroutineScope,
    onDismissRequest: () -> Unit,
    onApplyClick: (SwapConfigurationResult) -> Unit
) {
    PeraModalBottomSheet(sheetState = sheetState, onDismissRequest = onDismissRequest) {
        val swapDetails = swapViewModel.getSwapDetails()
        val balanceTextState = remember { mutableStateOf(TextFieldValue(emptyString())) }
        val slippageTextState = remember { mutableStateOf(TextFieldValue(swapDetails.slippage?.toString().orEmpty())) }
        val localCurrencyState = remember { mutableStateOf(swapDetails.useLocalCurrency) }
        val isBalanceErrorVisible by isBalanceErrorVisible(balanceTextState)
        val isSlippageErrorVisible by isSlippageErrorVisible(slippageTextState)
        val isApplyButtonEnabled by remember(isBalanceErrorVisible, isSlippageErrorVisible) {
            derivedStateOf { !isBalanceErrorVisible && !isSlippageErrorVisible }
        }
        Row {
            PeraToolbar(
                text = stringResource(R.string.swap_settings),
                startContainer = {
                    Spacer(modifier = Modifier.width(12.dp))
                    PeraToolbarIcon(
                        modifier = Modifier.clickableNoRipple {
                            scope.launch { swapViewModel.logSettingsCancelClick() }
                            onDismissRequest()
                        },
                        iconResId = R.drawable.ic_close
                    )
                },
                endContainer = {
                    PeraToolbarTextButton(text = stringResource(R.string.apply), enabled = isApplyButtonEnabled) {
                        val result = SwapConfigurationResult(
                            balancePercentage = balanceTextState.value.text.toFloatOrNull(),
                            slippageTolerance = slippageTextState.value.text.toDoubleOrNull(),
                            useLocalCurrency = localCurrencyState.value
                        )
                        scope.launch { swapViewModel.logSettingsApplyClick() }
                        onApplyClick(result)
                    }
                    Spacer(modifier = Modifier.width(24.dp))
                }
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        BalancePercentageInputContainer(scope, isBalanceErrorVisible, swapViewModel, balanceTextState)
        Spacer(modifier = Modifier.height(40.dp))
        SlippageInputContainer(scope, isSlippageErrorVisible, swapViewModel, slippageTextState)
        Spacer(modifier = Modifier.height(40.dp))
        LocalCurrencyToggle(scope, swapViewModel, localCurrencyState)
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun BalancePercentageInputContainer(
    scope: CoroutineScope,
    isErrorVisible: Boolean,
    swapViewModel: SwapViewModel,
    textState: MutableState<TextFieldValue>
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        val textInput = textState.value
        val percentageChips = getBalancePercentageChips()
        TextInputField(
            title = stringResource(R.string.balance_percentage),
            hint = stringResource(R.string.set_custom_percentage),
            text = textInput,
            onTextChanged = { textState.value = it.copy(text = it.text.replaceCommaWithDot()) }
        )
        ErrorText(
            error = stringResource(R.string.balance_percentage_must_be_between, MIN_BALANCE, MAX_BALANCE),
            isVisible = isErrorVisible
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 24.dp)
        ) {
            items(percentageChips) { chip ->
                ChipButton(
                    text = chip.text,
                    isSelected = textInput.text.toFloatOrNull() == chip.value,
                    onClick = {
                        scope.launch { swapViewModel.logBalancePercentageSelection(chip.value) }
                        textState.update(chip.value.formattedValue(0))
                    }
                )
            }
        }
    }
}

@Composable
private fun SlippageInputContainer(
    scope: CoroutineScope,
    isErrorVisible: Boolean,
    swapViewModel: SwapViewModel,
    textState: MutableState<TextFieldValue>
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        val textInput = textState.value
        val slippageChips = getSlippageChips()
        TextInputField(
            title = stringResource(R.string.slippage_tolerance),
            hint = stringResource(R.string.set_custom_slippage),
            text = textInput,
            onTextChanged = { textState.value = it.copy(text = it.text.replaceCommaWithDot()) }
        )
        ErrorText(
            error = stringResource(R.string.percentage_must_be_between, MIN_SLIPPAGE, MAX_SLIPPAGE),
            isVisible = isErrorVisible
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 24.dp)
        ) {
            items(slippageChips) { chip ->
                val isCustomButton = chip.value == CUSTOM_SLIPPAGE_VALUE
                val text = textInput.text
                val isSelected = if (isCustomButton) {
                    text.isNotEmpty() && slippageChips.none { it.value == text.toFloatOrNull() }
                } else {
                    text.toFloatOrNull() == chip.value
                }
                ChipButton(
                    text = chip.text,
                    isSelected = isSelected,
                    onClick = {
                        val newText = if (isCustomButton) emptyString() else chip.value.formattedValue(1)
                        if (!isCustomButton) scope.launch { swapViewModel.logSlippageSelection(chip.value) }
                        textState.update(newText.replaceCommaWithDot())
                    }
                )
            }
        }
    }
}

@Composable
private fun ErrorText(error: String, isVisible: Boolean) {
    val alphaAnimation = animateFloatAsState(if (isVisible) 1f else 0f)
    Row(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 4.dp)
            .alpha(alphaAnimation.value),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_error),
            tint = PeraTheme.colors.helper.negative,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = error,
            style = PeraTheme.typography.footnote.sansMedium,
            color = PeraTheme.colors.helper.negative
        )
    }
}

@Composable
private fun isSlippageErrorVisible(textState: MutableState<TextFieldValue>): State<Boolean> {
    return remember(textState.value.text) {
        derivedStateOf {
            textState.value.text.toFloatOrNull()?.let { it != 0f && (it !in MIN_SLIPPAGE..MAX_SLIPPAGE) } == true
        }
    }
}

@Composable
private fun isBalanceErrorVisible(textState: MutableState<TextFieldValue>): State<Boolean> {
    return remember(textState.value.text) {
        derivedStateOf {
            textState.value.text.toFloatOrNull()?.let { it != 0f && (it !in 1.0..100.0) } == true
        }
    }
}

private fun String.replaceCommaWithDot(): String = this.replace(",", ".")

private fun Float.formattedValue(maxDecimal: Int): String {
    return DecimalFormat("0", DecimalFormatSymbols(Locale.getDefault())).apply {
        maximumFractionDigits = maxDecimal
    }.format(this)
}

private fun MutableState<TextFieldValue>.update(text: String) {
    value = TextFieldValue(text, TextRange(text.length))
}

@Composable
private fun LocalCurrencyToggle(
    scope: CoroutineScope,
    swapViewModel: SwapViewModel,
    localCurrencyState: MutableState<Boolean>
) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text(
            text = stringResource(R.string.primary_currency),
            style = PeraTheme.typography.footnote.sans,
            color = PeraTheme.colors.text.gray
        )
        Row(
            modifier = Modifier.padding(top = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.use_local_currency),
                style = PeraTheme.typography.body.regular.sans,
                color = PeraTheme.colors.text.main
            )
            Spacer(modifier = Modifier.weight(1f))
            PeraSwitch(
                checked = localCurrencyState.value,
                onCheckedChange = { enabled ->
                    with(swapViewModel) {
                        scope.launch { if (enabled) logLocalCurrencyEnabled() else logLocalCurrencyDisabled() }
                    }
                    localCurrencyState.value = enabled
                }
            )
        }
    }
}

@Composable
private fun TextInputField(title: String, hint: String, text: TextFieldValue, onTextChanged: (TextFieldValue) -> Unit) {
    Text(
        modifier = Modifier.padding(horizontal = 24.dp),
        text = title,
        style = PeraTheme.typography.footnote.sans,
        color = PeraTheme.colors.text.gray
    )
    Spacer(modifier = Modifier.height(8.dp))
    PeraTextField(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth(),
        textFieldValue = text,
        onTextChanged = onTextChanged,
        hint = hint.capitalizeWords(),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        textStyle = PeraTheme.typography.body.large.sansMedium.copy(color = PeraTheme.colors.text.main),
        colors = PeraTextFieldColors.defaultColors().copy(
            focusedIndicatorColor = PeraTheme.colors.text.grayLighter,
            unfocusedIndicatorColor = PeraTheme.colors.text.grayLighter
        )
    )
}

@Composable
private fun ChipButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val (textColor, backgroundColor) = if (isSelected) {
        PeraTheme.colors.helper.positive to PeraTheme.colors.helper.positiveLighter
    } else {
        PeraTheme.colors.button.secondary.text to PeraTheme.colors.button.secondary.background
    }
    Text(
        modifier = Modifier
            .background(color = backgroundColor, shape = RoundedCornerShape(48.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickableNoRipple(onClick = onClick),
        text = text,
        style = PeraTheme.typography.body.regular.sansMedium,
        color = textColor
    )
}

@Composable
private fun getBalancePercentageChips(): List<ChipOption> {
    return listOf(
        ChipOption(25f, stringResource(R.string.formatted_percentage, "25")),
        ChipOption(50f, stringResource(R.string.formatted_percentage, "50")),
        ChipOption(75f, stringResource(R.string.formatted_percentage, "75")),
        ChipOption(100f, stringResource(R.string.max).uppercase())
    )
}

@Composable
private fun getSlippageChips(): List<ChipOption> {
    return listOf(
        ChipOption(CUSTOM_SLIPPAGE_VALUE, stringResource(R.string.custom)),
        ChipOption(0.5f, stringResource(R.string.formatted_percentage, "0.5")),
        ChipOption(1f, stringResource(R.string.formatted_percentage, "1")),
        ChipOption(2f, stringResource(R.string.formatted_percentage, "2")),
        ChipOption(5f, stringResource(R.string.formatted_percentage, "5"))
    )
}

private data class ChipOption(val value: Float, val text: String) {
    companion object {
        const val CUSTOM_SLIPPAGE_VALUE = -1f
    }
}
