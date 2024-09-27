/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.module.drawable.asset

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import com.algorand.android.designsystem.R
import kotlinx.parcelize.Parcelize

@Parcelize
class AlgoDrawableProvider : BaseAssetDrawableProvider() {

    override val logoUri: String?
        get() = null

    override fun getAssetDrawable(
        imageView: ImageView,
        onPreparePlaceHolder: (Context, Int) -> Drawable?,
        onResourceReady: (Drawable) -> Unit,
        onResourceFailed: (Drawable?) -> Unit,
        onUriReady: (String) -> Unit
    ) {
        val algoDrawable = AppCompatResources.getDrawable(imageView.context, R.drawable.ic_algo_round)
        imageView.setImageDrawable(algoDrawable)
        if (algoDrawable != null) {
            onResourceReady(algoDrawable)
        }
    }

    override fun createPlaceHolder(context: Context, width: Int): Drawable? {
        return AppCompatResources.getDrawable(context, R.drawable.ic_algo_round)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AlgoDrawableProvider

        return logoUri == other.logoUri
    }

    override fun hashCode(): Int {
        return logoUri?.hashCode() ?: 0
    }
}
