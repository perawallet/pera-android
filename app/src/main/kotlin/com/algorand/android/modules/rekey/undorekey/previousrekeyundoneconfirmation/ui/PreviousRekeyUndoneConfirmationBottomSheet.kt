/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.rekey.undorekey.previousrekeyundoneconfirmation.ui

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.algorand.android.R
import com.algorand.android.models.AnnotatedString
import com.algorand.android.modules.rekey.undorekey.resultinfo.ui.model.PreviousRekeyUndoneConfirmationPreview
import com.algorand.android.utils.BaseDoubleButtonBottomSheet
import com.algorand.android.utils.browser.REKEY_SUPPORT_URL
import com.algorand.android.utils.browser.openUrl
import com.algorand.android.utils.extensions.collectOnLifecycle
import com.algorand.android.utils.getCustomClickableSpan
import com.algorand.android.utils.getXmlStyledString
import com.algorand.android.utils.setFragmentNavigationResult
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PreviousRekeyUndoneConfirmationBottomSheet : BaseDoubleButtonBottomSheet() {

    private val previousRekeyUndoneConfirmationViewModel by viewModels<PreviousRekeyUndoneConfirmationViewModel>()

    private val previousRekeyUndoneConfirmationPreviewCollector: suspend (
        PreviousRekeyUndoneConfirmationPreview?
    ) -> Unit = { preview ->
        preview?.let { updateUi(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
    }

    private fun initObservers() {
        viewLifecycleOwner.collectOnLifecycle(
            previousRekeyUndoneConfirmationViewModel.undoRekeyVerifyInfoPreviewFlow,
            previousRekeyUndoneConfirmationPreviewCollector
        )
    }

    override fun setTitleText(textView: TextView) {
        textView.setText(R.string.your_previous_rekey_will_be_undone)
    }

    override fun setDescriptionText(textView: TextView) {
        // Description will be observed
    }

    private fun updateUi(preview: PreviousRekeyUndoneConfirmationPreview) {
        getDescriptionTextView().apply {
            val linkTextColor = ContextCompat.getColor(context, R.color.link_primary)
            val clickSpannable = getCustomClickableSpan(
                clickableColor = linkTextColor,
                onClick = { context?.openUrl(REKEY_SUPPORT_URL) }
            )
            val annotatedString = AnnotatedString(
                stringResId = R.string.auth_account_will_no_longer_be,
                replacementList = listOf(
                    "account_address" to preview.accountName,
                    "auth_account_address" to preview.rekeyAdminAccountName
                ),
                customAnnotationList = listOf("learn_more" to clickSpannable)
            )
            highlightColor = ContextCompat.getColor(context, R.color.transparent)
            movementMethod = LinkMovementMethod.getInstance()
            text = context?.getXmlStyledString(annotatedString)
        }
    }

    override fun setAcceptButton(materialButton: MaterialButton) {
        materialButton.apply {
            setText(R.string.confirm)
            setOnClickListener {
                setFragmentNavigationResult(PREVIOUS_REKEY_UNDONE_CONFIRMATION_KEY, true)
                navBack()
            }
        }
    }

    override fun setCancelButton(materialButton: MaterialButton) {
        materialButton.apply {
            setText(R.string.cancel)
            setOnClickListener { navBack() }
        }
    }

    override fun setIconImageView(imageView: ImageView) {
        imageView.apply {
            setImageResource(R.drawable.ic_error)
            imageTintList = ContextCompat.getColorStateList(context, R.color.negative)
        }
    }

    companion object {
        const val PREVIOUS_REKEY_UNDONE_CONFIRMATION_KEY = "previous_rekey_undone_confirmation"
    }
}
