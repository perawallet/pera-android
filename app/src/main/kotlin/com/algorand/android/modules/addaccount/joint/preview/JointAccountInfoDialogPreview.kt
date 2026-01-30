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

package com.algorand.android.modules.addaccount.joint.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.modules.addaccount.joint.info.ui.BulletPointsSection
import com.algorand.android.modules.addaccount.joint.info.ui.TitleWithNewBadge
import com.algorand.android.modules.addaccount.joint.info.ui.VisualHeader
import com.algorand.android.ui.compose.preview.PeraPreviewLightDark
import com.algorand.android.ui.compose.theme.ColorPalette
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.button.PeraPrimaryButton

@PeraPreviewLightDark
@Composable
fun JointAccountInfoDialogPreview() {
    PeraTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ColorPalette.Black.Alpha64)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = PeraTheme.colors.background.primary
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    VisualHeader()

                    TitleWithNewBadge()

                    Spacer(modifier = Modifier.height(12.dp))

                    BulletPointsSection()

                    Spacer(modifier = Modifier.height(32.dp))

                    PeraPrimaryButton(
                        text = stringResource(R.string.continue_text),
                        onClick = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
