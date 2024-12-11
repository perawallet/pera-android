package com.algorand.android.modules.keyreg.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.algorand.android.R
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.modules.basesingleaccountselection.ui.BaseSingleAccountSelectionFragment
import com.algorand.android.modules.basesingleaccountselection.ui.BaseSingleAccountSelectionViewModel
import com.algorand.android.modules.basesingleaccountselection.ui.model.SingleAccountSelectionListItem
import com.algorand.android.modules.keyreg.ui.KeyRegTransactionFragment.Companion.NAV_BACK_FRAGMENT_KEY
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.showAlertDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class KeyRegAccountSelectionFragment : BaseSingleAccountSelectionFragment() {

    override val toolbarConfiguration = ToolbarConfiguration(
        startIconResId = R.drawable.ic_left_arrow,
        startIconClick = ::navBack
    )
    override val fragmentConfiguration = FragmentConfiguration(toolbarConfiguration = toolbarConfiguration)

    override val singleAccountSelectionViewModel: BaseSingleAccountSelectionViewModel
        get() = keyRegAccountSelectionViewModel
    private val keyRegAccountSelectionViewModel by viewModels<KeyRegAccountSelectionViewModel>()

    override fun onAccountSelected(accountAddress: String) {
        nav(
            KeyRegAccountSelectionFragmentDirections
                .actionKeyRegAccountSelectionFragmentToKeyRegTransactionFragment(
                    keyRegTransactionDetail = keyRegAccountSelectionViewModel.keyRegTransactionDetail,
                    signingAccountAddress = accountAddress
                )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<String>(NAV_BACK_FRAGMENT_KEY)?.observe(viewLifecycleOwner) { result ->
                if (result == "KeyRegTransactionFragment") {
                    navBack() // goes back to account selection screen
                    navBack() // goes back to camera screen
                }
            }
        findNavController().previousBackStackEntry?.savedStateHandle?.set(NAV_BACK_FRAGMENT_KEY, null)
        initObservers()
    }

    private fun initObservers() {
        with(singleAccountSelectionViewModel.singleAccountSelectionFieldsFlow) {
            collectLatestOnLifecycle(
                flow = map { it?.singleAccountSelectionListItems },
                collection = singleAccountSelectionListItemsCollectorKeyReg
            )
        }
    }

    val singleAccountSelectionListItemsCollectorKeyReg: suspend (
        List<SingleAccountSelectionListItem>?
    ) -> Unit = { accountSelectionItemList ->
        var addressFound = false
        accountSelectionItemList?.forEach { accountSelectionItem ->
            accountSelectionItem.itemType
            if (accountSelectionItem.itemType == SingleAccountSelectionListItem.ItemType.ACCOUNT_ITEM) {
                if (accountSelectionItem.address == keyRegAccountSelectionViewModel.keyRegTransactionDetail.address) {
                    addressFound = true
                    onAccountSelected(accountSelectionItem.address.toString())
                }
            }
        }
        if (!accountSelectionItemList.isNullOrEmpty() && !addressFound) {
            activity?.showAlertDialog(
                getString(R.string.error),
                "Please add wallet address (" +
                        "${keyRegAccountSelectionViewModel.keyRegTransactionDetail.address}) on app before scanning"
            )
            navBack()
        }
    }
}
