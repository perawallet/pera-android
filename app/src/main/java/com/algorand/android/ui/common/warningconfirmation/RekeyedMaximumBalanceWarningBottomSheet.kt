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

package com.algorand.android.ui.common.warningconfirmation

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.algorand.android.R
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.formatAsAlgoString
import com.algorand.android.utils.getXmlStyledString
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigInteger

@AndroidEntryPoint
class RekeyedMaximumBalanceWarningBottomSheet : BaseMaximumBalanceWarningBottomSheet() {

    private val args: RekeyedMaximumBalanceWarningBottomSheetArgs by navArgs()

    private val minBalanceCollector: suspend (BigInteger?) -> Unit = { minBalance ->
        if (minBalance != null) {
            setDescriptionText(getDescriptionText(minBalance))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.collectLatestOnLifecycle(
            maximumBalanceWarningViewModel.minRequiredBalanceFlow,
            minBalanceCollector
        )
        maximumBalanceWarningViewModel.getMinimumBalance(args.publicKey)
    }

    private fun getDescriptionText(minRequiredBalance: BigInteger): CharSequence {
        return requireContext().getXmlStyledString(
            R.string.rekeyed_maximum_error_message,
            replacementList = listOf(
                "min_balance" to minRequiredBalance.formatAsAlgoString()
            )
        )
    }
}
