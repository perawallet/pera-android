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

package com.algorand.android.ui.transaction.history.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple
import com.algorand.android.ui.compose.widget.progress.PeraCircularProgressIndicator
import com.algorand.android.ui.transaction.history.model.TransactionHistoryItem
import com.algorand.android.ui.transaction.history.model.TransactionHistoryItem.ApplicationCall
import com.algorand.android.ui.transaction.history.model.TransactionHistoryItem.AssetConfiguration
import com.algorand.android.ui.transaction.history.model.TransactionHistoryItem.Date
import com.algorand.android.ui.transaction.history.model.TransactionHistoryItem.Heartbeat
import com.algorand.android.ui.transaction.history.model.TransactionHistoryItem.KeyRegistration
import com.algorand.android.ui.transaction.history.model.TransactionHistoryItem.OptIn
import com.algorand.android.ui.transaction.history.model.TransactionHistoryItem.OptOut
import com.algorand.android.ui.transaction.history.model.TransactionHistoryItem.Receive
import com.algorand.android.ui.transaction.history.model.TransactionHistoryItem.ReceiveOptOut
import com.algorand.android.ui.transaction.history.model.TransactionHistoryItem.Self
import com.algorand.android.ui.transaction.history.model.TransactionHistoryItem.Send
import com.algorand.android.ui.transaction.history.model.TransactionHistoryItem.SendOptOut
import com.algorand.android.ui.transaction.history.model.TransactionHistoryItem.Separator
import com.algorand.android.ui.transaction.history.model.TransactionHistoryItem.Swap
import com.algorand.android.utils.toShortenedAddress
import java.math.BigDecimal.ZERO

fun LazyListScope.pagingTransactionHistoryListItems(
    historyItems: LazyPagingItems<TransactionHistoryItem>?,
    listener: TransactionHistoryListListener
) {
    if (historyItems != null) {
        when (historyItems.loadState.refresh) {
            LoadState.Loading -> loadingState()
            is LoadState.Error -> emptyState()
            is LoadState.NotLoading -> {
                if (historyItems.itemCount == 0) {
                    emptyState()
                } else {
                    contentState(historyItems, listener)
                }
            }
        }
    }
}

@Composable
fun TransactionHistoryList(
    modifier: Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    historyItems: List<TransactionHistoryItem>,
    listener: TransactionHistoryListListener
) {
    LazyColumn(modifier, contentPadding = contentPadding) {
        items(historyItems) { historyItem ->
            TransactionHistoryListItem(historyItem, listener)
        }
    }
}

private fun LazyListScope.contentState(
    historyItems: LazyPagingItems<TransactionHistoryItem>,
    listener: TransactionHistoryListListener
) {
    items(
        count = historyItems.itemCount,
        key = { index -> index }
    ) { index ->
        val historyItem = historyItems[index]
        if (historyItem != null) {
            TransactionHistoryListItem(historyItem, listener)
        }
    }
}

private fun LazyListScope.loadingState() {
    item {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(56.dp),
            contentAlignment = Alignment.Center
        ) {
            PeraCircularProgressIndicator()
        }
    }
}

private fun LazyListScope.emptyState() {
    item {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.no_transactions),
                style = PeraTheme.typography.body.regular.sans,
                color = PeraTheme.colors.text.main,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.there_are_no),
                style = PeraTheme.typography.footnote.sans,
                color = PeraTheme.colors.text.gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun TransactionHistoryListItem(item: TransactionHistoryItem, listener: TransactionHistoryListListener) {
    with(listener) {
        when (item) {
            is ApplicationCall -> GenericItemContainer(R.string.application_call, item.formattedFee) {
                onApplicationCallClick(item.id)
            }
            is AssetConfiguration -> GenericItemContainer(R.string.asset_configuration, item.formattedFee) {
                onTransactionClick(item.id)
            }
            is Date -> DateItem(item.date)
            is Heartbeat -> GenericItemContainer(R.string.heartbeat, item.formattedFee) {
                onTransactionClick(item.id)
            }
            is KeyRegistration -> GenericItemContainer(R.string.key_reg, item.formattedFee) {
                onTransactionClick(item.id)
            }
            is OptIn -> GenericItemContainer(R.string.opt_in, item.formattedFee) {
                onTransactionClick(item.id)
            }
            is OptOut -> GenericItemContainer(R.string.opt_out, item.formattedFee) {
                onTransactionClick(item.id)
            }
            is Receive -> ReceiveItem(item) {
                onTransactionClick(item.id)
            }
            is ReceiveOptOut -> GenericItemContainer(R.string.receive_opt_out, item.formattedAmount) {
                onTransactionClick(item.id)
            }
            is Self -> GenericItemContainer(R.string.self_transfer, item.formattedAmount) {
                onTransactionClick(item.id)
            }
            is Send -> SendItem(item) {
                onTransactionClick(item.id)
            }
            is SendOptOut -> GenericItemContainer(R.string.opt_out, item.formattedAmount) {
                onTransactionClick(item.id)
            }
            is Swap -> SwapItem(item) {
                onSwapClick(item.groupId)
            }
            is Separator -> SeparatorItem()
        }
    }
}

@Composable
private fun SendItem(item: Send, onClick: () -> Unit) {
    TransactionItemContainer(
        iconResId = R.drawable.ic_send,
        primaryText = stringResource(R.string.send),
        secondaryText = item.receiverAddress.toShortenedAddress(),
        amountText = item.formattedAmount,
        amountTextColor = if (item.amount > ZERO) PeraTheme.colors.helper.negative else PeraTheme.colors.text.main,
        onClick = onClick
    )
}

@Composable
private fun ReceiveItem(item: Receive, onClick: () -> Unit) {
    TransactionItemContainer(
        iconResId = R.drawable.ic_receive,
        primaryText = stringResource(R.string.receive),
        secondaryText = item.senderAddress.toShortenedAddress(),
        amountText = item.formattedAmount,
        amountTextColor = if (item.amount > ZERO) PeraTheme.colors.helper.positive else PeraTheme.colors.text.main,
        onClick = onClick
    )
}

@Composable
private fun SwapItem(item: Swap, onClick: () -> Unit) {
    TransactionItemContainer(
        iconResId = R.drawable.ic_arrow_swap,
        primaryText = stringResource(R.string.swap),
        amountText = item.formattedAmountOut,
        onClick = onClick,
        secondaryText = stringResource(
            R.string.asset_for_asset_formatted,
            item.formattedAmountIn,
            item.formattedAmountOut
        )
    )
}

@Composable
private fun DateItem(date: String) {
    Row(
        modifier = Modifier.padding(vertical = 20.dp, horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .background(color = PeraTheme.colors.layer.grayLighter)
                .height(1.dp)
                .weight(1f)
        )
        Text(
            modifier = Modifier.padding(horizontal = 20.dp),
            text = date,
            style = PeraTheme.typography.footnote.sans,
            color = PeraTheme.colors.text.gray
        )
        Box(
            modifier = Modifier
                .background(color = PeraTheme.colors.layer.grayLighter)
                .height(1.dp)
                .weight(1f)
        )
    }
}

@Composable
private fun TransactionItemContainer(
    iconResId: Int,
    primaryText: String,
    secondaryText: String?,
    amountText: String,
    amountTextColor: Color = PeraTheme.colors.text.main,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickableNoRipple(onClick = onClick)
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TxnIcon(iconResId)
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            PrimaryText(primaryText)
            if (secondaryText != null) {
                SecondaryText(secondaryText)
            }
        }
        AmountText(amountText, amountTextColor)
    }
}

@Composable
private fun GenericItemContainer(primaryTextResId: Int, amountText: String, onClick: () -> Unit) {
    TransactionItemContainer(
        iconResId = R.drawable.ic_buy_sell_small,
        primaryText = stringResource(primaryTextResId),
        secondaryText = null,
        amountText = amountText,
        onClick = onClick
    )
}

@Composable
private fun SeparatorItem() {
    Box(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .background(color = PeraTheme.colors.layer.grayLighter)
            .fillMaxWidth()
            .height(1.dp)
    )
}

@Composable
private fun TxnIcon(resId: Int) {
    Icon(
        modifier = Modifier
            .background(color = PeraTheme.colors.layer.grayLighter, shape = CircleShape)
            .size(40.dp)
            .padding(8.dp),
        painter = painterResource(resId),
        contentDescription = null,
        tint = PeraTheme.colors.text.gray
    )
}

@Composable
private fun PrimaryText(text: String) {
    Text(
        text = text,
        style = PeraTheme.typography.body.regular.sans,
        color = PeraTheme.colors.text.main,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun SecondaryText(text: String) {
    Text(
        text = text,
        style = PeraTheme.typography.footnote.sans,
        color = PeraTheme.colors.text.grayLighter,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun AmountText(text: String, color: Color) {
    Text(
        text = text,
        style = PeraTheme.typography.body.regular.sansMedium,
        color = color
    )
}

interface TransactionHistoryListListener {
    fun onTransactionClick(id: String)
    fun onApplicationCallClick(id: String)
    fun onSwapClick(groupId: String)
}
