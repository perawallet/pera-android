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

package com.algorand.android.modules.addaccount.joint.creation.ui.namejointaccount

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.modules.addaccount.joint.creation.ui.namejointaccount.viewmodel.NameJointAccountViewModel
import com.algorand.android.modules.addaccount.joint.creation.ui.namejointaccount.viewmodel.NameJointAccountViewModel.ViewState
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.PeraToolbar
import com.algorand.android.ui.compose.widget.PeraToolbarIcon
import com.algorand.android.ui.compose.widget.button.PeraButtonState
import com.algorand.android.ui.compose.widget.button.PeraPrimaryButton
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple
import com.algorand.android.ui.compose.widget.text.PeraBodyText
import com.algorand.android.ui.compose.widget.text.PeraWarningText
import com.algorand.android.ui.compose.widget.textfield.PeraTextField

@Composable
fun NameJointAccountScreen(
    viewModel: NameJointAccountViewModel,
    listener: NameJointAccountScreenListener
) {
    var accountName by remember { mutableStateOf("") }
    val viewState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        accountName = viewModel.getDefaultAccountName()
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ToolbarSection(listener = listener)

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            TitleSection()
            Spacer(modifier = Modifier.height(16.dp))
            DescriptionSection()
            Spacer(modifier = Modifier.height(24.dp))
            AccountNameInputSection(
                accountName = accountName,
                onAccountNameChange = { accountName = it }
            )
            ErrorMessageSection(viewState = viewState)
        }

        FinishButtonSection(
            accountName = accountName,
            viewState = viewState,
            onFinishClick = { listener.onFinishClick(accountName) }
        )
    }
}

@Composable
private fun ToolbarSection(listener: NameJointAccountScreenListener) {
    PeraToolbar(
        modifier = Modifier.padding(horizontal = 12.dp),
        text = "",
        startContainer = {
            PeraToolbarIcon(
                iconResId = R.drawable.ic_left_arrow,
                modifier = Modifier.clickableNoRipple(onClick = listener::onBackClick)
            )
        }
    )
}

@Composable
private fun TitleSection() {
    Text(
        text = stringResource(R.string.name_your_account),
        style = PeraTheme.typography.title.regular.sansMedium,
        color = PeraTheme.colors.text.main
    )
}

@Composable
private fun DescriptionSection() {
    PeraBodyText(
        text = stringResource(R.string.name_your_account_to),
        color = PeraTheme.colors.text.gray
    )
}

@Composable
private fun AccountNameInputSection(
    accountName: String,
    onAccountNameChange: (String) -> Unit
) {
    PeraTextField(
        modifier = Modifier.fillMaxWidth(),
        text = accountName,
        onTextChanged = onAccountNameChange,
        hint = stringResource(R.string.joint_account)
    )
}

@Composable
private fun ErrorMessageSection(viewState: ViewState) {
    val errorMessage = when (viewState) {
        is ViewState.Error -> stringResource(viewState.messageResId)
        else -> null
    }
    ErrorText(
        error = errorMessage ?: "",
        isVisible = errorMessage != null
    )
}

@Composable
private fun FinishButtonSection(
    accountName: String,
    viewState: ViewState,
    onFinishClick: () -> Unit
) {
    val buttonState = when (viewState) {
        is ViewState.Loading -> PeraButtonState.PROGRESS
        is ViewState.Success -> PeraButtonState.PROGRESS
        is ViewState.Idle, is ViewState.Error -> {
            if (accountName.isNotBlank()) PeraButtonState.ENABLED else PeraButtonState.DISABLED
        }
    }

    PeraPrimaryButton(
        text = stringResource(R.string.finish_account_creation),
        onClick = onFinishClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        state = buttonState
    )
}

@Composable
private fun ErrorText(error: String, isVisible: Boolean) {
    val alphaAnimation = animateFloatAsState(if (isVisible) 1f else 0f, label = "errorAlpha")
    if (isVisible && error.isNotBlank()) {
        PeraWarningText(
            modifier = Modifier
                .padding(horizontal = 0.dp, vertical = 8.dp)
                .alpha(alphaAnimation.value),
            text = error
        )
    }
}

interface NameJointAccountScreenListener {
    fun onBackClick()
    fun onFinishClick(accountName: String)
}
