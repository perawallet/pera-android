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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple
import com.algorand.android.ui.transaction.csv.viewmodel.CsvViewModel
import com.algorand.android.ui.transaction.csv.viewmodel.CsvViewModel.ViewState.Idle

@Composable
fun TransactionHistoryListItemHeader(
    modifier: Modifier,
    csvViewModel: CsvViewModel,
    isFilterSelected: Boolean,
    onFilterClick: () -> Unit,
    onCsvClick: () -> Unit
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        TitleText()
        Spacer(Modifier.weight(1f))
        FilterButton(isFilterSelected, onFilterClick)
        Spacer(Modifier.width(8.dp))
        CsvButton(csvViewModel, onCsvClick)
    }
}

@Composable
private fun TitleText() {
    Text(
        text = stringResource(R.string.transactions),
        style = PeraTheme.typography.body.regular.sansMedium,
        color = PeraTheme.colors.text.main
    )
}

@Composable
private fun FilterButton(isFilterSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clickableNoRipple(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(20.dp)) {
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(R.drawable.ic_customize),
                contentDescription = null,
                tint = PeraTheme.colors.helper.positive
            )
            if (isFilterSelected) {
                Box(
                    modifier = Modifier
                        .background(color = PeraTheme.colors.helper.negative, shape = CircleShape)
                        .size(8.dp)
                        .align(Alignment.TopEnd)
                )
            }
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.filter),
            style = PeraTheme.typography.body.regular.sansMedium,
            color = PeraTheme.colors.helper.positive
        )
    }
}

@Composable
private fun CsvButton(
    csvViewModel: CsvViewModel,
    onClick: () -> Unit
) {
    val viewState = csvViewModel.state.collectAsStateWithLifecycle().value
    Row(
        modifier = Modifier
            .background(color = PeraTheme.colors.button.square.background, shape = RoundedCornerShape(8.dp))
            .clickableNoRipple(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(16.dp),
            painter = painterResource(R.drawable.ic_document),
            contentDescription = null,
            tint = PeraTheme.colors.helper.positive
        )
        Spacer(Modifier.width(8.dp))
        when (viewState) {
            Idle -> {
                Text(
                    text = stringResource(R.string.csv),
                    style = PeraTheme.typography.footnote.sansMedium,
                    color = PeraTheme.colors.helper.positive
                )
            }
            CsvViewModel.ViewState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = PeraTheme.colors.link.primary,
                    strokeWidth = 3.dp
                )
            }
        }
    }
}
