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

package com.algorand.android.ui.swap.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.button.PeraPrimaryButton

@Composable
fun SwapScreenIntroductionState(modifier: Modifier = Modifier, listener: SwapScreenIntroductionStateListener) {
    Box(modifier.fillMaxSize()) {
        val density = LocalDensity.current
        var bottomContainerHeight by remember { mutableStateOf(0.dp) }
        Column {
            ImageContainer()
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = bottomContainerHeight + 16.dp)
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    text = stringResource(R.string.swap_in_pera),
                    style = PeraTheme.typography.title.large.sansMedium,
                    color = PeraTheme.colors.text.main
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    modifier = Modifier
                        .padding(start = 24.dp)
                        .background(PeraTheme.colors.helper.positiveLighter, RoundedCornerShape(8.dp))
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    text = stringResource(R.string.new_text),
                    style = PeraTheme.typography.footnote.sans,
                    color = PeraTheme.colors.helper.positive
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    text = stringResource(R.string.pera_wallet_provides_an_easy),
                    color = PeraTheme.colors.text.gray,
                    style = PeraTheme.typography.body.regular.sans
                )
            }
        }
        BottomButtonContainer(listener) {
            bottomContainerHeight = with(density) { it.height.toDp() }
        }
    }
}

@Composable
private fun ImageContainer() {
    Image(
        modifier = Modifier
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF18181B), Color(0xFF242427), Color(0xFF18181B)),
                    radius = 1f
                )
            )
            .fillMaxWidth()
            .aspectRatio(1.5f),
        painter = painterResource(R.drawable.swap_introduction),
        contentDescription = null
    )
}

@Composable
private fun BoxScope.BottomButtonContainer(
    listener: SwapScreenIntroductionStateListener,
    onSizeChanged: (IntSize) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .align(Alignment.BottomCenter)
            .onSizeChanged { onSizeChanged(it) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = getPoweredByText())
        Spacer(modifier = Modifier.height(16.dp))
        PeraPrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.start_swapping),
            onClick = { listener.onStartSwappingClick() },
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = getTermsOfServiceText(listener::onTermsOfServiceClick),
            style = PeraTheme.typography.footnote.sans,
            color = PeraTheme.colors.text.gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun getPoweredByText() = buildAnnotatedString {
    withStyle(style = PeraTheme.typography.footnote.sans.toSpanStyle(PeraTheme.colors.text.grayLighter)) {
        append(stringResource(R.string.powered_by))
    }
    append(" ")
    withStyle(style = PeraTheme.typography.footnote.sansMedium.toSpanStyle(PeraTheme.colors.text.main)) {
        append(stringResource(R.string.pera_dot_separated))
    }
}

private fun TextStyle.toSpanStyle(color: Color): SpanStyle {
    return SpanStyle(
        color = color,
        fontFamily = fontFamily,
        fontSize = fontSize,
        fontWeight = fontWeight,
        fontStyle = fontStyle
    )
}

interface SwapScreenIntroductionStateListener {
    fun onStartSwappingClick()
    fun onTermsOfServiceClick()
}

@Composable
private fun getTermsOfServiceText(onClick: () -> Unit): AnnotatedString {
    return buildAnnotatedString {
        append(stringResource(R.string.by_continuing_you_agree))
        append(" ")
        val annotation = LinkAnnotation.Clickable(
            tag = "url",
            styles = TextLinkStyles(
                style = SpanStyle(
                    color = PeraTheme.colors.link.primary,
                    fontStyle = PeraTheme.typography.footnote.sansMedium.fontStyle
                )
            ),
            linkInteractionListener = object : LinkInteractionListener {
                override fun onClick(link: LinkAnnotation) {
                    onClick()
                }
            }
        )
        withLink(annotation) {
            append(stringResource(R.string.terms_of_service))
        }
    }
}
