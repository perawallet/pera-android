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

@file:OptIn(ExperimentalMaterial3Api::class)
@file:Suppress("LongParameterList")

package com.algorand.android.ui.compose.widget.textfield

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.algorand.android.ui.compose.theme.PeraTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeraTextField(
    modifier: Modifier = Modifier,
    text: String,
    onTextChanged: (String) -> Unit,
    label: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    hint: String? = null,
    colors: TextFieldColors = PeraTextFieldColors.defaultColors()
) {
    val textField = text.ifEmpty { " " }
    PeraTextFieldContainer(modifier, textField, hint) {
        BasicTextField(
            modifier = Modifier.defaultTextFieldModifier(),
            value = textField,
            onValueChange = onTextChanged,
            decorationBox = {
                TextFieldDecorationBox(textField, it, label, trailingIcon, singleLine, enabled, colors)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeraTextField(
    modifier: Modifier = Modifier,
    textFieldValue: TextFieldValue,
    onTextChanged: (TextFieldValue) -> Unit,
    label: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    hint: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    textStyle: TextStyle = TextStyle.Default,
    colors: TextFieldColors = PeraTextFieldColors.defaultColors()
) {
    PeraTextFieldContainer(modifier, textFieldValue.text, hint) {
        BasicTextField(
            modifier = Modifier.defaultTextFieldModifier(),
            value = textFieldValue,
            onValueChange = onTextChanged,
            keyboardOptions = keyboardOptions,
            textStyle = textStyle,
            decorationBox = {
                TextFieldDecorationBox(textFieldValue.text, it, label, trailingIcon, singleLine, enabled, colors)
            }
        )
    }
}

@Composable
private fun PeraTextFieldContainer(
    modifier: Modifier = Modifier,
    text: String,
    hint: String? = null,
    basicTextField: @Composable () -> Unit
) {
    Box(modifier = modifier, contentAlignment = Alignment.CenterStart) {
        val textField = text.ifEmpty { " " }
        HintText(hint, textField)
        basicTextField()
    }
}

@Composable
private fun Modifier.defaultTextFieldModifier(): Modifier = this
    .fillMaxWidth()
    .defaultMinSize(minHeight = 52.dp)

@Composable
private fun HintText(hint: String?, text: String) {
    if (!hint.isNullOrBlank() && text.isBlank()) {
        Text(
            text = hint,
            color = PeraTheme.colors.text.grayLighter,
            style = PeraTheme.typography.body.regular.sans
        )
    }
}

@Composable
private fun TextFieldDecorationBox(
    text: String,
    innerTextField: @Composable () -> Unit,
    label: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    colors: TextFieldColors = PeraTextFieldColors.defaultColors()
) {
    TextFieldDefaults.DecorationBox(
        label = label,
        value = text,
        innerTextField = innerTextField,
        trailingIcon = trailingIcon,
        singleLine = singleLine,
        enabled = enabled,
        visualTransformation = VisualTransformation.None,
        contentPadding = PaddingValues(start = 0.dp, top = 0.dp, end = 0.dp, bottom = 4.dp),
        interactionSource = remember { MutableInteractionSource() },
        shape = RectangleShape,
        colors = colors
    )
}

object PeraTextFieldColors {

    @Composable
    fun defaultColors(): TextFieldColors = TextFieldDefaults.colors().copy(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        unfocusedTextColor = PeraTheme.colors.text.main,
        focusedTextColor = PeraTheme.colors.text.main,
        cursorColor = PeraTheme.colors.text.main
    )
}
