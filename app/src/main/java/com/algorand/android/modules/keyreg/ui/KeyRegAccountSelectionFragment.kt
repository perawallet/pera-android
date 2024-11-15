package com.algorand.android.modules.keyreg.ui

import androidx.fragment.app.viewModels
import com.algorand.android.R
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.modules.basesingleaccountselection.ui.BaseSingleAccountSelectionFragment
import com.algorand.android.modules.basesingleaccountselection.ui.BaseSingleAccountSelectionViewModel
import dagger.hilt.android.AndroidEntryPoint

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
}
