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

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.algorand.android.R
import com.algorand.android.ui.compose.widget.quickaction.PrimaryQuickActionButton
import com.algorand.android.ui.compose.widget.quickaction.QuickActionButtonContainer
import com.algorand.android.ui.compose.widget.quickaction.SecondaryQuickActionButton

@Preview
@Composable
fun QuickActionButtonContainerPreview() {
    QuickActionButtonContainer {
        PrimaryQuickActionButton(R.drawable.ic_swap, "Swap", showIndicator = true) {}
        SecondaryQuickActionButton(R.drawable.ic_staking, "Stake") {}
        SecondaryQuickActionButton(R.drawable.ic_buy_sell_small, "Buy/Sell") {}
    }
}
