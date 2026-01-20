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

package com.algorand.android.ui.register.recoveraccounttypeselection.preview

import androidx.compose.runtime.Composable
import com.algorand.android.models.OnboardingAccountType
import com.algorand.android.ui.compose.preview.PeraPreviewLightDark
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.register.recoveraccounttypeselection.AccountRecoveryTypeSelectionScreen
import com.algorand.android.ui.register.recoveraccounttypeselection.AccountRecoveryTypeSelectionScreenListener

@PeraPreviewLightDark
@Composable
fun AccountRecoveryTypeSelectionScreenPreview() {
    PeraTheme {
        val listener = object : AccountRecoveryTypeSelectionScreenListener {
            override fun onNavigateToRecoverAccountInfo(onboardingAccountType: OnboardingAccountType) {}
            override fun onRecoverWithQRClick() {}
            override fun onPairLedgerClick() {}
            override fun onImportFromWebClick() {}
            override fun onAlgorandSecureBackupClick() {}
        }
        AccountRecoveryTypeSelectionScreen(listener = listener)
    }
}
