@file:Suppress("EmptyFunctionBlock", "Unused", "MagicNumber")
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

package com.algorand.android.modules.addaccount.joint.transaction.ui.preview

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.modules.accountcore.ui.usecase.AccountIconDrawablePreviews
import com.algorand.android.ui.compose.preview.PeraPreviewLightDark
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.AccountIcon
import com.algorand.android.ui.compose.widget.PeraToolbar
import com.algorand.android.ui.compose.widget.PeraToolbarIcon
import com.algorand.android.ui.compose.widget.button.slidetoconfirm.SlideToConfirm
import com.algorand.android.ui.compose.widget.button.slidetoconfirm.SlideToConfirmButton
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple
import com.algorand.android.ui.compose.widget.progress.PeraCircularProgressIndicator

@PeraPreviewLightDark
@Composable
fun JointAccountSignRequestScreenPreview() {
    PeraTheme {
        JointAccountSignRequestContentPreview()
    }
}

@PeraPreviewLightDark
@Composable
fun JointAccountSignRequestScreenLoadingPreview() {
    PeraTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PeraTheme.colors.background.primary),
            contentAlignment = Alignment.Center
        ) {
            PeraCircularProgressIndicator()
        }
    }
}

@Composable
private fun JointAccountSignRequestContentPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PeraTheme.colors.background.primary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 180.dp)
        ) {
            ToolbarSectionPreview()
            Spacer(modifier = Modifier.height(48.dp))
            JointAccountIconSectionPreview()
            Spacer(modifier = Modifier.height(24.dp))
            TransferToSectionPreview()
            Spacer(modifier = Modifier.height(12.dp))
            AmountSectionPreview()
        }
        BottomSectionPreview(modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun ToolbarSectionPreview() {
    Column {
        PeraToolbar(
            modifier = Modifier.padding(horizontal = 12.dp),
            text = stringResource(R.string.review_transaction),
            startContainer = {
                PeraToolbarIcon(
                    iconResId = R.drawable.ic_close,
                    modifier = Modifier.clickableNoRipple(onClick = {})
                )
            }
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AccountIcon(
                modifier = Modifier.size(20.dp),
                iconDrawablePreview = AccountIconDrawablePreviews.getJointDrawable()
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Joint Account #1",
                style = PeraTheme.typography.footnote.sans,
                color = PeraTheme.colors.text.gray
            )
        }
    }
}

@Composable
private fun JointAccountIconSectionPreview() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(color = PeraTheme.colors.layer.grayLighter, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(48.dp),
                painter = painterResource(R.drawable.ic_joint),
                contentDescription = null,
                tint = PeraTheme.colors.text.gray
            )
        }
    }
}

@Composable
private fun TransferToSectionPreview() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = PeraTheme.colors.text.gray)) { append("Transfer to ") }
                withStyle(
                    SpanStyle(
                        color = PeraTheme.colors.text.main,
                        fontWeight = PeraTheme.typography.body.regular.sansMedium.fontWeight
                    )
                ) { append("JDM35...XJD3M") }
            },
            style = PeraTheme.typography.body.regular.sans
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = {}, modifier = Modifier.size(16.dp)) {
            Icon(
                modifier = Modifier
                    .size(16.dp)
                    .padding(1.dp),
                painter = painterResource(R.drawable.ic_copy),
                contentDescription = stringResource(R.string.copy),
                tint = PeraTheme.colors.text.gray
            )
        }
    }
}

@Composable
private fun AmountSectionPreview() {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "₳21.6500",
            style = PeraTheme.typography.title.regular.sansMedium,
            color = PeraTheme.colors.text.main
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$6.24",
            style = PeraTheme.typography.body.regular.sans,
            color = PeraTheme.colors.text.main,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun BottomSectionPreview(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(PeraTheme.colors.background.primary)
    ) {
        HorizontalDivider(color = PeraTheme.colors.layer.grayLighter, thickness = 1.dp)
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.transacting_fee),
                    style = PeraTheme.typography.footnote.sans,
                    color = PeraTheme.colors.text.gray
                )
                Text(
                    text = "-₳0.002",
                    style = PeraTheme.typography.body.regular.sansMedium,
                    color = PeraTheme.colors.status.negative
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.clickable(onClick = {}), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.show_transaction_details),
                    style = PeraTheme.typography.footnote.sansMedium,
                    color = PeraTheme.colors.link.primary
                )
                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(R.drawable.ic_right_arrow),
                    contentDescription = null,
                    tint = PeraTheme.colors.link.primary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            val buttonState = remember { mutableStateOf(SlideToConfirm.ButtonState.Idle) }
            SlideToConfirmButton(
                modifier = Modifier.fillMaxWidth(),
                buttonState = buttonState,
                onConfirmed = {}
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = {})
                    .padding(vertical = 14.dp),
                text = stringResource(R.string.decline),
                style = PeraTheme.typography.body.regular.sansMedium,
                color = PeraTheme.colors.status.negative,
                textAlign = TextAlign.Center
            )
        }
    }
}
