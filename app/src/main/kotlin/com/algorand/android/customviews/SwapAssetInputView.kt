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

package com.algorand.android.customviews

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.use
import androidx.core.view.isVisible
import com.algorand.android.R
import com.algorand.android.databinding.CustomSwapAssetInputBinding
import com.algorand.android.models.CustomInputSavedState
import com.algorand.android.utils.requestFocusAndShowKeyboard
import com.algorand.android.utils.viewbinding.viewBinding

class SwapAssetInputView(context: Context, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {

    private val binding = viewBinding(CustomSwapAssetInputBinding::inflate)

    init {
        initAttributes(attrs)
        initViewIdAndConstraints()
        initRootClickListener()
    }

    private var latestAmountInputValue: String = ""

    private fun initAttributes(attrs: AttributeSet?) {
        context?.obtainStyledAttributes(attrs, R.styleable.SwapAssetInputView)?.use {
            with(binding) {
                titleTextView.apply {
                    val title = it.getString(R.styleable.SwapAssetInputView_title)
                    text = title
                    isVisible = !title.isNullOrBlank()
                }
                balanceTextView.isVisible = it.getBoolean(R.styleable.SwapAssetInputView_isBalanceVisible, true)
                it.getBoolean(R.styleable.SwapAssetInputView_isAssetChipClickable, true).let { isChipClickable ->
                    assetChipArrowImageView.isVisible = isChipClickable
                }
                it.getBoolean(R.styleable.SwapAssetInputView_isInputEnabled, true).let { isInputEnabled ->
                    if (!isInputEnabled) disableAmountInputEditText()
                }
            }
        }
    }

    fun setImageDrawable(drawable: Drawable?) {
        binding.assetIconImageView.setImageDrawable(drawable)
    }

    // Since there are 2 different SwapAssetInputView in the same layout, amountEditTexts have the same id
    // and this causes system to save & restore view states
    // Generating new id for amountEditText solves the issue
    private fun initViewIdAndConstraints() {
        val newViewId = generateViewId()
        binding.amountEditText.id = newViewId
        (binding.approximateValueTextView.layoutParams as? LayoutParams)?.run {
            topToBottom = newViewId
            startToStart = newViewId
            endToEnd = newViewId
        }
    }

    private fun showKeyboard() {
        with(binding.amountEditText) {
            if (isFocusable) requestFocusAndShowKeyboard()
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        return CustomInputSavedState(super.onSaveInstanceState(), latestAmountInputValue)
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
        (state as? CustomInputSavedState)?.run {
            latestAmountInputValue = text
        }
    }

    private fun disableAmountInputEditText() {
        binding.amountEditText.apply {
            isFocusable = false
            isClickable = false
            isFocusableInTouchMode = false
            keyListener = null
        }
    }

    private fun initRootClickListener() {
        binding.root.setOnClickListener {
            showKeyboard()
        }
    }
}
