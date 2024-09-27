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

package com.algorand.android.modules.swap.confirmswap.ui

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.algorand.android.HomeNavigationDirections
import com.algorand.android.R
import com.algorand.android.module.account.core.ui.model.AccountDisplayName
import com.algorand.android.module.account.core.ui.model.AccountIconDrawablePreview
import com.algorand.android.core.BaseFragment
import com.algorand.android.customviews.LedgerLoadingDialog
import com.algorand.android.customviews.SwapAssetInputView
import com.algorand.android.databinding.FragmentConfirmSwapBinding
import com.algorand.android.module.drawable.AnnotatedString
import com.algorand.android.module.drawable.getXmlStyledString
import com.algorand.android.module.foundation.Event
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.modules.swap.confirmswapconfirmation.SwapConfirmationBottomSheet.Companion.CONFIRMATION_SUCCESS_KEY
import com.algorand.android.modules.swap.ledger.signwithledger.ui.model.LedgerDialogPayload
import com.algorand.android.modules.swap.slippagetolerance.ui.SlippageToleranceBottomSheet.Companion.CHECKED_SLIPPAGE_TOLERANCE_KEY
import com.algorand.android.module.swap.component.domain.model.swapquotetxns.SwapQuoteTransaction
import com.algorand.android.module.swap.ui.assetswap.model.SwapError
import com.algorand.android.module.swap.ui.confirmswap.model.ConfirmSwapPreview
import com.algorand.android.module.swap.ui.confirmswap.model.ConfirmSwapPriceImpactWarningStatus
import com.algorand.android.module.swap.ui.confirmswap.model.SignSwapTransactionResult
import com.algorand.android.module.swap.ui.confirmswap.model.SignSwapTransactionResult.BluetoothNotEnabled
import com.algorand.android.module.swap.ui.confirmswap.model.SignSwapTransactionResult.BluetoothPermissionsAreNotGranted
import com.algorand.android.module.swap.ui.confirmswap.model.SignSwapTransactionResult.Error
import com.algorand.android.module.swap.ui.confirmswap.model.SignSwapTransactionResult.LedgerDisconnected
import com.algorand.android.module.swap.ui.confirmswap.model.SignSwapTransactionResult.LedgerOperationCancelled
import com.algorand.android.module.swap.ui.confirmswap.model.SignSwapTransactionResult.LedgerScanFailed
import com.algorand.android.module.swap.ui.confirmswap.model.SignSwapTransactionResult.LedgerWaitingForApproval
import com.algorand.android.module.swap.ui.confirmswap.model.SignSwapTransactionResult.LocationNotEnabled
import com.algorand.android.module.swap.ui.confirmswap.model.SignSwapTransactionResult.TransactionsSigned
import com.algorand.android.utils.AccountIconDrawable
import com.algorand.android.utils.BLUETOOTH_CONNECT_PERMISSION
import com.algorand.android.utils.BLUETOOTH_CONNECT_PERMISSION_REQUEST_CODE
import com.algorand.android.utils.BLUETOOTH_SCAN_PERMISSION
import com.algorand.android.utils.BLUETOOTH_SCAN_PERMISSION_REQUEST_CODE
import com.algorand.android.utils.LOCATION_PERMISSION
import com.algorand.android.utils.LOCATION_PERMISSION_REQUEST_CODE
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.extensions.hide
import com.algorand.android.utils.requestPermissionFromUser
import com.algorand.android.utils.showWithStateCheck
import com.algorand.android.utils.useFragmentResultListenerValue
import com.algorand.android.utils.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

@AndroidEntryPoint
class ConfirmSwapFragment : BaseFragment(R.layout.fragment_confirm_swap) {

    private val toolbarConfiguration = ToolbarConfiguration(
        startIconResId = R.drawable.ic_left_arrow,
        startIconClick = ::navBack,
        titleResId = R.string.confirm_swap
    )

    override val fragmentConfiguration = FragmentConfiguration(toolbarConfiguration = toolbarConfiguration)

    private val binding by viewBinding(FragmentConfirmSwapBinding::bind)

    private val confirmSwapViewModel by viewModels<ConfirmSwapViewModel>()

    private var ledgerLoadingDialog: LedgerLoadingDialog? = null

    private val bleRequestLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            confirmSwapViewModel.processWaitingTransaction()
        } else {
            permissionDeniedOnTransactionData(R.string.error_bluetooth_message, R.string.error_bluetooth_title)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
                confirmSwapViewModel.processWaitingTransaction()
            } else {
                permissionDeniedOnTransactionData(R.string.error_location_message, R.string.error_permission_title)
            }
        }
    }

    private val confirmSwapPreviewCollector: suspend (ConfirmSwapPreview?) -> Unit = { preview ->
        if (preview != null) initConfirmSwapPreview(preview)
    }

    private val isLoadingVisibleCollector: suspend (Boolean) -> Unit = { isLoading ->
        binding.progressBar.root.isVisible = isLoading
    }

    private val slippageToleranceCollector: suspend (String) -> Unit = { slippageTolerance ->
        binding.slippageToleranceTextView.text = slippageTolerance
    }

    private val minimumReceivedAmountCollector: suspend (AnnotatedString) -> Unit = { minimumReceivedAmount ->
        binding.minimumReceivedTextView.text = context?.getXmlStyledString(minimumReceivedAmount)
    }

    private val errorEventCollector: suspend (Event<SwapError>?) -> Unit = { errorEvent ->
        errorEvent?.consume()?.run {
            showGlobalError(description, title)
        }
    }

    private val updateSlippageToleranceSuccessEventCollector: suspend (Event<Unit>?) -> Unit = { updateSuccessEvent ->
        updateSuccessEvent?.consume()?.run { showAlertSuccess(getString(R.string.slippage_tolerance_value_updated)) }
    }

    private val signSwapTransactionResultCollector: suspend (Event<SignSwapTransactionResult>?) -> Unit = {
        it?.consume()?.let { result -> handleSignSwapTransactionResult(result) }
    }

    private val navigateToSwapConfirmationBottomSheetEventCollector: suspend (Event<Long>?) -> Unit = {
        it?.consume()?.let { priceImpact ->
            nav(ConfirmSwapFragmentDirections.actionConfirmSwapFragmentToSwapConfirmationBottomSheet(priceImpact))
        }
    }

    private fun showLedgerWaitingForApprovalBottomSheet(
        ledgerDialogPayload: LedgerDialogPayload
    ) {
        if (ledgerLoadingDialog == null) {
            ledgerLoadingDialog = LedgerLoadingDialog.createLedgerLoadingDialog(
                ledgerName = ledgerDialogPayload.ledgerName,
                currentTransactionIndex = ledgerDialogPayload.currentTransactionIndex,
                totalTransactionCount = ledgerDialogPayload.totalTransactionCount,
                isTransactionIndicatorVisible = ledgerDialogPayload.isTransactionIndicatorVisible,
                listener = {
                    ledgerLoadingDialog = null
                    confirmSwapViewModel.onLedgerDialogCancelled()
                }
            )
            ledgerLoadingDialog?.showWithStateCheck(childFragmentManager, ledgerDialogPayload.ledgerName.orEmpty())
        } else {
            ledgerLoadingDialog?.updateTransactionIndicator(ledgerDialogPayload.currentTransactionIndex)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initObservers()
        confirmSwapViewModel.setupSwapTransactionSignManager(viewLifecycleOwner.lifecycle)
    }

    private fun initUi() {
        with(binding) {
            confirmSwapButton.setOnClickListener { confirmSwapViewModel.onConfirmSwapClick() }
            priceRatioTextView.setOnClickListener { onSwitchPriceRatioClick() }
            slippageToleranceLabelTextView.setOnClickListener { navToSlippageToleranceInfoBottomSheet() }
            slippageToleranceTextView.setOnClickListener { onUpdateSlippageToleranceClick() }
            priceImpactLabelTextView.setOnClickListener { navToPriceImpactInfoBottomSheet() }
            exchangeFeeLabelTextView.setOnClickListener { navToExchangeFeeInfoBottomSheet() }
        }
    }

    override fun onResume() {
        super.onResume()
        useFragmentResultListenerValue<Float>(CHECKED_SLIPPAGE_TOLERANCE_KEY) { slippageTolerance ->
            confirmSwapViewModel.onSlippageToleranceUpdated(slippageTolerance)
        }
        useFragmentResultListenerValue<Boolean>(CONFIRMATION_SUCCESS_KEY) { isConfirmed ->
            confirmSwapViewModel.onSwapPriceImpactConfirmationResult(isConfirmed)
        }
    }

    private fun navToPriceImpactInfoBottomSheet() {
        nav(ConfirmSwapFragmentDirections.actionConfirmSwapFragmentToPriceImpactInfoBottomSheet())
    }

    private fun navToExchangeFeeInfoBottomSheet() {
        nav(ConfirmSwapFragmentDirections.actionConfirmSwapFragmentToExchangeFeeInfoBottomSheet())
    }

    private fun navToSlippageToleranceInfoBottomSheet() {
        nav(ConfirmSwapFragmentDirections.actionConfirmSwapFragmentToSlippageToleranceInfoBottomSheet())
    }

    private fun initObservers() {
        with(confirmSwapViewModel.confirmSwapPreviewFlow) {
            with(viewLifecycleOwner) {
                collectLatestOnLifecycle(
                    confirmSwapViewModel.confirmSwapPreviewFlow,
                    confirmSwapPreviewCollector
                )
                collectLatestOnLifecycle(
                    mapNotNull { it?.minimumReceived }.distinctUntilChanged(),
                    minimumReceivedAmountCollector
                )
                collectLatestOnLifecycle(
                    mapNotNull { it?.isLoading }.distinctUntilChanged(),
                    isLoadingVisibleCollector
                )
                collectLatestOnLifecycle(
                    mapNotNull { it?.slippageTolerance }.distinctUntilChanged(),
                    slippageToleranceCollector
                )
                collectLatestOnLifecycle(
                    mapNotNull { it?.errorEvent }.distinctUntilChanged(),
                    errorEventCollector
                )
                collectLatestOnLifecycle(
                    map { it?.slippageToleranceUpdateSuccessEvent }.distinctUntilChanged(),
                    updateSlippageToleranceSuccessEventCollector
                )
                collectLatestOnLifecycle(
                    map { it?.navToSwapConfirmationBottomSheetEvent }.distinctUntilChanged(),
                    navigateToSwapConfirmationBottomSheetEventCollector
                )
            }
        }
        viewLifecycleOwner.collectLatestOnLifecycle(
            confirmSwapViewModel.signSwapTransactionResultFlow,
            signSwapTransactionResultCollector
        )
    }

    private fun initConfirmSwapPreview(preview: ConfirmSwapPreview) {
        with(binding) {
            with(preview) {
                initAssetDetail(fromAssetInputView, fromAssetDetail)
                initAssetDetail(toAssetInputView, toAssetDetail)
                priceImpactTextView.text = formattedPriceImpact
                peraFeeTextView.text = formattedPeraFee
                exchangeFeeTextView.text = formattedExchangeFee
                priceRatioTextView.text = context?.getXmlStyledString(getPriceRatio())
                initPriceImpactWarningStatus(priceImpactWarningStatus)
                initToolbarAccountDetail(accountDisplayName, accountIconDrawablePreview)
            }
        }
    }

    private fun initPriceImpactWarningStatus(priceImpactWarningStatus: ConfirmSwapPriceImpactWarningStatus) {
        with(binding) {
            with(priceImpactWarningStatus) {
                priceImpactErrorGroup.isVisible = isPriceImpactErrorVisible
                priceImpactTextView.setTextColor(ContextCompat.getColor(root.context, priceImpactTextColorResId))
                confirmSwapButton.isEnabled = isConfirmButtonEnabled
                priceImpactLabelTextView.setTextColor(
                    ContextCompat.getColor(root.context, priceImpactLabelTextColorResId)
                )
                val errorTextAnnotatedString = getErrorText(root.context)
                priceImpactErrorTextView.apply {
                    movementMethod = priceImpactWarningStatus.movementMethod
                    if (errorTextAnnotatedString != null) {
                        text = context?.getXmlStyledString(errorTextAnnotatedString)
                    }
                }
            }
        }
    }

    private fun initToolbarAccountDetail(
        accountDisplayName: AccountDisplayName,
        accountIconDrawablePreview: AccountIconDrawablePreview
    ) {
        getAppToolbar()?.run {
            val accountIconDrawable = AccountIconDrawable.create(
                context = context,
                accountIconDrawablePreview = accountIconDrawablePreview,
                sizeResId = R.dimen.spacing_normal
            )
            setSubtitleStartDrawable(accountIconDrawable)
            changeSubtitle(accountDisplayName.primaryDisplayName)
            setOnTitleLongClickListener { onAccountAddressCopied(accountDisplayName.accountAddress) }
        }
    }

    private fun onSwitchPriceRatioClick() {
        val priceRatioAnnotatedString = confirmSwapViewModel.getSwitchedPriceRatio() ?: return
        binding.priceRatioTextView.text = context?.getXmlStyledString(priceRatioAnnotatedString)
    }

    private fun onUpdateSlippageToleranceClick() {
        nav(
            ConfirmSwapFragmentDirections
                .actionConfirmSwapFragmentToSlippageToleranceBottomSheet(confirmSwapViewModel.getSlippageTolerance())
        )
    }

    private fun initAssetDetail(assetInputView: SwapAssetInputView, assetDetail: ConfirmSwapPreview.SwapAssetDetail) {
        with(assetDetail) {
            assetInputView.apply {
                assetDrawableProvider.provideAssetDrawable(
                    imageView = getImageView(),
                    onResourceFailed = ::setImageDrawable
                )
                setAssetDetails(
                    amount = formattedAmount,
                    assetShortName = shortName,
                    verificationTierConfiguration = verificationTierConfig,
                    approximateValue = formattedApproximateValue
                )
                setAmountTextColors(
                    primaryValueTextColorResId = assetDetail.amountTextColorResId,
                    secondaryValueTextColorResId = assetDetail.approximateValueTextColorResId
                )
            }
        }
    }

    private fun handleSignSwapTransactionResult(result: SignSwapTransactionResult) {
        hideLoadings()
        when (result) {
            LedgerDisconnected -> showGlobalError(getString(R.string.an_error_occured))
            BluetoothNotEnabled -> showEnableBluetoothPopup()
            BluetoothPermissionsAreNotGranted -> requestBluetoothPermissions()
            is Error.Api -> showSignSwapTransactionResultError(result)
            is Error.Defined -> showSignSwapTransactionResultError(result)
            LedgerOperationCancelled -> showOperationCancelledError()
            LocationNotEnabled -> showLocationNotEnabledError()
            is TransactionsSigned -> navToStatusFragment(result.swapQuoteTransactions)
            LedgerScanFailed -> navToLedgerConnectionIssueBottomSheet()
            is LedgerWaitingForApproval -> {
                showLedgerWaitingForApprovalBottomSheet(LedgerDialogPayload(result.ledgerName, null, null, false))
            }
        }
    }

    private fun hideLoadings() {
        binding.progressBar.root.hide()
        ledgerLoadingDialog?.dismissAllowingStateLoss()
        ledgerLoadingDialog = null
    }

    private fun navToLedgerConnectionIssueBottomSheet() {
        nav(HomeNavigationDirections.actionGlobalLedgerConnectionIssueBottomSheet())
    }

    private fun navToStatusFragment(transactions: List<SwapQuoteTransaction>) {
        val navArgs = confirmSwapViewModel.getSwapTxnStatusNavArgs(transactions)
        nav(ConfirmSwapFragmentDirections.actionConfirmSwapFragmentToSwapTransactionStatusFragment(navArgs))
    }

    private fun showLocationNotEnabledError() {
        showGlobalError(getString(R.string.please_ensure), getString(R.string.bluetooth_location_services))
    }

    private fun showOperationCancelledError() {
        showGlobalError(getString(R.string.error_cancelled_message), getString(R.string.error_cancelled_title))
    }

    private fun showEnableBluetoothPopup() {
        Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE).apply { bleRequestLauncher.launch(this) }
    }

    private fun permissionDeniedOnTransactionData(@StringRes errorResId: Int, @StringRes titleResId: Int) {
        confirmSwapViewModel.clearCachedTransactions()
        showGlobalError(getString(errorResId), getString(titleResId))
    }

    private fun requestBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissionFromUser(BLUETOOTH_SCAN_PERMISSION, BLUETOOTH_SCAN_PERMISSION_REQUEST_CODE, true)
            requestPermissionFromUser(BLUETOOTH_CONNECT_PERMISSION, BLUETOOTH_CONNECT_PERMISSION_REQUEST_CODE, true)
        } else {
            requestPermissionFromUser(LOCATION_PERMISSION, LOCATION_PERMISSION_REQUEST_CODE, true)
        }
    }

    private fun showSignSwapTransactionResultError(error: Error) {
        context?.let {
            val (title, message) = error.getMessage(it)
            showGlobalError(message, title)
        }
    }
}
