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

package com.algorand.android.ui.send.transferpreview

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import com.algorand.android.HomeNavigationDirections
import com.algorand.android.R
import com.algorand.android.core.transaction.TransactionBaseFragment
import com.algorand.android.databinding.FragmentTransferAssetPreviewBinding
import com.algorand.android.foundation.Event
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.module.transaction.component.domain.model.SignedTransaction
import com.algorand.android.module.transaction.ui.sendasset.model.AssetTransferPreview
import com.algorand.android.module.transaction.ui.sendasset.model.AssetTransferTargetUser.Account
import com.algorand.android.module.transaction.ui.sendasset.model.AssetTransferTargetUser.Address
import com.algorand.android.module.transaction.ui.sendasset.model.AssetTransferTargetUser.ContactUser
import com.algorand.android.module.transaction.ui.sendasset.model.AssetTransferTargetUser.NfDomainUser
import com.algorand.android.ui.send.shared.AddNoteBottomSheet
import com.algorand.android.utils.Resource
import com.algorand.android.utils.extensions.changeTextAppearance
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.extensions.hide
import com.algorand.android.utils.extensions.setTextAndVisibility
import com.algorand.android.utils.extensions.show
import com.algorand.android.utils.formatAsCurrency
import com.algorand.android.utils.startSavedStateListener
import com.algorand.android.utils.toAlgoDisplayValue
import com.algorand.android.utils.useSavedStateValue
import com.algorand.android.utils.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

@AndroidEntryPoint
class AssetTransferPreviewFragment : TransactionBaseFragment(R.layout.fragment_transfer_asset_preview) {

    private val toolbarConfiguration = ToolbarConfiguration(
        startIconResId = R.drawable.ic_left_arrow,
        titleResId = R.string.confirm_transaction,
        startIconClick = ::navBack
    )

    override val fragmentConfiguration = FragmentConfiguration(toolbarConfiguration = toolbarConfiguration)

    private val assetTransferPreviewViewModel: AssetTransferPreviewViewModel by viewModels()

    private val binding by viewBinding(FragmentTransferAssetPreviewBinding::bind)

    private val assetTransferPreviewCollector: suspend (AssetTransferPreview?) -> Unit = {
        it?.let { updateUi(it) }
    }

    private var transactionNote: Pair<String?, Boolean>
        by Delegates.observable(Pair(null, false)) { _, _, (note, isNoteEnabled) ->
            with(binding) {
                if (isNoteEnabled) {
                    addEditNoteButton.show()
                    addEditNoteButton.setOnClickListener {
                        onAddEditNoteClicked()
                    }
                    if (note.isNullOrBlank()) {
                        setLayoutForAddNote()
                    } else {
                        setLayoutForEditNote(note)
                    }
                } else {
                    setLayoutForBlockedNote(note)
                }
            }
        }

    private val sendAlgoResponseCollector: suspend (Event<Resource<String>>?) -> Unit = {
        it?.consume()?.use(
            onSuccess = {
                nav(
                    AssetTransferPreviewFragmentDirections
                        .actionAssetTransferPreviewFragmentToTransactionConfirmationFragment()
                )
            },
            onFailed = { showGlobalError(it.parse(requireContext())) },
            onLoading = ::showProgress,
            onLoadingFinished = ::hideProgress
        )
    }
    override val transactionFragmentListener = object : TransactionFragmentListener {
        override fun onSignTransactionLoading() {
            showProgress()
        }

        override fun onSignTransactionLoadingFinished() {
            hideProgress()
        }

        override fun onSignTransactionFinished(signedTransaction: SignedTransaction) {
            assetTransferPreviewViewModel.sendSignedTxn(signedTransaction)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
    }

    private fun setTransactionNote(note: String?, isEditable: Boolean) {
        transactionNote = Pair(note, isEditable)
    }

    override fun onResume() {
        super.onResume()
        initSavedStateListener()
    }

    private fun initSavedStateListener() {
        // TODO use a better way to return the navigation results
        startSavedStateListener(R.id.assetTransferPreviewFragment) {
            useSavedStateValue<String>(AddNoteBottomSheet.ADD_NOTE_RESULT_KEY) {
                assetTransferPreviewViewModel.onNoteUpdate(it)
            }
        }
    }

    private fun initObservers() {
        viewLifecycleOwner.collectLatestOnLifecycle(
            assetTransferPreviewViewModel.assetTransferPreviewFlow,
            assetTransferPreviewCollector
        )
        viewLifecycleOwner.collectLatestOnLifecycle(
            assetTransferPreviewViewModel.sendAlgoResponseFlow,
            sendAlgoResponseCollector
        )
    }

    private fun onConfirmTransferClick() {
        assetTransferPreviewViewModel.createAssetTransferTransaction()
    }

    private fun updateUi(assetTransferPreview: AssetTransferPreview) {
        with(assetTransferPreview) {
            setConfirmTransferButton()
            setCurrencyViews(assetTransferPreview)
            setAssetViews(assetTransferPreview)
            setAccountViews(assetTransferPreview)
            setFee(fee)
            setTransactionNote(note, isNoteEditable)
            onSendAssetTransaction?.consume()?.let { transaction ->
                processTransaction(transaction)
            }
        }
    }

    private fun setConfirmTransferButton() {
        binding.confirmTransferButton.setOnClickListener { onConfirmTransferClick() }
    }

    private fun setCurrencyViews(assetTransferPreview: AssetTransferPreview) {
        with(assetTransferPreview) {
            with(binding) {
                if (isAlgo) {
                    algoCurrencyValueTextView.setTextAndVisibility(
                        amount.toAlgoDisplayValue().multiply(exchangePrice).formatAsCurrency(currencySymbol)
                    )
                    balanceCurrencyValueTextView.setTextAndVisibility(
                        accountBalance.toAlgoDisplayValue().multiply(exchangePrice).formatAsCurrency(currencySymbol)
                    )
                }
            }
        }
    }

    private fun setAssetViews(assetTransferPreview: AssetTransferPreview) {
        with(assetTransferPreview) {
            with(binding) {
                assetBalanceTextView.setAmount(
                    amount = accountBalance,
                    shortName = assetDetail.shortName,
                    decimals = assetDetail.decimals
                )
                assetAmountTextView.setAmount(
                    amount = amount,
                    shortName = assetDetail.shortName,
                    decimals = assetDetail.decimals
                )
                if (isAlgo) {
                    assetBalanceTextView.setTextColor(ContextCompat.getColor(root.context, R.color.tertiary_text_color))
                } else {
                    assetBalanceTextView.changeTextAppearance(R.style.TextAppearance_Body_Mono)
                }
            }
        }
    }

    private fun setAccountViews(assetTransferPreview: AssetTransferPreview) {
        with(assetTransferPreview) {
            with(binding) {
                accountUserView.setAccount(
                    name = senderAccountDisplayName.primaryDisplayName,
                    accountIconDrawablePreview = accountIconDrawablePreview,
                    publicKey = senderAccountDisplayName.accountAddress
                )
                toUserView.setOnAddButtonClickListener(::onAddButtonClicked)
                when (targetUser) {
                    is Account -> {
                        (targetUser as Account).run {
                            toUserView.setAccount(accountDisplayName, accountIconDrawablePreview, false)
                        }
                    }
                    is Address -> (targetUser as Address).run { toUserView.setAddress(address, address) }
                    is ContactUser -> toUserView.setContact((targetUser as ContactUser).value)
                    is NfDomainUser -> {
                        (targetUser as NfDomainUser).run {
                            toUserView.setNftDomainAddress(nfDomainAddress, nfDomainServiceLogoUrl)
                        }
                    }
                }
            }
        }
    }

    private fun setLayoutForAddNote() {
        with(binding) {
            noteTextView.hide()
            with(addEditNoteButton) {
                icon = ContextCompat.getDrawable(context, R.drawable.ic_plus)
                text = getString(R.string.add_note)
                updateLayoutParams<ConstraintLayout.LayoutParams> {
                    topToBottom = ConstraintLayout.LayoutParams.UNSET
                    bottomToTop = ConstraintLayout.LayoutParams.UNSET
                    topToTop = binding.noteLabelTextView.id
                    bottomToBottom = binding.noteLabelTextView.id
                    verticalBias = ADD_EDIT_NOTE_BUTTON_VERTICAL_BIAS
                }
            }

            with((addEditNoteButton.layoutParams as ViewGroup.MarginLayoutParams)) {
                setMargins(0, 0, 0, 0)
            }
            addEditNoteButton.requestLayout()
        }
    }

    private fun setLayoutForEditNote(note: String?) {
        with(binding) {
            noteTextView.text = note
            noteTextView.show()
            with(addEditNoteButton) {
                icon = ContextCompat.getDrawable(context, R.drawable.ic_pen)
                text = getString(R.string.edit_note)
                updateLayoutParams<ConstraintLayout.LayoutParams> {
                    topToTop = ConstraintLayout.LayoutParams.UNSET
                    bottomToBottom = ConstraintLayout.LayoutParams.UNSET
                    topToBottom = binding.noteTextView.id
                    bottomToTop = binding.confirmTransferButton.id
                    verticalBias = 0f
                }
            }
            val marginTop = resources.getDimensionPixelSize(R.dimen.spacing_small)
            with((addEditNoteButton.layoutParams as ViewGroup.MarginLayoutParams)) {
                setMargins(0, marginTop, 0, 0)
            }
            addEditNoteButton.requestLayout()
        }
    }

    private fun setLayoutForBlockedNote(note: String?) {
        with(binding) {
            addEditNoteButton.hide()
            if (note.isNullOrBlank()) {
                noteGroup.hide()
            } else {
                noteTextView.text = note
                noteTextView.show()
            }
        }
    }

    private fun setFee(fee: Long) {
        binding.feeAmountView.setAmountAsFee(fee)
    }

    private fun onAddButtonClicked(address: String) {
        nav(HomeNavigationDirections.actionGlobalContactAdditionNavigation(contactPublicKey = address))
    }

    private fun onAddEditNoteClicked() {
        nav(
            AssetTransferPreviewFragmentDirections
                .actionAssetTransferPreviewFragmentToAddNoteBottomSheet(
                    note = transactionNote.first,
                    isInputFieldEnabled = transactionNote.second
                )
        )
    }

    private fun showProgress() {
        binding.progressBar.root.show()
    }

    private fun hideProgress() {
        binding.progressBar.root.hide()
    }

    companion object {
        const val ADD_EDIT_NOTE_BUTTON_VERTICAL_BIAS = 0.5f
    }
}
