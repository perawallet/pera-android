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

package com.algorand.android.ui.passphrase.acknowledge.view

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.PeraBottomSheetDragIndicator
import com.algorand.android.ui.compose.widget.PeraCheckbox
import com.algorand.android.ui.compose.widget.button.PeraButtonState
import com.algorand.android.ui.compose.widget.button.PeraPrimaryButton
import com.algorand.android.ui.compose.widget.button.PeraSecondaryButton

@Composable
fun PassphraseAcknowledgeBottomSheetScreen(
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    val confirmationItems = remember { getConfirmationItems() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = PeraTheme.colors.background.primary,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PeraBottomSheetDragIndicator(modifier = Modifier.padding(top = 12.dp))
        Spacer(modifier = Modifier.height(32.dp))
        TopIcon()
        Spacer(modifier = Modifier.height(20.dp))
        TitleText()
        Spacer(modifier = Modifier.height(12.dp))
        DescriptionText()
        ConfirmationItemList(confirmationItems)
        Spacer(modifier = Modifier.height(32.dp))
        ConfirmButton(confirmationItems, onConfirm)
        Spacer(modifier = Modifier.height(12.dp))
        CancelButton(onCancel)
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun TopIcon() {
    Icon(
        modifier = Modifier.size(72.dp),
        painter = painterResource(R.drawable.ic_account_rekeyed),
        contentDescription = null,
        tint = PeraTheme.colors.helper.positive
    )
}

@Composable
private fun TitleText() {
    Text(
        text = stringResource(R.string.keep_it_secret_keep_it),
        style = PeraTheme.typography.body.large.sansMedium,
        color = PeraTheme.colors.text.main
    )
}

@Composable
private fun DescriptionText() {
    Text(
        text = stringResource(R.string.confirm_and_acknowledge_the_following),
        style = PeraTheme.typography.body.regular.sans,
        color = PeraTheme.colors.text.gray
    )
}

@Composable
private fun ConfirmButton(confirmationItems: SnapshotStateList<ConfirmationItem>, onClick: () -> Unit) {
    val isConfirmButtonEnabled by remember(confirmationItems) {
        derivedStateOf {
            confirmationItems.all { it.isChecked }
        }
    }
    PeraPrimaryButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        state = if (isConfirmButtonEnabled) PeraButtonState.ENABLED else PeraButtonState.DISABLED,
        text = stringResource(R.string.reveal_passphrase),
        onClick = onClick
    )
}

@Composable
private fun CancelButton(onClick: () -> Unit) {
    PeraSecondaryButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        text = stringResource(R.string.cancel),
        onClick = onClick
    )
}

@Composable
private fun ConfirmationItemList(confirmationItems: SnapshotStateList<ConfirmationItem>) {
    val density = LocalDensity.current
    var confirmationItemHeight by remember { mutableStateOf(0.dp) }
    LazyColumn(
        verticalArrangement = Arrangement.Center
    ) {
        itemsIndexed(confirmationItems) { index, item ->
            ConfirmationCheckbox(
                modifier = Modifier.defaultMinSize(minHeight = confirmationItemHeight),
                item = item,
                onCheckChange = { isChecked ->
                    confirmationItems[index] = item.copy(isChecked = isChecked)
                },
                onHeightChanged = { height ->
                    val heightDp = with(density) { height.toDp() }
                    if (confirmationItemHeight < heightDp) {
                        confirmationItemHeight = heightDp
                    }
                },
            )
            if (index < confirmationItems.lastIndex) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(1.dp)
                        .background(color = PeraTheme.colors.layer.grayLighter)
                )
            }
        }
    }
}

@Composable
private fun ConfirmationCheckbox(
    modifier: Modifier,
    item: ConfirmationItem,
    onCheckChange: (Boolean) -> Unit,
    onHeightChanged: (Int) -> Unit
) {
    Row(
        modifier = modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 18.dp, end = 16.dp)
                .onSizeChanged { onHeightChanged(it.height) },
            text = stringResource(id = item.resId),
            style = PeraTheme.typography.footnote.sans,
            color = PeraTheme.colors.text.main
        )
        PeraCheckbox(checked = item.isChecked) { onCheckChange(!item.isChecked) }
    }
}

private fun getConfirmationItems(): SnapshotStateList<ConfirmationItem> {
    return mutableStateListOf(
        ConfirmationItem(resId = R.string.nobody_can_see_my_screen, isChecked = false),
        ConfirmationItem(resId = R.string.i_risk_losing_all_my, isChecked = false),
        ConfirmationItem(resId = R.string.my_funds_are_permanently_lost, isChecked = false),
        ConfirmationItem(resId = R.string.pera_employees_will_never_ask, isChecked = false)
    )
}

private data class ConfirmationItem(
    @StringRes val resId: Int,
    var isChecked: Boolean
)
