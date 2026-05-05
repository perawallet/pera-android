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

package com.algorand.android.modules.addaccount.intro.view.preview

import androidx.compose.runtime.Composable
import com.algorand.android.modules.addaccount.intro.view.AddAccountIntroContent
import com.algorand.android.modules.addaccount.intro.view.AddAccountIntroScreenListener
import com.algorand.android.modules.addaccount.intro.viewmodel.AddAccountIntroViewModel
import com.algorand.android.modules.addaccount.intro.viewmodel.AddAccountIntroViewModel.ViewState
import com.algorand.android.ui.compose.preview.PeraPreviewLightDark
import com.algorand.android.ui.compose.theme.PeraTheme

@PeraPreviewLightDark
@Composable
fun AddAccountIntroScreenPreview() {
    PeraTheme {
        AddAccountIntroContent(
            contentState = ViewState.Content(
                isSkipButtonVisible = false,
                isCloseButtonVisible = true,
                primaryAccountOption = AddAccountIntroViewModel.PrimaryAccountOption.AddAccount,
                jointAccountOption = AddAccountIntroViewModel.JointAccountOption.Visible
            ),
            listener = object : AddAccountIntroScreenListener {
                override fun onAddAccountClick() = Unit
                override fun onAddJointAccountClick() = Unit
                override fun onImportAccountClick() = Unit
                override fun onWatchAddressClick() = Unit
                override fun onCreateUniversalWalletClick() = Unit
                override fun onCreateAlgo25AccountClick() = Unit
                override fun onCloseClick() = Unit
            }
        )
    }
}
