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

package com.algorand.android.core.transaction

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.fragment.app.viewModels
import com.algorand.android.HomeNavigationDirections
import com.algorand.android.algosdk.component.transaction.model.Transaction
import com.algorand.android.core.BaseFragment
import com.algorand.android.customviews.LedgerLoadingDialog
import com.algorand.android.designsystem.AnnotatedString
import com.algorand.android.designsystem.R
import com.algorand.android.foundation.Event
import com.algorand.android.transaction.domain.model.SignedTransaction
import com.algorand.android.module.transaction.ui.core.model.SignTransactionUiResult
import com.algorand.android.module.transaction.ui.core.model.SignTransactionUiResult.BluetoothNotEnabled
import com.algorand.android.module.transaction.ui.core.model.SignTransactionUiResult.BluetoothPermissionsAreNotGranted
import com.algorand.android.module.transaction.ui.core.model.SignTransactionUiResult.Error.GlobalWarningError
import com.algorand.android.module.transaction.ui.core.model.SignTransactionUiResult.Error.SnackbarError
import com.algorand.android.module.transaction.ui.core.model.SignTransactionUiResult.LedgerDisconnected
import com.algorand.android.module.transaction.ui.core.model.SignTransactionUiResult.LedgerOperationCancelled
import com.algorand.android.module.transaction.ui.core.model.SignTransactionUiResult.LedgerScanFailed
import com.algorand.android.module.transaction.ui.core.model.SignTransactionUiResult.LedgerWaitingForApproval
import com.algorand.android.module.transaction.ui.core.model.SignTransactionUiResult.Loading
import com.algorand.android.module.transaction.ui.core.model.SignTransactionUiResult.LocationNotEnabled
import com.algorand.android.module.transaction.ui.core.model.SignTransactionUiResult.TransactionSigned
import com.algorand.android.utils.BLUETOOTH_CONNECT_PERMISSION
import com.algorand.android.utils.BLUETOOTH_CONNECT_PERMISSION_REQUEST_CODE
import com.algorand.android.utils.BLUETOOTH_SCAN_PERMISSION
import com.algorand.android.utils.BLUETOOTH_SCAN_PERMISSION_REQUEST_CODE
import com.algorand.android.utils.LOCATION_PERMISSION
import com.algorand.android.utils.LOCATION_PERMISSION_REQUEST_CODE
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.requestPermissionFromUser
import com.algorand.android.utils.showWithStateCheck

abstract class TransactionBaseFragment(@LayoutRes layoutResId: Int) : BaseFragment(layoutResId) {

    private var ledgerLoadingDialog: LedgerLoadingDialog? = null

    open val transactionFragmentListener: TransactionFragmentListener? = null

    private val transactionViewModel: TransactionViewModel by viewModels()

    private val signTransactionUiResultCollector: suspend (Event<SignTransactionUiResult>?) -> Unit = { event ->
        event?.consume()?.let { handleSignTransactionResult(it) }
    }

    private val bleRequestLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            transactionViewModel.processWaitingTransaction()
        } else {
            permissionDeniedOnTransactionData(R.string.error_bluetooth_message, R.string.error_bluetooth_title)
        }
    }

    private fun handleSignTransactionResult(result: SignTransactionUiResult) {
        when (result) {
            BluetoothNotEnabled -> showEnableBluetoothPopup()
            BluetoothPermissionsAreNotGranted -> requestBluetoothPermissions()
            LocationNotEnabled -> showLocationNotEnabledError()
            is GlobalWarningError -> onGlobalWarningError(result)
            LedgerScanFailed -> onLedgerScanFailed()
            is LedgerWaitingForApproval -> showLedgerLoading(result.ledgerName)
            Loading -> transactionFragmentListener?.onSignTransactionLoading()
            is TransactionSigned -> onTransactionSigned(result.signedTransaction)
            LedgerDisconnected -> showGlobalError(getString(com.algorand.android.R.string.an_error_occured))
            LedgerOperationCancelled -> onSignTransactionCancelledByLedger()
            is SnackbarError.Retry -> {
//                Currently, we are showing this kind of error in case of ASA  adding failure. Since we are are
//                handling this operation in [MainActivity], no need to check it here.
            }
        }
    }

    private val ledgerLoadingDialogListener = LedgerLoadingDialog.Listener { shouldStopResources ->
        hideLoading()
        if (shouldStopResources) {
            transactionViewModel.stopAllResources()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
                transactionViewModel.processWaitingTransaction()
            } else {
                permissionDeniedOnTransactionData(R.string.error_location_message, R.string.error_permission_title)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        transactionViewModel.setup(viewLifecycleOwner.lifecycle)
        viewLifecycleOwner.collectLatestOnLifecycle(
            transactionViewModel.signTransactionUiResultFlow,
            signTransactionUiResultCollector
        )
    }

    private fun onLedgerScanFailed() {
        hideLoading()
        navigateToConnectionIssueBottomSheet()
    }

    private fun onGlobalWarningError(error: GlobalWarningError) {
        showTransactionError(error)
        transactionFragmentListener?.onSignTransactionFailed()
    }

    private fun onTransactionSigned(signedTransaction: SignedTransaction) {
        hideLoading()
        transactionFragmentListener?.onSignTransactionFinished(signedTransaction)
    }

    private fun showLedgerLoading(ledgerName: String?) {
        if (ledgerLoadingDialog == null) {
            ledgerLoadingDialog = LedgerLoadingDialog.createLedgerLoadingDialog(ledgerName, ledgerLoadingDialogListener)
            ledgerLoadingDialog?.showWithStateCheck(childFragmentManager)
        }
    }

    protected fun processTransaction(transaction: Transaction) {
        transactionViewModel.processTransaction(transaction)
    }

    private fun permissionDeniedOnTransactionData(@StringRes errorResId: Int, @StringRes titleResId: Int) {
        transactionViewModel.clearCachedTransactions()
        showTransactionError(
            GlobalWarningError.Defined(
                description = AnnotatedString(errorResId),
                titleResId = titleResId
            )
        )
    }

    private fun hideLoading() {
        transactionFragmentListener?.onSignTransactionLoadingFinished()
        ledgerLoadingDialog?.dismissAllowingStateLoss()
        ledgerLoadingDialog = null
    }

    private fun navigateToConnectionIssueBottomSheet() {
        nav(HomeNavigationDirections.actionGlobalLedgerConnectionIssueBottomSheet())
    }

    protected open fun onSignTransactionCancelledByLedger() {
        val transactionManagerResult = GlobalWarningError.Defined(
            description = AnnotatedString(R.string.error_cancelled_message),
            titleResId = R.string.error_cancelled_title
        )
        showTransactionError(transactionManagerResult)
    }

    private fun showTransactionError(error: GlobalWarningError) {
        hideLoading()
        val (title, errorMessage) = error.getMessage(requireContext())
        showGlobalError(errorMessage, title)
        transactionViewModel.stopAllResources()
    }

    private fun requestBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissionFromUser(BLUETOOTH_SCAN_PERMISSION, BLUETOOTH_SCAN_PERMISSION_REQUEST_CODE, true)
            requestPermissionFromUser(BLUETOOTH_CONNECT_PERMISSION, BLUETOOTH_CONNECT_PERMISSION_REQUEST_CODE, true)
        } else {
            requestPermissionFromUser(LOCATION_PERMISSION, LOCATION_PERMISSION_REQUEST_CODE, true)
        }
    }

    private fun showLocationNotEnabledError() {
        showGlobalError(
            getString(com.algorand.android.R.string.please_ensure),
            getString(com.algorand.android.R.string.bluetooth_location_services)
        )
    }

    private fun showEnableBluetoothPopup() {
        Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE).apply { bleRequestLauncher.launch(this) }
    }

    interface TransactionFragmentListener {
        fun onSignTransactionLoadingFinished() {
            // This blank line is here to disable mandatory overriding operation
        }

        fun onSignTransactionLoading() {
            // This blank line is here to disable mandatory overriding operation
        }

        fun onSignTransactionFailed() {
            // This blank line is here to disable mandatory overriding operation
        }

        fun onSignTransactionFinished(signedTransaction: SignedTransaction)
    }
}
