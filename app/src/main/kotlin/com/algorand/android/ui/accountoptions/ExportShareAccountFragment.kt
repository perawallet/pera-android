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

package com.algorand.android.ui.accountoptions

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.algorand.android.R
import com.algorand.android.core.DaggerBaseFragment
import com.algorand.android.databinding.FragmentExportShareAccountBinding
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.utils.getQrCodeBitmap
import com.algorand.android.utils.openTextShareBottomMenuChooser
import com.algorand.android.utils.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExportShareAccountFragment : DaggerBaseFragment(R.layout.fragment_export_share_account) {

    private val toolbarConfiguration = ToolbarConfiguration(
        startIconResId = R.drawable.ic_close,
        startIconClick = ::navBack
    )

    override val fragmentConfiguration: FragmentConfiguration = FragmentConfiguration(
        firebaseEventScreenId = FIREBASE_EVENT_SCREEN_ID,
        toolbarConfiguration = toolbarConfiguration
    )

    private val qrCodeBitmap by lazy {
        getQrCodeBitmap(resources.getDimensionPixelSize(R.dimen.show_qr_size), getExportUrl())
    }

    private val binding by viewBinding(FragmentExportShareAccountBinding::bind)

    private val args: ExportShareAccountFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getAppToolbar()?.changeTitle(getString(R.string.export_share_account))
        with(binding) {
            exportUrlTextView.text = getExportUrl()
            qrImageView.setImageBitmap(qrCodeBitmap)
            copyUrlButton.setOnClickListener { onCopyUrlClick() }
            shareUrlButton.setOnClickListener { onShareUrlClick() }
        }
    }

    private fun getExportUrl(): String {
        return "perawallet://app/joint-account-import/?address=${args.accountAddress}"
    }

    private fun onCopyUrlClick() {
        onAccountAddressCopied(getExportUrl())
    }

    private fun onShareUrlClick() {
        val exportUrl = getExportUrl()
        requireContext().openTextShareBottomMenuChooser(
            text = exportUrl,
            title = getString(R.string.export_share_account)
        )
    }

    companion object {
        private const val FIREBASE_EVENT_SCREEN_ID = "screen_export_share_account"
    }
}
