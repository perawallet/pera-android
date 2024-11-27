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

package co.algorand.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.algorand.common.ui.typography.PeraTypography
import kotlinx.serialization.Serializable

@Serializable
data object PeraTypographyPreviewScreenNavigation

@Composable
fun PeraTypographyPreviewScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TitlePreview()
        BodyPreview()
        FootnotePreview()
        CaptionPreview()
    }
}

@Composable
private fun TitlePreview() {
    val typography = PeraTypography()
    Column {
        SectionTitle("Title Small")
        Text(
            text = "Sans",
            style = typography.title.small.sans
        )
        Text(
            text = "Sans Medium",
            style = typography.title.small.sansMedium
        )
        SectionTitle("Title Regular")
        Text(
            text = "Sans",
            style = typography.title.regular.sans
        )
        Text(
            text = "Sans Medium",
            style = typography.title.regular.sansMedium
        )
        Text(
            text = "Sans Bold",
            style = typography.title.regular.sansBold
        )
        SectionTitle("Title Large")
        Text(
            text = "Sans",
            style = typography.title.large.sans
        )
        Text(
            text = "Sans Medium",
            style = typography.title.large.sansMedium
        )
        Text(
            text = "Mono",
            style = typography.title.large.mono
        )
        Text(
            text = "Medium",
            style = typography.title.large.monoMedium
        )
    }
}

@Composable
private fun BodyPreview() {
    val typography = PeraTypography()
    Column {
        SectionTitle("Body Regular")
        Text(
            text = "Sans",
            style = typography.body.regular.sans
        )
        Text(
            text = "Sans Medium",
            style = typography.body.regular.sansMedium
        )
        Text(
            text = "Sans Bold",
            style = typography.body.regular.sansBold
        )
        Text(
            text = "Mono",
            style = typography.body.regular.mono
        )
        Text(
            text = "Mono Medium",
            style = typography.body.regular.monoMedium
        )
        SectionTitle("Body Large")
        Text(
            text = "Sans",
            style = typography.body.large.sans
        )
        Text(
            text = "Sans Medium",
            style = typography.body.large.sansMedium
        )
        Text(
            text = "Mono",
            style = typography.body.large.mono
        )
    }
}

@Composable
private fun FootnotePreview() {
    val typography = PeraTypography()
    Column {
        SectionTitle("Footnote")
        Text(
            text = "Sans",
            style = typography.footnote.sans
        )
        Text(
            text = "Sans Bold",
            style = typography.footnote.sansBold
        )
        Text(
            text = "Sans Medium",
            style = typography.footnote.sansMedium
        )
        Text(
            text = "Mono",
            style = typography.footnote.mono
        )
        Text(
            text = "Mono Medium",
            style = typography.footnote.monoMedium
        )
    }
}

@Composable
private fun CaptionPreview() {
    val typography = PeraTypography()
    Column {
        SectionTitle("Caption")
        Text(
            text = "Sans",
            style = typography.caption.sans
        )
        Text(
            text = "Sans Bold",
            style = typography.caption.sansBold
        )
        Text(
            text = "Sans Medium",
            style = typography.caption.sansMedium
        )
        Text(
            text = "Mono",
            style = typography.caption.mono
        )
        Text(
            text = "Mono Medium",
            style = typography.caption.monoMedium
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray, shape = RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp),
        text = title,
        style = PeraTypography().body.large.sansMedium
    )
}
