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

package com.algorand.android.modules.assetinbox.detail.transactiondetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isEmpty
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.algorand.android.R
import com.algorand.android.databinding.FragmentArc59TransactionDetailBinding
import com.algorand.android.databinding.LayoutArc59SendersAmountBinding
import com.algorand.android.modules.assetinbox.detail.receivedetail.ui.model.ReceiverAccountDetailPreview
import com.algorand.android.modules.assetinbox.detail.transactiondetail.model.Arc59TransactionDetailPreview
import com.algorand.android.utils.AccountIconDrawable
import com.algorand.android.utils.copyToClipboard
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.extensions.hide
import com.algorand.android.utils.extensions.show
import com.algorand.android.utils.loadImage
import com.algorand.android.utils.setDrawable
import com.algorand.android.utils.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Arc59TransactionDetailFragment : Fragment(R.layout.fragment_arc59_transaction_detail) {

    private val binding by viewBinding(FragmentArc59TransactionDetailBinding::bind)

    private val viewModel: Arc59TransactionDetailViewModel by viewModels()

    private val previewObserver: suspend (Arc59TransactionDetailPreview) -> Unit = {
        initPreview(it)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
    }

    private fun initObservers() {
        collectLatestOnLifecycle(
            flow = viewModel.previewFlow,
            collection = previewObserver
        )
    }

    private fun initPreview(preview: Arc59TransactionDetailPreview) {
        with(binding) {
            primaryTextView.text = preview.primaryText
            secondaryTextView.text = preview.secondaryText
            initAccountPreview(preview.receiverAccountDetailPreview)
            if (preview.nftUrl != null) {
                context?.loadImage(
                    uri = preview.nftUrl,
                    onResourceReady = {
                        collectibleImageView.setImageDrawable(it)
                        collectibleImageView.show()
                    },
                    onLoadFailed = { collectibleImageView.hide() }
                )
            }
            sendersTitleTextView.text = resources.getQuantityString(
                R.plurals.sender_plural,
                preview.sendersAmountMap.size,
                preview.sendersAmountMap.size
            )
            assetIdTextView.text = preview.assetId.toString()
            copyIdTextView.setOnClickListener { context?.copyToClipboard(preview.assetId.toString()) }
            populateSendersAndAmount(preview.sendersAmountMap)
            optInExpenseTextView.text = getString(R.string.you_will_receive_algo, preview.optInExpense)
        }
    }

    private fun populateSendersAndAmount(sendersAmountMap: Map<String, String>) {
        with(binding) {
            if (sendersContainer.isEmpty()) {
                sendersAmountMap.forEach {
                    val senderItem = LayoutArc59SendersAmountBinding.inflate(LayoutInflater.from(context))
                    senderItem.senderTextView.text = it.key
                    senderItem.amountTextView.text = it.value
                    sendersContainer.addView(senderItem.root)
                }
            }
        }
    }

    private fun initAccountPreview(receiver: ReceiverAccountDetailPreview) {
        with(binding) {
            val accountIconDrawable = AccountIconDrawable.create(
                context = root.context,
                accountIconDrawablePreview = receiver.accountIconDrawable,
                sizeResId = R.dimen.spacing_xlarge
            )
            val accountName = receiver.displayName.getAccountPrimaryDisplayName()
            accountTextView.apply {
                setDrawable(start = accountIconDrawable)
                text = accountName
            }
        }
    }
}
