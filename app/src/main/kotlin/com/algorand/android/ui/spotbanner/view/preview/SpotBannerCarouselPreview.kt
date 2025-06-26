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

package com.algorand.android.ui.spotbanner.view.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.algorand.android.ui.spotbanner.view.SpotBannerCarousel
import com.algorand.wallet.spotbanner.domain.model.SpotBanner

@Preview(showBackground = true)
@Composable
fun SpotBannerCarouselPreview(
    @PreviewParameter(SpotBannerCarouselPreviewProvider::class) spotBanners: List<SpotBanner>
) {
    SpotBannerCarousel(spotBanners, listener = null)
}

private class SpotBannerCarouselPreviewProvider : PreviewParameterProvider<List<SpotBanner>> {

    override val values: Sequence<List<SpotBanner>> = listOf(
        listOf(SpotBanner.BackupPassphrase),
        listOf(createGenericBanner(), createGenericBanner())
    ).asSequence()

    private fun createGenericBanner(): SpotBanner.Generic {
        return SpotBanner.Generic(1L, text = "Title", image = null, url = null, isExternalButtonUrl = false)
    }
}
