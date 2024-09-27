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

// TODO: We should remove this after function count decrease under 25
@file:Suppress("TooManyFunctions", "MaxLineLength")
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

package com.algorand.android.ui.wctransactionrequest

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import com.algorand.android.HomeNavigationDirections
import com.algorand.android.MainNavigationDirections
import com.algorand.android.R
import com.algorand.android.WalletConnectTransactionRequestNavigationDirections
import com.algorand.android.core.DaggerBaseFragment
import com.algorand.android.customviews.LedgerLoadingDialog
import com.algorand.android.databinding.FragmentWalletConnectTransactionRequestBinding
import com.algorand.android.module.foundation.Event
import com.algorand.android.models.AnnotatedString
import com.algorand.android.models.ConfirmationBottomSheetParameters
import com.algorand.android.models.ConfirmationBottomSheetResult
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.TransactionRequestAction
import com.algorand.android.models.WalletConnectRequest.WalletConnectTransaction
import com.algorand.android.modulenew.walletconnect.SignWalletConnectTransactionResult
import com.algorand.android.modulenew.walletconnect.SignWalletConnectTransactionResult.BluetoothNotEnabled
import com.algorand.android.modulenew.walletconnect.SignWalletConnectTransactionResult.BluetoothPermissionsAreNotGranted
import com.algorand.android.modulenew.walletconnect.SignWalletConnectTransactionResult.Error
import com.algorand.android.modulenew.walletconnect.SignWalletConnectTransactionResult.LedgerDisconnected
import com.algorand.android.modulenew.walletconnect.SignWalletConnectTransactionResult.LedgerOperationCancelled
import com.algorand.android.modulenew.walletconnect.SignWalletConnectTransactionResult.LedgerScanFailed
import com.algorand.android.modulenew.walletconnect.SignWalletConnectTransactionResult.LedgerWaitingForApproval
import com.algorand.android.modulenew.walletconnect.SignWalletConnectTransactionResult.LocationNotEnabled
import com.algorand.android.modulenew.walletconnect.SignWalletConnectTransactionResult.TransactionsSigned
import com.algorand.android.modules.walletconnect.ui.model.WalletConnectSessionIdentifier
import com.algorand.android.ui.common.walletconnect.WalletConnectAppPreviewCardView
import com.algorand.android.utils.BLUETOOTH_CONNECT_PERMISSION
import com.algorand.android.utils.BLUETOOTH_CONNECT_PERMISSION_REQUEST_CODE
import com.algorand.android.utils.BLUETOOTH_SCAN_PERMISSION
import com.algorand.android.utils.BLUETOOTH_SCAN_PERMISSION_REQUEST_CODE
import com.algorand.android.utils.BaseDoubleButtonBottomSheet.Companion.RESULT_KEY
import com.algorand.android.utils.LOCATION_PERMISSION
import com.algorand.android.utils.LOCATION_PERMISSION_REQUEST_CODE
import com.algorand.android.utils.Resource
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.extensions.hide
import com.algorand.android.utils.extensions.show
import com.algorand.android.utils.navigateSafe
import com.algorand.android.utils.requestPermissionFromUser
import com.algorand.android.utils.showWithStateCheck
import com.algorand.android.utils.startSavedStateListener
import com.algorand.android.utils.useSavedStateValue
import com.algorand.android.utils.viewbinding.viewBinding
import com.algorand.android.utils.walletconnect.isFutureTransaction
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class WalletConnectTransactionRequestFragment :
    DaggerBaseFragment(R.layout.fragment_wallet_connect_transaction_request), TransactionRequestAction {

    override val fragmentConfiguration = FragmentConfiguration()

    private val binding by viewBinding(FragmentWalletConnectTransactionRequestBinding::bind)
    private val transactionRequestViewModel: WalletConnectTransactionRequestViewModel by viewModels()

    private val signTxnResultObserver: suspend (Event<SignWalletConnectTransactionResult>?) -> Unit = { result ->
        result?.consume()?.let { handleSignResult(it) }
    }

    private lateinit var walletConnectNavController: NavController

    private var ledgerLoadingDialog: LedgerLoadingDialog? = null

    private var walletConnectTransaction: WalletConnectTransaction? = null

    private val bleRequestLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            transactionRequestViewModel.confirmRequest()
        } else {
            showSigningError(getString(R.string.error_bluetooth_message), getString(R.string.error_bluetooth_title))
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
                transactionRequestViewModel.processWaitingTransaction()
            } else {
                showSigningError(getString(R.string.error_location_message), getString(R.string.error_permission_title))
            }
        }
    }

    private val requestResultObserver = Observer<Event<Resource<AnnotatedString>>> {
        it.consume()?.use(
            onSuccess = { transactionRequestViewModel.onTransactionConfirmed() },
            onFailed = { navBack() }
        )
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (!walletConnectNavController.navigateUp()) {
                rejectRequest()
            }
        }
    }

    private val appPreviewListener = WalletConnectAppPreviewCardView.OnShowMoreClickListener { peerMeta, message ->
        nav(
            WalletConnectTransactionRequestNavigationDirections.actionGlobalWalletConnectDappMessageBottomSheet(
                message = message,
                peerMeta = peerMeta
            )
        )
    }

    private val ledgerLoadingDialogListener = LedgerLoadingDialog.Listener { shouldStopResources ->
        hideLoading()
        if (shouldStopResources) {
            transactionRequestViewModel.stopAllResources()
        }
    }

    private val navToLaunchBackNavigationEventCollector: suspend (
        Event<WalletConnectSessionIdentifier>?
    ) -> Unit = { event ->
        event?.consume()?.run {
            nav(
                WalletConnectTransactionRequestFragmentDirections
                    .actionWalletConnectTransactionRequestFragmentToWcRequestLaunchBackNavigation(
                        this,
                        transactionRequestViewModel.transaction
                    )
            )
        }
    }

    private val navBackEventCollector: suspend (Event<Unit>?) -> Unit = { event ->
        event?.consume()?.let { navBack() }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        walletConnectTransaction = transactionRequestViewModel.transaction
        initNavController()
        handleNextNavigation()
        configureScreenStateByDestination()
        initObservers()
        initUi()
        transactionRequestViewModel.setup(viewLifecycleOwner.lifecycle)
    }

    private fun initNavController() {
        walletConnectNavController = (
            childFragmentManager.findFragmentById(binding.walletConnectNavigationHostFragment.id) as NavHostFragment
            ).navController
    }

    private fun handleNextNavigation() {
        val transactionListItem = transactionRequestViewModel.createTransactionListItems(
            walletConnectTransaction?.transactionList.orEmpty()
        )
        val (startDestinationId, startDestinationArgs) = transactionRequestViewModel
            .handleStartDestinationAndArgs(transactionListItem)

        with(walletConnectNavController) {
            setGraph(
                navInflater.inflate(R.navigation.transaction_request_navigation).apply {
                    setStartDestination(startDestinationId)
                },
                startDestinationArgs
            )
        }
    }

    private fun configureScreenStateByDestination() {
        val motionTransaction = binding.transactionRequestMotionLayout.getTransition(R.id.wcRequestMotionScene)
        walletConnectNavController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.walletConnectSingleTransactionFragment -> {
                    binding.transactionRequestMotionLayout.transitionToStart()
                    motionTransaction.isEnabled = false
                }

                else -> {
                    motionTransaction.isEnabled = true
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startSavedStateListener(R.id.walletConnectTransactionRequestFragment) {
            useSavedStateValue<ConfirmationBottomSheetResult>(RESULT_KEY) { result ->
                if (result.isAccepted) confirmTransaction()
            }
        }
    }

    private fun initObservers() {
        collectLatestOnLifecycle(
            flow = transactionRequestViewModel.signWcTransactionResultFlow,
            collection = signTxnResultObserver
        )
        with(transactionRequestViewModel) {
            requestResultLiveData.observe(viewLifecycleOwner, requestResultObserver)
            with(walletConnectTransactionRequestPreviewFlow) {
                collectLatestOnLifecycle(
                    flow = map { it.navBackEvent },
                    collection = navBackEventCollector
                )
                collectLatestOnLifecycle(
                    flow = map { it.navToLaunchBackNavigationEvent },
                    collection = navToLaunchBackNavigationEventCollector
                )
            }
        }
    }

    private fun initUi() {
        val transaction = transactionRequestViewModel.transaction
        with(binding) {
            declineButton.setOnClickListener { rejectRequest() }
            confirmButton.apply {
                setOnClickListener { onConfirmClick() }
                val transactionCount = walletConnectTransaction?.transactionList?.size ?: 0
                text = resources.getQuantityString(R.plurals.confirm_transactions, transactionCount)
            }
        }
        walletConnectTransaction = transaction
        initAppPreview()
        rejectRequestOnBackPressed()
        checkIfShouldShowFirstRequestBottomSheet()
    }

    private fun confirmTransaction() {
        showLoading()
        transactionRequestViewModel.confirmRequest()
    }

    private fun onSigningSuccess(result: TransactionsSigned) {
        transactionRequestViewModel.processWalletConnectSignResult(result)
    }

    private fun showLedgerWaitingForApprovalBottomSheet(
        ledgerName: String?,
        currentTransactionIndex: Int?,
        totalTransactionCount: Int?,
        isTransactionIndicatorVisible: Boolean
    ) {
        if (ledgerLoadingDialog == null) {
            ledgerLoadingDialog = LedgerLoadingDialog.createLedgerLoadingDialog(
                ledgerName = ledgerName,
                listener = ledgerLoadingDialogListener,
                currentTransactionIndex = currentTransactionIndex,
                totalTransactionCount = totalTransactionCount,
                isTransactionIndicatorVisible = isTransactionIndicatorVisible
            )
            ledgerLoadingDialog?.showWithStateCheck(childFragmentManager, ledgerName.orEmpty())
        } else {
            ledgerLoadingDialog?.updateTransactionIndicator(transactionIndex = currentTransactionIndex)
        }
    }

    private fun onConfirmClick() {
        val currentTransaction = transactionRequestViewModel.transaction ?: return
        if (currentTransaction.isFutureTransaction()) {
            showFutureTransactionConfirmationBottomSheet(currentTransaction.requestId)
        } else {
            confirmTransaction()
        }
    }

    private fun showFutureTransactionConfirmationBottomSheet(requestId: Long) {
        val confirmationParams = ConfirmationBottomSheetParameters(
            titleResId = R.string.future_transaction_detected,
            descriptionText = getString(R.string.this_transaction_will_be),
            confirmationIdentifier = requestId
        )
        nav(MainNavigationDirections.actionGlobalConfirmationBottomSheet(confirmationParams))
    }

    private fun checkIfShouldShowFirstRequestBottomSheet() {
        if (transactionRequestViewModel.shouldShowFirstRequestBottomSheet()) {
            showFirstRequestBottomSheet()
        }
    }

    private fun showFirstRequestBottomSheet() {
        val navDirection = MainNavigationDirections.actionGlobalSingleButtonBottomSheet(
            titleAnnotatedString = AnnotatedString(R.string.transaction_request_faq),
            drawableResId = R.drawable.ic_info,
            drawableTintResId = R.color.info_tint_color,
            descriptionAnnotatedString = AnnotatedString(R.string.external_applications_also)
        )
        nav(navDirection)
    }

    private fun handleSignResult(result: SignWalletConnectTransactionResult) {
        binding.progressBar.root.hide()
        when (result) {
            is TransactionsSigned -> onSigningSuccess(result)
            is Error -> result.getMessage(requireContext()).run { showSigningError(first, second) }
            is LedgerWaitingForApproval -> {
                showLedgerWaitingForApprovalBottomSheet(
                    ledgerName = result.ledgerName,
                    currentTransactionIndex = null, // TODO
                    totalTransactionCount = null, // TODO
                    isTransactionIndicatorVisible = false // TODO
                )
            }
            LedgerScanFailed -> showLedgerNotFoundDialog()
            BluetoothNotEnabled -> showEnableBluetoothPopup()
            BluetoothPermissionsAreNotGranted -> requestBluetoothPermissions()
            LocationNotEnabled -> showLocationNotEnabledError()
            LedgerDisconnected -> showGlobalError(getString(R.string.an_error_occured))
            LedgerOperationCancelled -> handleLedgerCancellation()
        }
    }

    private fun showEnableBluetoothPopup() {
        Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE).apply { bleRequestLauncher.launch(this) }
    }

    private fun rejectRequestOnBackPressed() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, onBackPressedCallback)
    }

    private fun rejectRequest() {
        transactionRequestViewModel.rejectRequest()
    }

    private fun showLocationNotEnabledError() {
        showGlobalError(getString(R.string.please_ensure), getString(R.string.bluetooth_location_services))
    }

    private fun requestBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissionFromUser(BLUETOOTH_SCAN_PERMISSION, BLUETOOTH_SCAN_PERMISSION_REQUEST_CODE, true)
            requestPermissionFromUser(BLUETOOTH_CONNECT_PERMISSION, BLUETOOTH_CONNECT_PERMISSION_REQUEST_CODE, true)
        } else {
            requestPermissionFromUser(LOCATION_PERMISSION, LOCATION_PERMISSION_REQUEST_CODE, true)
        }
    }

    private fun handleLedgerCancellation() {
        val errorMessage = getString(R.string.error_cancelled_message)
        val title = getString(R.string.error_cancelled_title)
        showSigningError(title, errorMessage)
        rejectRequest()
    }

    private fun initAppPreview() {
        walletConnectTransaction?.run {
            binding.dAppPreviewView.initPeerMeta(session.peerMeta, message, appPreviewListener)
        }
    }

    private fun showLedgerNotFoundDialog() {
        hideLoading()
        navigateToConnectionIssueBottomSheet()
    }

    private fun navigateToConnectionIssueBottomSheet() {
        nav(HomeNavigationDirections.actionGlobalLedgerConnectionIssueBottomSheet())
    }

    private fun hideLoading() {
        binding.progressBar.root.hide()
        ledgerLoadingDialog?.dismissAllowingStateLoss()
        ledgerLoadingDialog = null
    }

    private fun showLoading() {
        binding.progressBar.root.show()
    }

    private fun showSigningError(title: String, errorMessage: CharSequence) {
        hideLoading()
        showGlobalError(errorMessage = errorMessage, title = title, tag = baseActivityTag)
    }

    override fun onNavigate(navDirections: NavDirections) {
        walletConnectNavController.navigateSafe(navDirections)
    }

    override fun onNavigateBack() {
        walletConnectNavController.navigateUp()
    }

    override fun showButtons() {
        with(binding) {
            confirmButton.show()
            declineButton.show()
        }
    }

    override fun hideButtons() {
        with(binding) {
            confirmButton.hide()
            declineButton.hide()
        }
    }

    override fun motionTransitionToEnd() {
        binding.transactionRequestMotionLayout.transitionToEnd()
    }
}
