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

package com.algorand.android.modules.assets.core.ui.domain.usecase

import android.content.res.Resources
import com.algorand.android.R
import com.algorand.android.modules.assets.core.ui.domain.model.AssetName
import java.util.Locale

internal class GetAssetNameUseCase(
    private val resources: Resources
) : GetAssetName {

    override fun invoke(name: String?): AssetName {
        val safeName = name ?: resources.getString(DEFAULT_ASSET_NAME_RES_ID)
        val safeAvatarName = getSafeAvatarName(safeName)
        return AssetName(safeName, safeAvatarName)
    }

    private fun getSafeAvatarName(name: String): String {
        val splitItem = name.trim().split(" ", "-").filter { it.isNotBlank() }
        return if (splitItem.size == 1) {
            splitItem.firstOrNull()
        } else {
            splitItem.joinToString("") { s -> s.substring(0, 1) }
        }?.take(ASSET_AVATAR_MAX_LETTER_COUNT)?.uppercase(Locale.ENGLISH).orEmpty()
    }

    private companion object {
        val DEFAULT_ASSET_NAME_RES_ID = R.string.unnamed
        const val ASSET_AVATAR_MAX_LETTER_COUNT = 3
    }
}
