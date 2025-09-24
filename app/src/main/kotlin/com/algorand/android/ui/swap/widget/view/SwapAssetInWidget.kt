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

package com.algorand.android.ui.swap.widget.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.models.AnnotatedString
import com.algorand.android.modules.currency.domain.model.Currency
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.ShimmerTextBox
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple
import com.algorand.android.ui.compose.widget.textfield.AmountInputTextField
import com.algorand.android.ui.compose.widget.textfield.DecimalFormattedVisualTransformation
import com.algorand.android.ui.swap.viewmodel.SwapViewModel
import com.algorand.android.ui.swap.widget.viewmodel.SwapAssetSelectionViewModel
import com.algorand.android.ui.swap.widget.viewmodel.SwapWidgetViewModel
import com.algorand.android.ui.swap.widget.viewmodel.SwapWidgetViewModel.ViewState
import com.algorand.android.ui.swap.widget.viewmodel.SwapWidgetViewModel.ViewState.Content
import com.algorand.android.ui.swap.widget.viewmodel.SwapWidgetViewModel.ViewState.Loading
import com.algorand.android.utils.getXmlStyledString
import com.algorand.wallet.swap.domain.model.SwapQuoteDetail
import com.algorand.wallet.swap.domain.model.SwapQuoteException.InsufficientAlgoBalance
import com.algorand.wallet.swap.domain.model.SwapQuoteException.InsufficientAssetBalance
import com.algorand.wallet.swap.domain.model.SwapQuoteException.InsufficientBalanceForFee

@Composable
fun SwapAssetInWidget(
    swapViewModel: SwapViewModel,
    widgetViewModel: SwapWidgetViewModel,
    assetSelectionViewModel: SwapAssetSelectionViewModel,
    onAssetChipClick: () -> Unit
) {
    Column {
        val viewState = widgetViewModel.state.collectAsStateWithLifecycle().value
        val swapDetails = swapViewModel.swapDetailsFlow.collectAsStateWithLifecycle().value
        SwapAssetWidget(
            title = stringResource(R.string.you_pay),
            amountContent = { AssetInAmountContent(swapDetails, viewState, widgetViewModel) },
            viewModel = assetSelectionViewModel,
            assetSelectionChipBackgroundColor = PeraTheme.colors.layer.grayLightest,
            onAssetChipClick = onAssetChipClick
        )
        ErrorText(viewState)
    }
}

@Composable
private fun RowScope.AssetInAmountContent(
    swapDetails: SwapViewModel.SwapDetails,
    viewState: ViewState,
    widgetViewModel: SwapWidgetViewModel
) {
    val assetInAmount by widgetViewModel.getAmountInputFlow().collectAsStateWithLifecycle("")
    val focusRequester = remember { FocusRequester() }
    Column(
        modifier = Modifier
            .clickableNoRipple { focusRequester.requestFocus() }
            .weight(1f)) {
        val textFieldValue = TextFieldValue(assetInAmount, TextRange(assetInAmount.length))
        Row {
            if (textFieldValue.text.isNotBlank() && swapDetails.useLocalCurrency) {
                Text(
                    text = swapDetails.primaryCurrencySymbol,
                    style = PeraTheme.typography.body.large.sansMedium,
                    color = PeraTheme.colors.text.main,
                )
            }
            AssetInAmountInputTextField(viewState, focusRequester, textFieldValue) {
                widgetViewModel.setAmountInput(it.text)
            }
        }
        SecondaryAmountText(viewState)
    }
}

@Composable
private fun AssetInAmountInputTextField(
    viewState: ViewState,
    focusRequester: FocusRequester,
    textFieldValue: TextFieldValue,
    onTextChanged: (TextFieldValue) -> Unit,
) {
    AmountInputTextField(
        modifier = Modifier
            .focusRequester(focusRequester)
            .fillMaxWidth(),
        textFieldValue = textFieldValue,
        hint = (viewState as? Content)?.amountRenderers?.assetInPrimaryAmountHint?.getDisplayValue(),
        onTextChanged = onTextChanged,
        textStyle = PeraTheme.typography.body.large.sansMedium,
        visualTransformation = DecimalFormattedVisualTransformation()
    )
}

@Composable
private fun SecondaryAmountText(viewState: ViewState) {
    val textStyle = PeraTheme.typography.footnote.sans
    when (viewState) {
        is Content -> {
            Text(
                text = viewState.amountRenderers.assetInSecondaryAmount.getDisplayValue(),
                style = textStyle,
                color = PeraTheme.colors.text.gray
            )
        }
        Loading -> ShimmerTextBox(textStyle, width = 80.dp)
    }
}

@Composable
private fun ErrorText(viewState: ViewState) {
    val viewQuoteState = ((viewState as? Content)?.contentState as? Content.ContentState.Quote) ?: return
    val quoteState = viewQuoteState.selectedQuoteDetail.state
    if (quoteState is SwapQuoteDetail.SwapQuoteState.NonSwappable) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_error),
                tint = PeraTheme.colors.helper.negative,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = getErrorMessage(quoteState),
                style = PeraTheme.typography.footnote.sansMedium,
                color = PeraTheme.colors.helper.negative
            )
        }
    }
}

@Composable
private fun getErrorMessage(quoteState: SwapQuoteDetail.SwapQuoteState.NonSwappable): String {
    return when (quoteState.exception) {
        InsufficientAlgoBalance -> stringResource(R.string.account_does_not_have)
        is InsufficientAssetBalance -> {
            val assetUnitName = (quoteState.exception as InsufficientAssetBalance).assetUnitName
            if (!assetUnitName.isNullOrBlank()) {
                val annotatedString = AnnotatedString(
                    stringResId = R.string.asa_balance_is_not_sufficient_formatted,
                    replacementList = listOf("asa_short_name" to assetUnitName)
                )
                LocalContext.current.getXmlStyledString(annotatedString).toString()
            } else {
                stringResource(R.string.asa_balance_is_not_sufficient)
            }
        }
        is InsufficientBalanceForFee -> {
            val minBalance = (quoteState.exception as InsufficientBalanceForFee).minRequiredBalance
            val annotatedString = AnnotatedString(
                stringResId = R.string.algo_balance_is_too_low,
                replacementList = listOf(
                    "algo_icon" to Currency.ALGO.symbol,
                    "min_balance" to minBalance.stripTrailingZeros().toPlainString()
                )
            )
            LocalContext.current.getXmlStyledString(annotatedString).toString()
        }
    }
}
