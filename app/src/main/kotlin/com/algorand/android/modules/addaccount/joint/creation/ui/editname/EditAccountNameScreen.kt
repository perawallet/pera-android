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

@file:Suppress("MagicNumber")

package com.algorand.android.modules.addaccount.joint.creation.ui.editname

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.modules.addaccount.joint.creation.model.SelectedJointAccountItem
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.ContactIcon
import com.algorand.android.ui.compose.widget.PeraToolbar
import com.algorand.android.ui.compose.widget.PeraToolbarIcon
import com.algorand.android.ui.compose.widget.PeraToolbarTextButton
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple
import com.algorand.android.ui.compose.widget.textfield.PeraTextField
import com.algorand.android.ui.compose.widget.textfield.PeraTextFieldLabel

@Composable
fun EditAccountNameScreen(
    account: SelectedJointAccountItem,
    showRemoveButton: Boolean = true,
    listener: EditAccountNameScreenListener
) {
    var name by remember { mutableStateOf(account.accountDisplayName.primaryDisplayName) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ToolbarSection(
            name = name,
            onBackClick = listener::onBackClick,
            onDoneClick = { listener.onDoneClick(name) }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            ContactIcon(
                backgroundColor = PeraTheme.colors.layer.grayLighter,
                iconTint = PeraTheme.colors.text.gray,
                imageUri = account.imageUri,
                size = 80.dp,
                contentDescription = stringResource(R.string.contacts)
            )

            Spacer(modifier = Modifier.height(72.dp))

            NameInputSection(
                name = name,
                onNameChange = { name = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            AccountAddressSection(address = account.accountDisplayName.accountAddress)

            Spacer(modifier = Modifier.height(32.dp))

            if (showRemoveButton && account.isContact) {
                RemoveAddressButtonSection(onRemoveClick = listener::onRemoveClick)
            }
        }
    }
}

@Composable
private fun ToolbarSection(
    name: String,
    onBackClick: () -> Unit,
    onDoneClick: () -> Unit
) {
    PeraToolbar(
        modifier = Modifier.padding(horizontal = 12.dp),
        text = stringResource(R.string.edit_address),
        startContainer = {
            PeraToolbarIcon(
                iconResId = R.drawable.ic_left_arrow,
                modifier = Modifier.clickableNoRipple(onClick = onBackClick)
            )
        },
        endContainer = {
            PeraToolbarTextButton(
                text = stringResource(R.string.done),
                onClick = onDoneClick,
                enabled = name.isNotBlank()
            )
        }
    )
}

@Composable
private fun NameInputSection(
    name: String,
    onNameChange: (String) -> Unit
) {
    PeraTextField(
        modifier = Modifier.fillMaxWidth(),
        text = name,
        onTextChanged = onNameChange,
        label = { PeraTextFieldLabel(text = stringResource(R.string.nickname_optional)) },
        hint = stringResource(R.string.add_a_nickname)
    )
}

@Composable
private fun AccountAddressSection(address: String) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.account_address),
            style = PeraTheme.typography.footnote.sans,
            color = PeraTheme.colors.text.grayLighter
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp),
            text = address,
            style = PeraTheme.typography.body.regular.sans,
            color = PeraTheme.colors.text.gray
        )
    }
}

@Composable
private fun RemoveAddressButtonSection(onRemoveClick: () -> Unit) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
            .height(52.dp),
        onClick = onRemoveClick,
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PeraTheme.colors.helper.negativeLighter,
            contentColor = PeraTheme.colors.helper.negative
        ),
        contentPadding = PaddingValues(16.dp)
    ) {
        Text(
            text = stringResource(R.string.remove_address),
            style = PeraTheme.typography.body.regular.sansMedium
        )
    }
}

interface EditAccountNameScreenListener {
    fun onBackClick()
    fun onDoneClick(name: String)
    fun onRemoveClick()
}
