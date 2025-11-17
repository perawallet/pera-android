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
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import com.algorand.android.R
import com.algorand.android.utils.PassphraseKeywordUtils.generatePassphraseValidationItems
import com.algorand.android.utils.PassphraseValidationItem

class PassphraseValidationGroupView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val passphraseValidationViews = mutableListOf<PassphraseValidatorView>()

    private val passphraseValidatorViewListener = PassphraseValidatorView.Listener {
        listener?.onInputUpdate(passphraseValidationViews.all { it.isWordSelected() })
    }

    init {
        orientation = VERTICAL
    }

    private var listener: Listener? = null

    fun setupUI(words: List<String>, listener: Listener?) {
        this.listener = listener
        recreateUI(words, isFirstSetup = true)
    }

    fun recreateUI(words: List<String>, isFirstSetup: Boolean = false) {
        if (passphraseValidationViews.isNotEmpty()) {
            passphraseValidationViews.clear()
            removeAllViews()
        }
        addPassphraseValidationViews(words)
        if (isFirstSetup.not()) {
            listener?.onInputUpdate(allWordsSelected = false)
        }
    }

    private fun addPassphraseValidationViews(words: List<String>) {
        val validationItems = generatePassphraseValidationItems(
            words = words,
            itemCount = VALIDATION_VIEW_COUNT,
            perItemCount = PER_ITEM_COUNT
        )
        validationItems.forEachIndexed { index, item ->
            addPassphraseValidationView(
                passphraseValidatorView = createPassphraseValidatorView(item),
                addMarginToBottom = index + 1 != validationItems.size
            )
        }
    }

    private fun addPassphraseValidationView(
        passphraseValidatorView: PassphraseValidatorView,
        addMarginToBottom: Boolean
    ) {
        addView(passphraseValidatorView)
        passphraseValidationViews.add(passphraseValidatorView)
        if (addMarginToBottom) {
            passphraseValidatorView.updateLayoutParams<LayoutParams> {
                updateMargins(bottom = resources.getDimensionPixelOffset(R.dimen.passphrase_validation_bottom_margin))
            }
        }
    }

    private fun createPassphraseValidatorView(
        item: PassphraseValidationItem
    ): PassphraseValidatorView {
        return PassphraseValidatorView(context).apply {
            setup(
                words = item.options,
                correctWord = item.correctWord,
                correctWordPosition = item.correctWordIndex,
                listener = passphraseValidatorViewListener
            )
        }
    }

    fun isValidated(): Boolean {
        return passphraseValidationViews.isNotEmpty() && passphraseValidationViews.all { it.isValidated() }
    }

    interface Listener {
        fun onInputUpdate(allWordsSelected: Boolean)
    }

    companion object {
        private const val VALIDATION_VIEW_COUNT = 4
        private const val PER_ITEM_COUNT = 3
    }
}
