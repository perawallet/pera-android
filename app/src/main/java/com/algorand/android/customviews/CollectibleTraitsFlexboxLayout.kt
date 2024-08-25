/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.algorand.android.customviews

import android.content.Context
import android.util.AttributeSet
import com.algorand.android.assetdetailui.detail.nftprofile.model.CollectibleTraitItem
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout

class CollectibleTraitsFlexboxLayout(
    context: Context,
    attrs: AttributeSet? = null
) : FlexboxLayout(context, attrs) {

    init {
        flexWrap = FlexWrap.WRAP
        flexDirection = FlexDirection.ROW
    }

    fun initView(traits: List<CollectibleTraitItem>) {
        removeAllViews()
        createTraitItemViews(traits)
    }

    private fun createTraitItemViews(traits: List<CollectibleTraitItem>) {
        traits.forEach {
            val propertyView = CollectibleTraitItemView.create(context, it)
            addView(propertyView)
        }
    }
}
