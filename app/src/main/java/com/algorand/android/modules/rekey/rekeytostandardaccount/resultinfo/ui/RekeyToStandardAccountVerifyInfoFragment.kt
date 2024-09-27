/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.rekey.rekeytostandardaccount.resultinfo.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.algorand.android.R
import com.algorand.android.RekeyToStandardAccountNavigationDirections
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.module.account.core.ui.model.AccountDisplayName
import com.algorand.android.ui.common.BaseInfoFragment
import com.algorand.android.utils.extensions.collectOnLifecycle
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RekeyToStandardAccountVerifyInfoFragment : BaseInfoFragment() {

    override val fragmentConfiguration = FragmentConfiguration()

    private val rekeyToStandardAccountVerifyInfoViewModel by viewModels<RekeyToStandardAccountVerifyInfoViewModel>()

    private val accountDisplayNameCollector: suspend (AccountDisplayName?) -> Unit = {
        if (it != null) {
            val description = getString(
                R.string.the_account_name_was_successfully_rekeyed_formatted,
                it.primaryDisplayName
            )
            setDescriptionText(description)
        }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            popRekeyToStandardAccountNavigationUp()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, onBackPressedCallback)
        viewLifecycleOwner.collectOnLifecycle(
            rekeyToStandardAccountVerifyInfoViewModel.accountDisplayNameFlow,
            accountDisplayNameCollector
        )
    }

    override fun setImageView(imageView: ImageView) {
        with(imageView) {
            setImageResource(R.drawable.ic_check)
            setColorFilter(ContextCompat.getColor(requireContext(), R.color.info_image_color))
        }
    }

    override fun setTitleText(textView: TextView) {
        textView.setText(R.string.account_successfully_rekeyed)
    }

    override fun setDescriptionText(textView: TextView) {
        // TODO make this optional
    }

    override fun setFirstButton(materialButton: MaterialButton) {
        with(materialButton) {
            text = getString(R.string.done)
            setOnClickListener { popRekeyToStandardAccountNavigationUp() }
        }
    }

    private fun popRekeyToStandardAccountNavigationUp() {
        nav(RekeyToStandardAccountNavigationDirections.actionRekeyToStandardAccountNavigationPop())
    }
}
