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

package com.algorand.android.customviews.accountassetitem

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.use
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.algorand.android.R
import com.algorand.android.databinding.ItemAccountAndAssetListBinding
import com.algorand.android.utils.extensions.hide
import com.algorand.android.utils.extensions.show
import com.algorand.android.utils.viewbinding.viewBinding

@Suppress("UnnecessaryAbstractClass")
abstract class BaseAccountAndAssetItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    protected val binding: ItemAccountAndAssetListBinding = viewBinding(ItemAccountAndAssetListBinding::inflate)

    init {
        initRootView(attrs)
    }

    fun getStartIconImageView(): AppCompatImageView = binding.startIconImageView

    fun setStartIconDrawable(drawable: Drawable?) {
        binding.startIconImageView.apply {
            isVisible = drawable != null
            setImageDrawable(drawable)
        }
    }

    fun setStartIconResource(@DrawableRes iconResId: Int?) {
        binding.startIconImageView.apply {
            isVisible = iconResId != null
            if (iconResId == null) return
            setImageResource(iconResId)
        }
    }

    fun setTitleText(title: String?) {
        binding.titleTextView.apply {
            isVisible = !title.isNullOrBlank()
            text = title
        }
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

    fun setEndIconDrawable(drawable: Drawable?) {
        binding.endIconImageView.apply {
            isVisible = drawable != null
            setImageDrawable(drawable)
        }
        updateStatefulButtonsFlowPaddingIfNeeded()
    }

    fun setEndIconResource(@DrawableRes endIconDrawableResId: Int) {
        binding.endIconImageView.apply {
            setImageResource(endIconDrawableResId)
            show()
        }
        binding.endIconImageView.setImageResource(endIconDrawableResId)
    }

    fun setEndIconClickListener(onClick: (() -> Unit)?) {
        binding.endIconImageView.setOnClickListener { onClick?.invoke() }
    }

    fun setStartSmallIconDrawableResource(@DrawableRes drawableResId: Int?) {
        binding.startSmallIconImageView.apply {
            isVisible = drawableResId != null
            drawableResId?.let { setImageResource(it) }
        }
    }

    fun setParticipantCountBadge(participantCount: Int?) {
        binding.participantCountBadgeTextView.apply {
            isVisible = participantCount != null
            text = participantCount?.toString().orEmpty()
        }
    }

    fun setPrimaryValueText(primaryValue: String?) {
        binding.primaryValueTextView.apply {
            isVisible = !primaryValue.isNullOrBlank()
            text = primaryValue
        }
    }

    fun setPrimaryValueText(primaryValue: String?, isPrimaryValueVisible: Boolean) {
        binding.primaryValueTextView.apply {
            isVisible = isPrimaryValueVisible
            text = primaryValue
        }
    }

    fun setSecondaryValueText(secondaryValue: String?) {
        binding.secondaryValueTextView.apply {
            isVisible = !secondaryValue.isNullOrBlank()
            text = secondaryValue
        }
    }

    fun setSecondaryValueText(secondaryValue: String?, isSecondaryValueVisible: Boolean) {
        binding.secondaryValueTextView.apply {
            isVisible = isSecondaryValueVisible
            text = secondaryValue
        }
    }

    fun setTrailingIconOfTitleText(@DrawableRes iconResId: Int?) {
        if (iconResId == null) {
            binding.titleEndIconImageView.hide()
        } else {
            binding.titleEndIconImageView.setImageResource(iconResId)
        }
    }

    fun setTrailingIconOfTitleText(iconDrawable: Drawable?) {
        if (iconDrawable == null) {
            binding.titleEndIconImageView.hide()
        } else {
            binding.titleEndIconImageView.setImageDrawable(iconDrawable)
        }
    }

    fun setIsFavorite(isFavorite: Boolean) {
        binding.favoriteIconImageView.isVisible = isFavorite
    }

    fun setStartIconProgressBarVisibility(isVisible: Boolean) {
        binding.startIconProgressBar.isVisible = isVisible
    }

    private fun initRootView(attrs: AttributeSet?) {
        val defaultPadding = resources.getDimension(R.dimen.spacing_xlarge).toInt()
        context.obtainStyledAttributes(attrs, R.styleable.BaseAccountAndAssetItemView).use {
            val horizontalPadding = it.getDimensionPixelSize(
                R.styleable.BaseAccountAndAssetItemView_rootHorizontalPadding,
                defaultPadding
            )
            updatePadding(left = horizontalPadding, right = horizontalPadding)
            binding.dividerView.isVisible = it.getBoolean(
                R.styleable.BaseAccountAndAssetItemView_isDividerVisible,
                true
            )
        }
        minHeight = resources.getDimensionPixelSize(R.dimen.account_asset_item_view_min_height)
    }

    fun setPrimaryValueTextColor(@ColorRes colorResId: Int) {
        val color = ContextCompat.getColor(context, colorResId)
        binding.primaryValueTextView.setTextColor(color)
    }

    fun setSecondaryValueTextColor(@ColorRes colorResId: Int) {
        val color = ContextCompat.getColor(context, colorResId)
        binding.secondaryValueTextView.setTextColor(color)
    }

    protected fun updateStatefulButtonsFlowPaddingIfNeeded() {
        val hasVisibleStatefulButton = binding.statefulButtonsFlow.referencedIds.any { viewId ->
            findViewById<View>(viewId).isVisible
        }
        val startPadding = if (hasVisibleStatefulButton) resources.getDimensionPixelSize(R.dimen.spacing_small) else 0
        binding.statefulButtonsFlow.paddingLeft = startPadding
    }
}
