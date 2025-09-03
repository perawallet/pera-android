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

package com.algorand.android.ui.compose.widget.textfield

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark

@PreviewLightDark
@Preview(showBackground = true)
@Composable
fun PeraTextFieldPreview() {
    Column {
        PeraTextField(
            modifier = Modifier,
            text = "Text Field Input",
            onTextChanged = {}
        )
        PeraTextField(
            modifier = Modifier,
            text = "",
            onTextChanged = {},
            hint = "Hint Text",
        )
    }
}
