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

package com.algorand.android.modules.addaccount.joint.info.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.ColorPalette
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.button.PeraPrimaryButton

@Composable
fun JointAccountInfoDialog(
    onContinueClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
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
                    onClick = onContinueClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
internal fun VisualHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(222.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.ic_joint_account_info_header),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
internal fun TitleWithNewBadge() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = PeraTheme.colors.helper.positiveLighter,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = stringResource(R.string.new_text).uppercase(),
                style = PeraTheme.typography.caption.sansBold,
                color = PeraTheme.colors.helper.positive
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.joint_account),
            style = PeraTheme.typography.body.large.sansMedium,
            color = PeraTheme.colors.text.main,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
internal fun BulletPointsSection() {
    BulletPoint(
        number = stringResource(R.string.dialpad_1),
        text = stringResource(R.string.joint_account_info_1)
    )

    Spacer(modifier = Modifier.height(12.dp))

    BulletPoint(
        number = stringResource(R.string.dialpad_2),
        text = stringResource(R.string.joint_account_info_2)
    )

    Spacer(modifier = Modifier.height(12.dp))

    BulletPoint(
        number = stringResource(R.string.dialpad_3),
        text = stringResource(R.string.joint_account_info_3)
    )
}

@Composable
internal fun BulletPoint(number: String, text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .shadow(
                    elevation = 2.dp,
                    shape = CircleShape,
                    spotColor = ColorPalette.Gray.V900Alpha12,
                    ambientColor = ColorPalette.Gray.V900Alpha12
                )
                .background(
                    color = PeraTheme.colors.background.primary,
                    shape = CircleShape
                )
                .border(
                    width = 1.dp,
                    color = PeraTheme.colors.layer.grayLighter,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                style = PeraTheme.typography.body.regular.sans,
                color = PeraTheme.colors.text.main,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = text,
            style = PeraTheme.typography.body.regular.sans,
            color = PeraTheme.colors.text.gray,
            modifier = Modifier
                .weight(1f)
                .padding(top = 4.dp)
        )
    }
}
