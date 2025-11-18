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

package com.algorand.android.ui.compose.widget.asset.icon.mapper

import com.algorand.android.ui.compose.widget.asset.icon.AssetIconDrawable
import com.algorand.test.peraFixture
import com.algorand.wallet.asset.domain.model.AssetLite
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_ID
import kotlin.test.assertEquals
import org.junit.Test

class DefaultAssetIconDrawableMapperTest {

    private val sut = DefaultAssetIconDrawableMapper()

    @Test
    fun `EXPECT ALGO drawable WHEN asset lite is ALGO`() {
        val assetLite = peraFixture<AssetLite>().copy(assetId = ALGO_ID)

        val result = sut.map(assetLite)

        assertEquals(AssetIconDrawable.AlgoDrawable, result)
    }

    @Test
    fun `EXPECT ASSET drawable WHEN asset lite is not ALGO`() {
        val assetLite = peraFixture<AssetLite>().copy(
            assetId = 123L,
            shortName = "unit name",
            type = AssetLite.Type.Asset(logoUrl = "logo url")
        )

        val result = sut.map(assetLite)

        val expected = AssetIconDrawable.AssetDrawable(url = "logo url", unitName = "unit name")
        assertEquals(expected, result)
    }
}
