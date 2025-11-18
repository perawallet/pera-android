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

package com.algorand.android.ui.invite.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.bottomsheet.PeraBottomSheetDragIndicator
import com.algorand.android.ui.compose.widget.button.PeraPrimaryButton
import com.algorand.android.ui.compose.widget.button.PeraSecondaryButton

@Composable
fun InviteFriendsBottomSheetScreen(
    onShareClick: () -> Unit,
    onCloseClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = PeraTheme.colors.background.primary,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .padding(start = 24.dp, end = 24.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        PeraBottomSheetDragIndicator(modifier = Modifier.padding(top = 12.dp))
        Spacer(modifier = Modifier.height(32.dp))
        ShareAnimation()
        Spacer(modifier = Modifier.height(20.dp))
        TitleText()
        Spacer(modifier = Modifier.height(12.dp))
        DescriptionText()
        Spacer(modifier = Modifier.height(48.dp))
        ShareButton(onShareClick)
        Spacer(modifier = Modifier.height(12.dp))
        CloseButton(onCloseClick)
    }
}

@Composable
private fun ShareAnimation() {
    val compositionResult = LottieCompositionSpec.RawRes(resId = R.raw.invite_friends_animation)
    val lottieComposition by rememberLottieComposition(compositionResult)
    LottieAnimation(
        composition = lottieComposition,
        modifier = Modifier.size(96.dp),
    )
}

@Composable
private fun TitleText() {
    Text(
        text = stringResource(R.string.invite_friends),
        color = PeraTheme.colors.text.main,
        style = PeraTheme.typography.body.large.sansMedium
    )
}

@Composable
private fun DescriptionText() {
    Text(
        text = stringResource(R.string.share_pera_wallet_download),
        color = PeraTheme.colors.text.gray,
        style = PeraTheme.typography.body.regular.sans,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun ShareButton(onClick: () -> Unit) {
    PeraPrimaryButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        text = stringResource(R.string.share)
    )
}

@Composable
private fun CloseButton(onClick: () -> Unit) {
    PeraSecondaryButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        text = stringResource(R.string.close)
    )
}
