@file:Suppress("EmptyFunctionBlock")
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

package com.algorand.android.modules.addaccount.joint.creation.ui.setthreshold.preview

import androidx.compose.runtime.Composable
import com.algorand.android.modules.addaccount.joint.creation.ui.setthreshold.SetThresholdScreen
import com.algorand.android.modules.addaccount.joint.creation.ui.setthreshold.SetThresholdScreenListener
import com.algorand.android.ui.compose.preview.PeraPreviewLightDark
import com.algorand.android.ui.compose.theme.PeraTheme

@PeraPreviewLightDark
@Composable
fun SetThresholdScreenPreview() {
    PeraTheme {
        val listener = object : SetThresholdScreenListener {
            override fun onBackClick() {}
            override fun onContinueClick(threshold: Int) {}
        }
        SetThresholdScreen(
            numberOfAccounts = 3,
            listener = listener
        )
    }
}

@PeraPreviewLightDark
@Composable
fun SetThresholdScreenWithTwoAccountsPreview() {
    PeraTheme {
        val listener = object : SetThresholdScreenListener {
            override fun onBackClick() {}
            override fun onContinueClick(threshold: Int) {}
        }
        SetThresholdScreen(
            numberOfAccounts = 2,
            listener = listener
        )
    }
}

@PeraPreviewLightDark
@Composable
fun SetThresholdScreenWithManyAccountsPreview() {
    PeraTheme {
        val listener = object : SetThresholdScreenListener {
            override fun onBackClick() {}
            override fun onContinueClick(threshold: Int) {}
        }
        SetThresholdScreen(
            numberOfAccounts = 5,
            listener = listener
        )
    }
}
