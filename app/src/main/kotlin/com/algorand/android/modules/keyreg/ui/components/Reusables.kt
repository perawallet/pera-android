/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.keyreg.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@SuppressLint("ComposableNaming")
@Composable
fun algorandDivider() {
    HorizontalDivider(
        modifier =
        Modifier
            .width(327.dp),
        thickness = 1.dp,
        color = Color.Gray,
    )
}

@SuppressLint("ComposableNaming")
@Composable
fun algorandButton(
    buttonText: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(Color.Black),
        shape = RoundedCornerShape(8.dp),
        modifier =
        Modifier
            .width(327.dp)
            .height(52.dp),
    ) {
        Text(
            text = buttonText,
            style = MaterialTheme.typography.labelLarge,
            color = Color.White,
        )
    }
}
