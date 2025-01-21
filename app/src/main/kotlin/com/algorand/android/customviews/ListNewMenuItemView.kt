package com.algorand.android.customviews

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.use
import androidx.core.view.isVisible
import com.algorand.android.R
import com.algorand.android.databinding.CustomListNewMenuItemViewBinding
import com.algorand.android.utils.extensions.setImageResAndVisibility
import com.algorand.android.utils.extensions.setTextAndVisibility
import com.algorand.android.utils.viewbinding.viewBinding

class ListNewMenuItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private val binding = viewBinding(CustomListNewMenuItemViewBinding::inflate)

    init {
        initAttributes(attrs)
    }

    @SuppressLint("CustomViewStyleable")
    private fun initAttributes(attrs: AttributeSet?) {
        context.obtainStyledAttributes(attrs, R.styleable.ListMenuItemView).use {
            val title = it.getString(R.styleable.ListMenuItemView_title)
            val description = it.getString(R.styleable.ListMenuItemView_description)
            val iconResId = it.getResourceId(R.styleable.ListMenuItemView_icon, -1)
            val isDividerVisible = it.getBoolean(R.styleable.ListMenuItemView_showBottomDivider, false)

            with(binding) {
                titleTextView.setTextAndVisibility(title)
                descriptionTextView.setTextAndVisibility(description)
                iconImageView.setImageResAndVisibility(iconResId)
                dividerView.isVisible = isDividerVisible
            }
        }
    }
}
