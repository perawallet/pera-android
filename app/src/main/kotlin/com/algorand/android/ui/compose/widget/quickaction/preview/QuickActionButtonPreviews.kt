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

package com.algorand.android.ui.compose.widget.quickaction.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.algorand.android.ui.compose.widget.quickaction.BuySellQuickActionButton
import com.algorand.android.ui.compose.widget.quickaction.CopyAddressQuickActionButton
import com.algorand.android.ui.compose.widget.quickaction.InboxQuickActionButton
import com.algorand.android.ui.compose.widget.quickaction.MoreQuickActionButton
import com.algorand.android.ui.compose.widget.quickaction.SendQuickActionButton
import com.algorand.android.ui.compose.widget.quickaction.ShowAddressQuickActionButton
import com.algorand.android.ui.compose.widget.quickaction.StakeQuickActionButton
import com.algorand.android.ui.compose.widget.quickaction.SwapQuickActionButton

@OptIn(ExperimentalLayoutApi::class)
@Preview(showBackground = true)
@Composable
fun QuickActionButtonPreviews() {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SwapQuickActionButton {}
        BuySellQuickActionButton {}
        StakeQuickActionButton {}
        SendQuickActionButton {}
        InboxQuickActionButton(isSelected = false) {}
        InboxQuickActionButton(isSelected = true) {}
        CopyAddressQuickActionButton {}
        MoreQuickActionButton {}
        ShowAddressQuickActionButton {}
    }
}
