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

package com.algorand.android.ui.compose.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.icon.PeraIconRoundShapeBig
import com.algorand.android.ui.compose.widget.text.PeraTitleText

@Composable
fun AnimationLoader(
    modifier: Modifier = Modifier,
    start: ImageVector,
    lottie: LottieCompositionSpec,
    end: ImageVector,
    description: String
) {
    val preloaderLottieComposition by rememberLottieComposition(lottie)
    val preloaderProgress by animateLottieCompositionAsState(
        preloaderLottieComposition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true
    )
    Column(modifier = modifier) {
        Row(modifier = Modifier.align(alignment = Alignment.CenterHorizontally)) {
            PeraIconRoundShapeBig(
                imageVector = start,
                contentDescription = "start side of the animation"
            )
            LottieAnimation(
                composition = preloaderLottieComposition,
                progress = { preloaderProgress },
                modifier = Modifier
                    .width(120.dp)
                    .align(alignment = Alignment.CenterVertically),
            )
            PeraIconRoundShapeBig(
                imageVector = end,
                contentDescription = "end side of the animation"
            )
        }
        PeraTitleText(
            modifier = Modifier
                .padding(top = 40.dp)
                .align(alignment = Alignment.CenterHorizontally),
            text = description
        )
    }
}

@PreviewLightDark
@Composable
fun AnimationLoaderPreview() {
    PeraTheme {
        AnimationLoader(
            start = ImageVector.vectorResource(R.drawable.ic_ledger_old_export),
            end = ImageVector.vectorResource(R.drawable.ic_phone_new),
            lottie = LottieCompositionSpec.RawRes(resId = R.raw.loading_dots),
            description = "Searching your accounts"
        )
    }
}
