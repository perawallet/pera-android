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

package com.algorand.android.ui.compose.widget.textfield.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.ui.compose.widget.textfield.PeraSlimTextField

@Preview(showBackground = true)
@Composable
fun PeraSlimTextFieldPreview() {

    val longText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut"

    Column {
        PeraSlimTextField(
            text = "Text Field Input",
            hint = "Hint",
            onTextChanged = {}
        )

        PreviewSeparator()

        PeraSlimTextField(
            text = "",
            hint = "Hint",
            onTextChanged = {}
        )

        PreviewSeparator()

        PeraSlimTextField(
            text = longText,
            onTextChanged = {}
        )

        PreviewSeparator()

        PeraSlimTextField(
            text = "",
            hint = longText,
            onTextChanged = {},
            startIconContainer = { IconPlaceholder() }
        )

        PreviewSeparator()

        PeraSlimTextField(
            text = "",
            hint = longText,
            onTextChanged = {},
            endIconContainer = { IconPlaceholder() }
        )

        PreviewSeparator()

        PeraSlimTextField(
            text = "",
            hint = longText,
            onTextChanged = {},
            startIconContainer = { IconPlaceholder() },
            endIconContainer = { IconPlaceholder() }
        )
    }
}

@Composable
private fun PreviewSeparator() {
    Spacer(
        modifier = Modifier
            .background(color = Color.Black)
            .height(12.dp)
            .fillMaxWidth()
    )
}

@Composable
private fun IconPlaceholder() {
    Icon(
        modifier = Modifier.size(24.dp),
        contentDescription = null,
        painter = painterResource(R.drawable.ic_close)
    )
}
