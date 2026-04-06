@file:Suppress("EmptyFunctionBlock", "Unused")
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

package com.algorand.android.modules.addaccount.joint.creation.ui.namejointaccount.preview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.ui.compose.preview.PeraPreviewLightDark
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.PeraToolbar
import com.algorand.android.ui.compose.widget.PeraToolbarIcon
import com.algorand.android.ui.compose.widget.button.PeraButtonState
import com.algorand.android.ui.compose.widget.button.PeraPrimaryButton
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple
import com.algorand.android.ui.compose.widget.text.PeraBodyText
import com.algorand.android.ui.compose.widget.text.PeraWarningText
import com.algorand.android.ui.compose.widget.textfield.PeraTextField

@PeraPreviewLightDark
@Composable
fun NameJointAccountScreenPreview() {
    PeraTheme {
        NameJointAccountScreenPreviewContent(
            accountName = "",
            defaultAccountName = "Shared Account 1",
            errorMessage = null,
            buttonState = PeraButtonState.DISABLED
        )
    }
}

@PeraPreviewLightDark
@Composable
fun NameJointAccountScreenWithNamePreview() {
    PeraTheme {
        NameJointAccountScreenPreviewContent(
            accountName = "My Joint Account",
            defaultAccountName = "Shared Account 1",
            errorMessage = null,
            buttonState = PeraButtonState.ENABLED
        )
    }
}

@PeraPreviewLightDark
@Composable
fun NameJointAccountScreenErrorPreview() {
    PeraTheme {
        NameJointAccountScreenPreviewContent(
            accountName = "My Account",
            defaultAccountName = "Shared Account 1",
            errorMessage = "An error occurred",
            buttonState = PeraButtonState.ENABLED
        )
    }
}

@Composable
private fun NameJointAccountScreenPreviewContent(
    accountName: String,
    defaultAccountName: String,
    errorMessage: String?,
    buttonState: PeraButtonState
) {
    var text by remember { mutableStateOf(accountName.ifEmpty { defaultAccountName }) }

    Column(modifier = Modifier.fillMaxSize()) {
        PeraToolbar(
            modifier = Modifier.padding(horizontal = 12.dp),
            text = "",
            startContainer = {
                PeraToolbarIcon(
                    iconResId = R.drawable.ic_left_arrow,
                    modifier = Modifier.clickableNoRipple(onClick = {})
                )
            }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.name_your_account),
                style = PeraTheme.typography.title.regular.sansMedium,
                color = PeraTheme.colors.text.main
            )
            Spacer(modifier = Modifier.height(16.dp))
            PeraBodyText(
                text = stringResource(R.string.name_your_account_to),
                color = PeraTheme.colors.text.gray
            )
            Spacer(modifier = Modifier.height(24.dp))
            PeraTextField(
                modifier = Modifier.fillMaxWidth(),
                text = text,
                onTextChanged = { text = it },
                hint = stringResource(R.string.joint_account)
            )
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                PeraWarningText(text = errorMessage)
            }
        }

        PeraPrimaryButton(
            text = stringResource(R.string.finish_account_creation),
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            state = buttonState
        )
    }
}
