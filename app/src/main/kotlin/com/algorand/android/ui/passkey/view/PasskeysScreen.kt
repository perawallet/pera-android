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

package com.algorand.android.ui.passkey.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.PeraToolbar
import com.algorand.android.ui.compose.widget.PeraToolbarIcon
import com.algorand.android.ui.compose.widget.bottomsheet.PeraModalBottomSheet
import com.algorand.android.ui.compose.widget.bottomsheet.rememberPeraSheetState
import com.algorand.android.ui.compose.widget.button.PeraPrimaryButton
import com.algorand.android.ui.compose.widget.button.PeraSecondaryButton
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple
import com.algorand.android.ui.passkey.model.PasskeyListItem
import com.algorand.android.ui.passkey.viewmodel.PasskeysViewModel
import com.algorand.android.ui.passkey.viewmodel.PasskeysViewModel.ViewState.Content
import com.algorand.android.ui.passkey.viewmodel.PasskeysViewModel.ViewState.Empty
import com.algorand.android.ui.passkey.viewmodel.PasskeysViewModel.ViewState.Idle
import com.algorand.wallet.utils.date.RelativeTimeDifference
import com.algorand.wallet.utils.date.RelativeTimeDifference.RelativeTime.Date
import com.algorand.wallet.utils.date.RelativeTimeDifference.RelativeTime.Days
import com.algorand.wallet.utils.date.RelativeTimeDifference.RelativeTime.Hours
import com.algorand.wallet.utils.date.RelativeTimeDifference.RelativeTime.Minutes
import com.algorand.wallet.utils.date.RelativeTimeDifference.RelativeTime.Now

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasskeysScreen(viewModel: PasskeysViewModel = hiltViewModel(), onBackClick: () -> Unit) {
    Column {
        val scope = rememberCoroutineScope()
        val confirmationSheetState = rememberPeraSheetState(scope)
        var selectedPasskeyItem by remember { mutableStateOf<PasskeyListItem?>(null) }
        PeraToolbar(
            text = stringResource(R.string.passkeys),
            startContainer = {
                PeraToolbarIcon(
                    iconResId = R.drawable.ic_left_arrow,
                    modifier = Modifier.clickableNoRipple(onClick = onBackClick)
                )
            }
        )
        val viewState = viewModel.state.collectAsStateWithLifecycle().value
        when (viewState) {
            Idle -> Unit
            Empty -> EmptyState()
            is Content -> {
                ContentState(
                    passkeys = viewState.passkeyListItems,
                    onDeleteClick = {
                        selectedPasskeyItem = it
                        confirmationSheetState.show()
                    }
                )
            }
        }
        confirmationSheetState.SheetContent {
            RemoveConfirmationBottomSheet(
                sheetState = confirmationSheetState.sheetState,
                onDismiss = {
                    selectedPasskeyItem = null
                    confirmationSheetState.hide()
                },
                onRemoveClick = {
                    selectedPasskeyItem?.let { viewModel.removePasskey(it) }
                    selectedPasskeyItem = null
                    confirmationSheetState.hide()
                }
            )
        }
    }
    LaunchedEffect(Unit) {
        viewModel.initPasskeys()
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            imageVector = ImageVector.vectorResource(R.drawable.ic_passkeys),
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.no_passkeys_yet),
            style = PeraTheme.typography.body.regular.sans,
            color = PeraTheme.colors.text.main
        )
    }
}

@Composable
private fun ContentState(passkeys: List<PasskeyListItem>, onDeleteClick: (PasskeyListItem) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(passkeys) { passkey ->
            Column(
                modifier = Modifier
                    .border(width = 1.dp, color = PeraTheme.colors.layer.gray, shape = RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                PasskeyListItemHeader(passkey) { onDeleteClick(passkey) }
                Divider()
                DetailsRow(R.string.last_used, getFormattedLastUsedTime(passkey.lastUsedRelativeTime))
                Divider()
                DetailsRow(R.string.user_name, passkey.username)
            }
        }
    }
}

@Composable
private fun getFormattedLastUsedTime(relativeTime: RelativeTimeDifference.RelativeTime?): String {
    return when (relativeTime) {
        Now -> stringResource(R.string.now)
        is Minutes -> pluralStringResource(R.plurals.min_ago, relativeTime.value, relativeTime.value)
        is Hours -> pluralStringResource(R.plurals.hours_ago, relativeTime.value, relativeTime.value)
        is Days -> pluralStringResource(R.plurals.days_ago, relativeTime.value, relativeTime.value)
        is Date -> relativeTime.value
        null -> stringResource(R.string.never)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RemoveConfirmationBottomSheet(sheetState: SheetState, onDismiss: () -> Unit, onRemoveClick: () -> Unit) {
    PeraModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Icon(
                modifier = Modifier.size(72.dp),
                painter = painterResource(R.drawable.ic_trash),
                tint = PeraTheme.colors.helper.negative,
                contentDescription = null
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.remove_passkey),
                style = PeraTheme.typography.body.large.sansMedium,
                color = PeraTheme.colors.text.main
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.you_are_about_to_remove_passkey),
                style = PeraTheme.typography.body.regular.sans,
                color = PeraTheme.colors.text.gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            PeraPrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.remove),
                onClick = onRemoveClick
            )
            Spacer(modifier = Modifier.height(16.dp))
            PeraSecondaryButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.keep_it),
                onClick = onDismiss
            )
        }
    }
}

@Composable
private fun PasskeyListItemHeader(passkey: PasskeyListItem, onDeleteClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(R.drawable.ic_passkey),
            tint = PeraTheme.colors.text.main,
            contentDescription = null
        )
        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .weight(1f)
        ) {
            Text(
                text = passkey.displayName,
                style = PeraTheme.typography.body.regular.sansMedium,
                color = PeraTheme.colors.text.main,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = passkey.rpId,
                style = PeraTheme.typography.footnote.sans,
                color = PeraTheme.colors.text.grayLighter,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
        Icon(
            modifier = Modifier
                .size(24.dp)
                .clickableNoRipple(onClick = onDeleteClick),
            painter = painterResource(R.drawable.ic_trash),
            tint = PeraTheme.colors.text.gray,
            contentDescription = null
        )
    }
}

@Composable
private fun DetailsRow(labelResId: Int, value: String) {
    Row {
        Text(
            text = stringResource(labelResId),
            style = PeraTheme.typography.footnote.sans,
            color = PeraTheme.colors.text.main,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value,
            style = PeraTheme.typography.footnote.sans,
            color = PeraTheme.colors.text.gray
        )
    }
}

@Composable
private fun Divider() {
    Box(
        modifier = Modifier
            .padding(vertical = 12.dp)
            .background(color = PeraTheme.colors.layer.grayLighter)
            .fillMaxWidth()
            .height(1.dp)
    )
}
