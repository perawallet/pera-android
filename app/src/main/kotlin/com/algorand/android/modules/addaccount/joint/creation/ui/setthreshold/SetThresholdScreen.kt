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

package com.algorand.android.modules.addaccount.joint.creation.ui.setthreshold

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.modules.addaccount.joint.core.JointAccountConstants
import com.algorand.android.ui.compose.widget.PeraToolbarIcon
import com.algorand.android.ui.compose.widget.button.PeraButtonState
import com.algorand.android.ui.compose.widget.button.PeraPrimaryButton
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple

@Composable
fun SetThresholdScreen(
    numberOfAccounts: Int,
    listener: SetThresholdScreenListener
) {
    var threshold by remember { mutableIntStateOf(minOf(JointAccountConstants.MIN_THRESHOLD, numberOfAccounts)) }

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
            DescriptionSection()
            Spacer(modifier = Modifier.height(48.dp))
            NumberOfAccountsSection(numberOfAccounts = numberOfAccounts)
            Spacer(modifier = Modifier.height(24.dp))
            ThresholdControlSection(
                threshold = threshold,
                numberOfAccounts = numberOfAccounts,
                onThresholdChange = { threshold = it }
            )
        }

        ContinueButtonSection(
            numberOfAccounts = numberOfAccounts,
            onContinueClick = { listener.onContinueClick(threshold) }
        )
    }
}

@Composable
private fun ToolbarSection(listener: SetThresholdScreenListener) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        PeraToolbarIcon(
            iconResId = R.drawable.ic_left_arrow,
            modifier = Modifier.clickableNoRipple(onClick = listener::onBackClick)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            modifier = Modifier.padding(horizontal = 12.dp),
            text = stringResource(R.string.set_a_threshold),
            style = PeraTheme.typography.title.regular.sansMedium,
            color = PeraTheme.colors.text.main
        )
    }
}

@Composable
private fun DescriptionSection() {
    Text(
        text = stringResource(R.string.set_threshold_description),
        style = PeraTheme.typography.body.regular.sans,
        color = PeraTheme.colors.text.gray
    )
}

@Composable
private fun NumberOfAccountsSection(numberOfAccounts: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.number_of_accounts),
                style = PeraTheme.typography.body.regular.sans,
                color = PeraTheme.colors.text.main
            )
            Text(
                text = stringResource(R.string.you_included, numberOfAccounts),
                style = PeraTheme.typography.footnote.sans,
                color = PeraTheme.colors.text.gray
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                modifier = Modifier.size(32.dp),
                painter = painterResource(R.drawable.ic_joint),
                contentDescription = stringResource(R.string.joint_account),
                tint = PeraTheme.colors.text.grayLighter
            )
            Text(
                text = numberOfAccounts.toString(),
                style = PeraTheme.typography.title.small.sansMedium,
                color = PeraTheme.colors.text.grayLighter
            )
        }
    }
}

@Composable
private fun ThresholdControlSection(
    threshold: Int,
    numberOfAccounts: Int,
    onThresholdChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.threshold),
            style = PeraTheme.typography.body.regular.sans,
            color = PeraTheme.colors.text.main
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DecreaseThresholdButton(
                threshold = threshold,
                onDecrease = { onThresholdChange(threshold - 1) }
            )
            ThresholdValueDisplay(threshold = threshold)
            IncreaseThresholdButton(
                threshold = threshold,
                numberOfAccounts = numberOfAccounts,
                onIncrease = { onThresholdChange(threshold + 1) }
            )
        }
    }
}

@Composable
private fun DecreaseThresholdButton(
    threshold: Int,
    onDecrease: () -> Unit
) {
    val isEnabled = threshold > JointAccountConstants.MIN_THRESHOLD
    val decreaseDescription = stringResource(R.string.decrease)
    Box(
        modifier = Modifier
            .size(40.dp)
            .semantics { contentDescription = decreaseDescription }
            .background(
                color = if (isEnabled) {
                    PeraTheme.colors.helper.positiveLighter
                } else {
                    PeraTheme.colors.layer.grayLighter
                },
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(
                enabled = isEnabled,
                onClick = onDecrease,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "−",
            style = PeraTheme.typography.title.regular.sansMedium,
            color = if (isEnabled) {
                PeraTheme.colors.helper.positive
            } else {
                PeraTheme.colors.text.gray
            }
        )
    }
}

@Composable
private fun ThresholdValueDisplay(threshold: Int) {
    Text(
        text = threshold.toString(),
        style = PeraTheme.typography.title.small.sansMedium,
        color = PeraTheme.colors.text.main
    )
}

@Composable
private fun IncreaseThresholdButton(
    threshold: Int,
    numberOfAccounts: Int,
    onIncrease: () -> Unit
) {
    val isEnabled = threshold < numberOfAccounts
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(
                color = if (isEnabled) {
                    PeraTheme.colors.helper.positiveLighter
                } else {
                    PeraTheme.colors.layer.grayLighter
                },
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(
                enabled = isEnabled,
                onClick = onIncrease,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(R.drawable.ic_plus),
            contentDescription = stringResource(R.string.increase),
            tint = if (isEnabled) {
                PeraTheme.colors.helper.positive
            } else {
                PeraTheme.colors.text.gray
            }
        )
    }
}

@Composable
private fun ContinueButtonSection(
    numberOfAccounts: Int,
    onContinueClick: () -> Unit
) {
    val buttonState = if (numberOfAccounts >= JointAccountConstants.MIN_THRESHOLD) {
        PeraButtonState.ENABLED
    } else {
        PeraButtonState.DISABLED
    }

    PeraPrimaryButton(
        text = stringResource(R.string.continue_text),
        onClick = onContinueClick,
        state = buttonState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp)
    )
}

interface SetThresholdScreenListener {
    fun onBackClick()
    fun onContinueClick(threshold: Int)
}
