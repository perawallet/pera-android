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

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.algorand.android.ui.compose.theme.PeraTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmountInputTextField(
    modifier: Modifier = Modifier,
    text: String,
    onTextChanged: (String) -> Unit = {},
    hint: String? = null,
    enabled: Boolean = true,
    textStyle: TextStyle,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    Box {
        BasicTextField(
            modifier = modifier,
            value = text,
            onValueChange = onTextChanged,
            visualTransformation = DecimalFormattedVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            keyboardActions = keyboardActions,
            textStyle = textStyle.copy(color = PeraTheme.colors.text.main),
            decorationBox = {
                TextFieldDefaults.DecorationBox(
                    label = null,
                    value = text,
                    innerTextField = it,
                    singleLine = true,
                    enabled = enabled,
                    visualTransformation = visualTransformation,
                    contentPadding = PaddingValues(start = 0.dp, top = 0.dp, end = 0.dp, bottom = 0.dp),
                    interactionSource = remember { MutableInteractionSource() },
                    shape = RectangleShape,
                    colors = TextFieldDefaults.colors().copy(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    )
                )
            }
        )
        if (!hint.isNullOrBlank() && text.isBlank()) {
            Text(
                text = hint,
                color = PeraTheme.colors.text.grayLighter,
                style = PeraTheme.typography.body.large.sansMedium
            )
        }
    }
}
