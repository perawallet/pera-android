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
 *
 */

package com.algorand.android.ui.common.walletconnect

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import com.algorand.android.R
import com.algorand.android.accountcore.ui.model.AccountIconDrawablePreview
import com.algorand.android.databinding.CustomWalletConnectSenderViewBinding
import com.algorand.android.models.ApplicationCallStateSchema
import com.algorand.android.models.BaseAppCallTransaction
import com.algorand.android.models.BaseWalletConnectDisplayedAddress
import com.algorand.android.models.TransactionRequestAssetInformation
import com.algorand.android.models.TransactionRequestSenderInfo
import com.algorand.android.utils.AccountIconDrawable
import com.algorand.android.utils.extensions.show
import com.algorand.android.utils.setAssetNameTextColorByVerificationTier
import com.algorand.android.utils.setDrawable
import com.algorand.android.utils.viewbinding.viewBinding

class WalletConnectSenderCardView(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private val binding = viewBinding(CustomWalletConnectSenderViewBinding::inflate)

    private var listener: WalletConnectSenderCardViewListener? = null

    init {
        initRootLayout()
    }

    fun setListener(listener: WalletConnectSenderCardViewListener) {
        this.listener = listener
    }

    fun initSender(senderInfo: TransactionRequestSenderInfo?) {
        if (senderInfo == null) return
        with(senderInfo) {
            initSenderAddress(fromDisplayedAddress, fromAccountIconDrawablePreview)
            initOnComplete(onCompletion)
            initRekeyToAddress(rekeyToAccountAddress, warningCount)
            initApplicationId(appId)
            initAppGlobalSchema(appGlobalScheme)
            initAppLocalSchema(appLocalScheme)
            initAppExtraPages(appExtraPages)
            initApprovalHash(approvalHash)
            initClearStateHash(clearStateHash)
            initAssetInformation(assetInformation, toDisplayedAddress?.fullAddress)
            initToAccount(toDisplayedAddress, toAccountIconDrawablePreview)
        }
    }

    private fun initSenderAddress(
        address: BaseWalletConnectDisplayedAddress?,
        accountIconDrawablePreview: AccountIconDrawablePreview?
    ) {
        address?.let { address ->
            binding.senderNameTextView.apply {
                text = address.displayValue
                setOnLongClickListener { listener?.onAccountAddressLongPressed(address.fullAddress); true }
            }
        }
        accountIconDrawablePreview?.let {
            val accountIconDrawable = AccountIconDrawable.create(
                context = context,
                accountIconDrawablePreview = accountIconDrawablePreview,
                sizeResId = R.dimen.spacing_xlarge
            )
            binding.fromAccountTypeImageView.apply {
                setImageDrawable(accountIconDrawable)
                show()
            }
        }
    }

    private fun initAppGlobalSchema(appGlobalSchema: ApplicationCallStateSchema?) {
        appGlobalSchema?.let { schema ->
            if (schema.numberOfInts == null || schema.numberOfBytes == null) return
            with(binding) {
                appGlobalSchemaTextView.text = context.getString(
                    R.string.byte_uint_formatted,
                    schema.numberOfBytes,
                    schema.numberOfInts
                )
                appGlobalSchemaGroup.show()
            }
        }
    }

    private fun initAppLocalSchema(appLocalSchema: ApplicationCallStateSchema?) {
        appLocalSchema?.let { schema ->
            if (schema.numberOfInts == null || schema.numberOfBytes == null) return
            with(binding) {
                appLocalSchemaTextView.text = context.getString(
                    R.string.byte_uint_formatted,
                    schema.numberOfBytes,
                    schema.numberOfInts
                )
                appLocalSchemaGroup.show()
            }
        }
    }

    private fun initAppExtraPages(appExtraPages: Int?) {
        appExtraPages?.let { extraPages ->
            with(binding) {
                appExtraPagesTextView.text = extraPages.toString()
                appExtraPagesGroup.show()
            }
        }
    }

    private fun initApprovalHash(approvalHash: String?) {
        approvalHash?.let { hash ->
            if (hash.isBlank()) return
            with(binding) {
                approvalHashTextView.text = hash
                approvalHashGroup.show()
            }
        }
    }

    private fun initClearStateHash(clearStateHash: String?) {
        clearStateHash?.let { hash ->
            if (hash.isBlank()) return
            with(binding) {
                clearStateHashTextView.text = hash
                clearStateHashGroup.show()
            }
        }
    }

    private fun initOnComplete(onComplete: BaseAppCallTransaction.AppOnComplete?) {
        onComplete?.let {
            with(binding) {
                onCompleteTextView.text = root.context.getText(onComplete.displayTextResId)
                onCompleteGroup.show()
            }
        }
    }

    private fun initApplicationId(appId: Long?) {
        if (appId == null) return
        with(binding) {
            val appIdWithHashTag = "#$appId"
            applicationIdTextView.text = appIdWithHashTag
            applicationIdGroup.show()
        }
    }

    private fun initRekeyToAddress(address: String?, warningCount: Int?) {
        if (!address.isNullOrBlank()) {
            with(binding) {
                rekeyToTextView.text = address
                rekeyToTextView.setOnLongClickListener {
                    listener?.onAccountAddressLongPressed(address)
                    return@setOnLongClickListener true
                }
                rekeyGroup.show()
                rekeyToWarningTextView.isVisible = warningCount != null
            }
        }
    }

    private fun initAssetInformation(
        assetInformation: TransactionRequestAssetInformation?,
        accountAddress: String?
    ) {
        assetInformation?.let {
            with(binding) {
                it.verificationTierConfiguration.drawableResId?.run {
                    assetNameTextView.setDrawable(start = AppCompatResources.getDrawable(context, this))
                }
                assetNameTextView.apply {
                    text = assetInformation.shortName
                    setAssetNameTextColorByVerificationTier(it.verificationTierConfiguration)
                    setOnClickListener {
                        listener?.onAssetItemClick(assetId = assetInformation.assetId, accountAddress = accountAddress)
                    }
                }
                assetIdTextView.apply {
                    text = assetInformation.assetId.toString()
                    setOnClickListener {
                        listener?.onAssetItemClick(assetId = assetInformation.assetId, accountAddress = accountAddress)
                    }
                }
                assetGroup.show()
            }
        }
    }

    private fun initToAccount(
        toDisplayedAddress: BaseWalletConnectDisplayedAddress?,
        accountIconDrawablePreview: AccountIconDrawablePreview?
    ) {
        toDisplayedAddress?.let {
            with(binding) {
                toNameTextView.text = toDisplayedAddress.displayValue
                toNameTextView.isSingleLine = toDisplayedAddress.isSingleLine
                toNameTextView.setOnLongClickListener {
                    listener?.onAccountAddressLongPressed(toDisplayedAddress.fullAddress)
                    return@setOnLongClickListener true
                }
                toGroup.show()
            }
        }

        accountIconDrawablePreview?.let {
            val accountIconDrawable = AccountIconDrawable.create(
                context = context,
                accountIconDrawablePreview = accountIconDrawablePreview,
                sizeResId = R.dimen.spacing_xlarge
            )
            binding.toAccountTypeImageView.apply {
                setImageDrawable(accountIconDrawable)
                show()
            }
        }
    }

    private fun initRootLayout() {
        setPadding(resources.getDimensionPixelSize(R.dimen.spacing_xlarge))
    }

    interface WalletConnectSenderCardViewListener {
        fun onAccountAddressLongPressed(fullAddress: String)
        fun onAssetItemClick(assetId: Long?, accountAddress: String?)
    }
}
