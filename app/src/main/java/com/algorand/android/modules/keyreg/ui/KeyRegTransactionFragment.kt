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

package com.algorand.android.modules.keyreg.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import com.algorand.android.HomeNavigationDirections
import com.algorand.android.R
import com.algorand.android.core.TransactionBaseFragment
import com.algorand.android.customviews.LedgerLoadingDialog
import com.algorand.android.databinding.FragmentKeyRegTransactionBinding
import com.algorand.android.models.AnnotatedString
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.modules.keyreg.domain.KeyRegTransactionSignManager
import com.algorand.android.modules.keyreg.ui.model.KeyRegTransactionPreview
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignResult
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignResult.Error
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignResult.LedgerScanFailed
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignResult.LedgerWaitingForApproval
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignResult.Loading
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignResult.NotInitialized
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignResult.Success
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignResult.TransactionCancelled
import com.algorand.android.ui.send.shared.AddNoteBottomSheet
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.extensions.hide
import com.algorand.android.utils.extensions.show
import com.algorand.android.utils.getXmlStyledString
import com.algorand.android.utils.showAlertDialog
import com.algorand.android.utils.showWithStateCheck
import com.algorand.android.utils.startSavedStateListener
import com.algorand.android.utils.useSavedStateValue
import com.algorand.android.utils.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.properties.Delegates

@AndroidEntryPoint
class KeyRegTransactionFragment : TransactionBaseFragment(R.layout.fragment_key_reg_transaction) {

    private val toolbarConfiguration = ToolbarConfiguration(
        startIconResId = R.drawable.ic_left_arrow,
        titleResId = R.string.key_reg_transaction_title,
        startIconClick = ::navBack
    )

    override val fragmentConfiguration =
        FragmentConfiguration(toolbarConfiguration = toolbarConfiguration)

    private val keyRegTransactionViewModel by viewModels<KeyRegTransactionViewModel>()

    private val binding by viewBinding(FragmentKeyRegTransactionBinding::bind)

    private var ledgerLoadingDialog: LedgerLoadingDialog? = null

    @Inject
    lateinit var keyRegTransactionSignManager: KeyRegTransactionSignManager

    private val previewStateCollector: suspend (KeyRegTransactionPreview?) -> Unit = {
        it?.let {
            updateUi(it)
        }
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

    private val isTransactionConfirmedCollector: suspend (String?) -> Unit = { transactionId ->
        if (transactionId == KeyRegTransactionViewModel.TRANSACTION_ERROR) {
            activity?.showAlertDialog(
                getString(R.string.error),
                "Could not confirm transaction on blockchain"
            )
        } else if (transactionId != null) {
            navToConfirmationFragment(transactionId)
        }
    }

    private val externalTransactionSignManagerCollector: suspend (ExternalTransactionSignResult) -> Unit =
        {
            if (it !is Loading) hideLoader()
            when (it) {
                is Success<*> -> sendSignedTransactions(it.signedTransaction)
                is Error -> showTransactionSignResultError(it)
                LedgerScanFailed -> showLedgerNotFoundDialog()
                is LedgerWaitingForApproval -> showLedgerWaitingForApprovalBottomSheet(it)
                Loading -> showLoader()
                NotInitialized -> Unit
                is TransactionCancelled -> showTransactionCancelledError(it)
            }
        }

    private val ledgerLoadingDialogListener = LedgerLoadingDialog.Listener {
        ledgerLoadingDialog = null
        keyRegTransactionSignManager.stopAllResources()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initObservers()
        initSavedStateListener()
        keyRegTransactionSignManager.setup(viewLifecycleOwner.lifecycle)
        keyRegTransactionViewModel.initUi()
    }

    private fun initUi() {
        updateUi(null)
    }

    private fun initObservers() {
        collectLatestOnLifecycle(
            flow = keyRegTransactionViewModel.previewState,
            collection = previewStateCollector
        )
        collectLatestOnLifecycle(
            flow = keyRegTransactionSignManager.keyRegTransactionSignResultFlow,
            collection = externalTransactionSignManagerCollector
        )
        collectLatestOnLifecycle(
            flow = keyRegTransactionViewModel.confirmedTransactionIdState,
            collection = isTransactionConfirmedCollector
        )
    }

    private fun updateUi(preview: KeyRegTransactionPreview?) {
        preview?.let {
            setConfirmTransactionButton()
            setTransactionDetails(preview)
            setKeyRegDetails(preview)
            setTransactionNote(preview.xNote, preview.note, preview.xNote.isNullOrBlank())

            preview.signTransactionEvent?.consume()?.let { keyRegTxn ->
                keyRegTransactionSignManager.signKeyRegTransaction(keyRegTxn)
            }
        }
    }

    private fun initSavedStateListener() {
        startSavedStateListener(R.id.keyRegTransactionFragment) {
            useSavedStateValue<String>(AddNoteBottomSheet.ADD_NOTE_RESULT_KEY) {
                if (transactionNote.second) {
                    transactionNote = Pair(it, transactionNote.second)
                }
                keyRegTransactionViewModel.updateTransactionNotes(transactionNote.first)
            }
        }
    }

    private fun showLedgerNotFoundDialog() {
        nav(HomeNavigationDirections.actionGlobalLedgerConnectionIssueBottomSheet())
    }

    private fun showTransactionSignResultError(error: Error) {
        dismissLedgerDialog()
        context?.run {
            val (title, description) = error.getMessage(this)
            showGlobalError(description, title)
        }
    }

    private fun showTransactionCancelledError(result: TransactionCancelled) {
        dismissLedgerDialog()
        val annotatedString = (result.error as? Error.Defined)?.description
            ?: AnnotatedString(R.string.an_error_occured)
        context?.getXmlStyledString(annotatedString)?.let {
            showGlobalError(it)
        }
    }

    private fun showLedgerWaitingForApprovalBottomSheet(
        ledgerPayload: LedgerWaitingForApproval
    ) {
        if (ledgerLoadingDialog == null) {
            ledgerLoadingDialog = LedgerLoadingDialog.createLedgerLoadingDialog(
                ledgerName = ledgerPayload.ledgerName,
                listener = ledgerLoadingDialogListener,
                currentTransactionIndex = ledgerPayload.currentTransactionIndex,
                totalTransactionCount = ledgerPayload.totalTransactionCount,
                isTransactionIndicatorVisible = ledgerPayload.isTransactionIndicatorVisible
            )
            ledgerLoadingDialog?.showWithStateCheck(
                childFragmentManager,
                ledgerPayload.ledgerName.orEmpty()
            )
        } else {
            ledgerLoadingDialog?.updateTransactionIndicator(ledgerPayload.currentTransactionIndex)
        }
    }

    private fun sendSignedTransactions(signedTransaction: List<Any?>) {
        dismissLedgerDialog()
        keyRegTransactionViewModel.sendSignedTransaction(signedTransaction)
    }

    private fun navToConfirmationFragment(transactionId: String) {
        nav(
            KeyRegTransactionFragmentDirections
                .actionKeyRegTransactionFragmentToTransactionConfirmationFragment(
                    transactionId = transactionId,
                    titleResId = R.string.operation_completed,
                )
        )
    }

    private fun dismissLedgerDialog() {
        ledgerLoadingDialog?.dismissAllowingStateLoss()
        ledgerLoadingDialog = null
    }

    private fun setConfirmTransactionButton() {
        binding.confirmTransferButton.setOnClickListener {
            keyRegTransactionViewModel.confirmTransaction()
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

    private fun setTransactionDetails(preview: KeyRegTransactionPreview) {
        with(binding) {
            typeTextView.text = preview.type
            addressTextView.text = preview.address
            feeAmountView.setAmountAsFee(preview.fee.toLong())
        }
    }

    private fun setKeyRegDetails(preview: KeyRegTransactionPreview) {
        with(binding) {
            selectionKeyTextView.text = preview.selectionKey
            voteKeyTextView.text = preview.votingKey
            stateProofKeyTextView.text = preview.stateProofKey
            validFirstRoundTextView.text = preview.firstValid
            validLastRoundTextView.text = preview.lastValid
            voteKeyDilutionTextView.text = preview.keyDilution
        }
    }

    private fun setTransactionNote(xNote: String?, note: String?, isEditable: Boolean) {
        transactionNote = Pair(xNote ?: note, isEditable)
    }

    private fun onAddEditNoteClicked() {
        nav(
            KeyRegTransactionFragmentDirections
                .actionKeyRegTransactionFragmentToAddNoteNavigation(
                    note = transactionNote.first,
                    isInputFieldEnabled = transactionNote.second
                )
        )
    }

    private fun showLoader() {
        binding.progressBar.root.show()
    }

    private fun hideLoader() {
        binding.progressBar.root.hide()
    }

    companion object {
        const val ADD_EDIT_NOTE_BUTTON_VERTICAL_BIAS = 0.5f
    }
}
