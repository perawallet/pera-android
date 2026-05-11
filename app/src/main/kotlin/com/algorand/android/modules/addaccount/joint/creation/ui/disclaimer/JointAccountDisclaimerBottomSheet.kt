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

package com.algorand.android.modules.addaccount.joint.creation.ui.disclaimer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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
import com.algorand.android.ui.compose.widget.button.PeraSecondaryButton

@Composable
fun JointAccountDisclaimerBottomSheet(
    onProceedClick: () -> Unit,
    onGoBackClick: () -> Unit,
    onLearnMoreClick: () -> Unit
) {
    Dialog(
        onDismissRequest = onGoBackClick,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = PeraTheme.colors.text.main.copy(alpha = 0.64f)),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = PeraTheme.colors.background.primary,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                InfoIcon()

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.before_you_create_joint_account),
                    style = PeraTheme.typography.body.large.sansMedium,
                    color = PeraTheme.colors.text.main,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                DisclaimerBulletPoint(
                    number = "1",
                    text = stringResource(R.string.joint_account_disclaimer_1)
                )

                Spacer(modifier = Modifier.height(24.dp))

                DisclaimerBulletPoint(
                    number = "2",
                    text = stringResource(R.string.joint_account_disclaimer_2)
                )

                Spacer(modifier = Modifier.height(24.dp))

                LearnMoreSection(onClick = onLearnMoreClick)

                Spacer(modifier = Modifier.height(32.dp))

                PeraPrimaryButton(
                    text = stringResource(R.string.proceed),
                    onClick = onProceedClick,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                PeraSecondaryButton(
                    text = stringResource(R.string.go_back_and_edit),
                    onClick = onGoBackClick,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun InfoIcon() {
    Box(
        modifier = Modifier.size(72.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.size(72.dp),
            painter = painterResource(R.drawable.ic_info),
            contentDescription = null,
            tint = PeraTheme.colors.text.main
        )
    }
}

@Composable
private fun DisclaimerBulletPoint(number: String, text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
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
                color = PeraTheme.colors.text.gray,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.width(20.dp))

        Text(
            text = text,
            style = PeraTheme.typography.body.regular.sans,
            color = PeraTheme.colors.text.main,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun LearnMoreSection(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(R.drawable.ic_info),
            contentDescription = null,
            tint = PeraTheme.colors.button.square.icon
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.learn_more),
            style = PeraTheme.typography.body.regular.sansMedium,
            color = PeraTheme.colors.button.square.icon
        )
    }
}
