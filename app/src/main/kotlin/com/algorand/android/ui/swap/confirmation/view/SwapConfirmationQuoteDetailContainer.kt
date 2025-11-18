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

@file:OptIn(ExperimentalGlideComposeApi::class)

package com.algorand.android.ui.swap.confirmation.view

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.models.AnnotatedString
import com.algorand.android.ui.common.amount.AmountRenderer
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple
import com.algorand.android.ui.swap.confirmation.model.SwapPriceImpact
import com.algorand.android.ui.swap.confirmation.model.SwapPriceImpact.WarningStatus
import com.algorand.android.ui.swap.confirmation.model.SwapPriceImpact.WarningStatus.Level1
import com.algorand.android.ui.swap.confirmation.model.SwapPriceImpact.WarningStatus.Level2
import com.algorand.android.ui.swap.confirmation.model.SwapPriceImpact.WarningStatus.NoWarning
import com.algorand.android.ui.swap.confirmation.viewmodel.SwapConfirmationViewModel.ViewState.Content
import com.algorand.android.utils.getXmlStyledString
import com.algorand.wallet.swap.domain.model.SwapQuoteProvider
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

@Composable
fun SwapConfirmationQuoteDetailContainer(content: Content, listener: SwapConfirmationQuoteDetailContainerListener) {
    Box(
        modifier = Modifier
            .background(color = PeraTheme.colors.layer.grayLighter)
            .fillMaxWidth()
            .height(1.dp)
    )
    Spacer(modifier = Modifier.height(4.dp))
    Column(modifier = Modifier.padding(24.dp)) {
        Price(content)
        QuoteRowSeparator()
        Provider(content.quote.provider)
        QuoteRowSeparator()
        SlippageTolerance(content.slippage, listener::onSlippageToleranceInfoClick)
        QuoteRowSeparator()
        PriceImpact(content.priceImpact, listener::onPriceImpactInfoClick)
        QuoteRowSeparator()
        MinimumReceived(content.minReceivedAssetAmount)
        QuoteRowSeparator()
        ExchangeFee(content.exchangeFee, listener::onExchangeFeeInfoClick)
        QuoteRowSeparator()
        PeraFee(content.peraFee)
        QuoteRowSeparator()
        PriceImpactWarning(content.priceImpact.warningStatus, listener::onTinymanFaqPriceImpactUrlClick)
    }
}

interface SwapConfirmationQuoteDetailContainerListener {
    fun onSlippageToleranceInfoClick()
    fun onPriceImpactInfoClick()
    fun onExchangeFeeInfoClick()
    fun onTinymanFaqPriceImpactUrlClick()
}

@Composable
private fun QuoteRowSeparator() {
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun Price(content: Content) {
    var isAssetInFirst by remember { mutableStateOf(true) }
    QuoteDetailRow(
        labelContent = {
            QuoteDetailLabel(textResId = R.string.price)
        },
        valueContent = {
            val context = LocalContext.current
            val priceRatioText by remember {
                derivedStateOf {
                    val ratio = if (isAssetInFirst) content.assetInToOutPriceRatio else content.assetOutToInPriceRatio
                    getPriceRatioText(context, ratio)
                }
            }
            QuoteDetailValue(text = priceRatioText)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                modifier = Modifier.clickableNoRipple {
                    isAssetInFirst = !isAssetInFirst
                },
                painter = painterResource(R.drawable.ic_switch),
                tint = PeraTheme.colors.text.gray,
                contentDescription = null
            )
        }
    )
}

private fun getPriceRatioText(context: Context, priceRatio: Content.PriceRatio): String {
    val annotatedString = AnnotatedString(
        stringResId = R.string.from_to_to_asset_ratio,
        replacementList = listOf(
            "ratio" to priceRatio.ratio.getDisplayValue(),
            "firstAssetShortName" to priceRatio.firstAssetUnitName,
            "secondAssetShortName" to priceRatio.secondAssetUnitName
        )
    )
    return context.getXmlStyledString(annotatedString).toString()
}

@Composable
private fun Provider(provider: SwapQuoteProvider) {
    QuoteDetailRow(
        labelContent = { QuoteDetailLabel(textResId = R.string.provider) },
        valueContent = {
            GlideImage(
                model = provider.iconUrl,
                modifier = Modifier.size(16.dp),
                contentDescription = null,
            )
            Spacer(modifier = Modifier.width(4.dp))
            QuoteDetailValue(text = provider.displayName)
        }
    )
}

@Composable
private fun SlippageTolerance(slippage: String, onInfoClick: () -> Unit) {
    QuoteDetailRow(
        labelContent = {
            QuoteDetailLabel(textResId = R.string.slippage_tolerance)
            Spacer(modifier = Modifier.width(6.dp))
            InfoIcon(onClick = onInfoClick)
        },
        valueContent = { QuoteDetailValue(text = slippage) }
    )
}

@Composable
private fun PriceImpact(priceImpact: SwapPriceImpact, onInfoClick: () -> Unit) {
    val hasWarning = priceImpact.warningStatus !is NoWarning
    val warningColor = PeraTheme.colors.helper.negative
    QuoteDetailRow(
        labelContent = {
            val labelColor = if (hasWarning) warningColor else PeraTheme.colors.text.gray
            val infoIconColor = if (hasWarning) warningColor else PeraTheme.colors.text.grayLighter
            QuoteDetailLabel(textResId = R.string.price_impact, textColor = labelColor)
            Spacer(modifier = Modifier.width(6.dp))
            InfoIcon(onClick = onInfoClick, tintColor = infoIconColor)
        },
        valueContent = {
            val valueColor = if (hasWarning) warningColor else PeraTheme.colors.text.main
            QuoteDetailValue(text = priceImpact.percentage.getDisplayValue(), textColor = valueColor)
        }
    )
}

@Composable
private fun MinimumReceived(minReceivedAssetAmount: AmountRenderer) {
    QuoteDetailRow(
        labelContent = { QuoteDetailLabel(textResId = R.string.minimum_received) },
        valueContent = { QuoteDetailValue(text = minReceivedAssetAmount.getDisplayValue()) }
    )
}

@Composable
private fun ExchangeFee(exchangeFee: AmountRenderer, onInfoClick: () -> Unit) {
    QuoteDetailRow(
        labelContent = {
            QuoteDetailLabel(textResId = R.string.exchange_fee)
            Spacer(modifier = Modifier.width(6.dp))
            InfoIcon(onClick = onInfoClick)
        },
        valueContent = { QuoteDetailValue(text = exchangeFee.getDisplayValue()) }
    )
}

@Composable
private fun PeraFee(peraFee: AmountRenderer) {
    QuoteDetailRow(
        labelContent = { QuoteDetailLabel(textResId = R.string.pera_fee) },
        valueContent = { QuoteDetailValue(text = peraFee.getDisplayValue()) }
    )
}

@Composable
private fun QuoteDetailRow(
    labelContent: @Composable RowScope.() -> Unit,
    valueContent: @Composable RowScope.() -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            labelContent()
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            valueContent()
        }
    }
}

@Composable
private fun QuoteDetailLabel(
    modifier: Modifier = Modifier,
    textResId: Int,
    textColor: Color = PeraTheme.colors.text.gray
) {
    Text(
        modifier = modifier,
        text = stringResource(textResId),
        style = PeraTheme.typography.footnote.sans,
        color = textColor
    )
}

@Composable
private fun QuoteDetailValue(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color = PeraTheme.colors.text.main
) {
    Text(
        modifier = modifier,
        text = text,
        style = PeraTheme.typography.footnote.sans,
        color = textColor
    )
}

@Composable
private fun InfoIcon(
    tintColor: Color = PeraTheme.colors.text.grayLighter,
    onClick: () -> Unit
) {
    Icon(
        modifier = Modifier
            .clickableNoRipple(onClick = onClick)
            .size(20.dp),
        painter = painterResource(R.drawable.ic_info),
        tint = tintColor,
        contentDescription = null
    )
}

@Composable
private fun PriceImpactWarning(warningStatus: WarningStatus, onTinymanPriceImpactFaqClick: () -> Unit) {
    val annotatedString: androidx.compose.ui.text.AnnotatedString? = when (warningStatus) {
        is Level1 -> buildAnnotatedString { append(stringResource(R.string.caution_price_impact_greater)) }
        is Level2 -> getBlockedPriceImpactWarningText(onTinymanPriceImpactFaqClick)
        NoWarning -> null
    }
    annotatedString?.let {
        Row {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(R.drawable.ic_error),
                tint = PeraTheme.colors.helper.negative,
                contentDescription = null
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = annotatedString,
                style = PeraTheme.typography.footnote.sansMedium,
                color = PeraTheme.colors.helper.negative
            )
        }
    }
}

@Composable
private fun getBlockedPriceImpactWarningText(onUrlClick: () -> Unit): androidx.compose.ui.text.AnnotatedString {
    val description = stringResource(R.string.this_swap_can_not_be_executed)
    val linkString = stringResource(id = R.string.here)
    return buildAnnotatedString {
        append(description)
        val startIndex = description.indexOf(linkString)
        if (startIndex != -1) {
            val endIndex = startIndex + linkString.length
            addStyle(
                style = SpanStyle(color = PeraTheme.colors.link.primary),
                start = startIndex,
                end = endIndex
            )
            addLink(
                clickable = LinkAnnotation.Clickable(tag = "url", linkInteractionListener = { onUrlClick() }),
                start = startIndex,
                end = endIndex
            )
        }
    }
}
