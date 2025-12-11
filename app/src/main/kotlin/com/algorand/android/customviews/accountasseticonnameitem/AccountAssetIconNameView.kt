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

package com.algorand.android.customviews.accountasseticonnameitem

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.algorand.android.databinding.ItemAccountAssetIconNameBinding
import com.algorand.android.utils.setDrawable
import com.algorand.android.utils.viewbinding.viewBinding

class AccountAssetIconNameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private val binding = viewBinding(ItemAccountAssetIconNameBinding::inflate)

    fun getStartIconImageView(): AppCompatImageView = binding.startIconImageView

    fun setTitleText(title: String?) {
        binding.titleTextView.apply {
            isVisible = !title.isNullOrBlank()
            text = title
        }
    }

    fun setTitleText(@StringRes titleResId: Int) {
        binding.titleTextView.setText(titleResId)
    }

    fun setTitleTextColor(@ColorRes colorResId: Int) {
        val color = ContextCompat.getColor(context, colorResId)
        binding.titleTextView.setTextColor(color)
    }

    fun setDescriptionText(description: String?) {
        binding.descriptionTextView.apply {
            isVisible = !description.isNullOrBlank()
            text = description
        }
    }

    fun setDescriptionText(@StringRes textResId: Int) {
        binding.descriptionTextView.setText(textResId)
    }

    fun setTrailingIconOfTitleText(@DrawableRes iconResId: Int?) {
        val endIconDrawable = if (iconResId != null) AppCompatResources.getDrawable(context, iconResId) else null
        binding.titleTextView.setDrawable(end = endIconDrawable)
    }

    fun setTrailingIconOfTitleText(iconDrawable: Drawable?) {
        binding.titleTextView.setDrawable(end = iconDrawable)
    }
}
