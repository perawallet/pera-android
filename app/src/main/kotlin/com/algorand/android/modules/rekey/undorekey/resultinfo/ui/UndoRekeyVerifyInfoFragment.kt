/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.rekey.undorekey.resultinfo.ui

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.viewModels
import com.algorand.android.R
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.ui.common.BaseInfoFragment
import com.algorand.android.ui.compose.widget.text.PeraBodyText
import com.algorand.android.ui.compose.widget.text.PeraHeadlineText
import com.algorand.android.ui.compose.widget.icon.PeraIcon
import com.algorand.android.ui.compose.widget.button.PeraPrimaryButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UndoRekeyVerifyInfoFragment : BaseInfoFragment() {

    override val fragmentConfiguration = FragmentConfiguration()

    private val undoRekeyVerifyInfoViewModel by viewModels<UndoRekeyVerifyInfoViewModel>()

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            popUndoRekeyNavigationUp()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, onBackPressedCallback)
    }

    @Composable
    override fun Icon(modifier: Modifier) =
        PeraIcon(
            painter = painterResource(id = R.drawable.ic_check),
            contentDescription = stringResource(id = R.string.check),
            modifier = modifier
        )

    @Composable
    override fun Title(modifier: Modifier) =
        PeraHeadlineText(
            modifier = modifier,
            text = stringResource(id = R.string.rekey_successfully_undone)
        )

    @Composable
    override fun Description(modifier: Modifier) {
        val accountDisplayName by undoRekeyVerifyInfoViewModel.accountDisplayName.collectAsState()

        PeraBodyText(
            text = stringResource(
                id = R.string.the_account_has_been_reverted,
                accountDisplayName
            ),
            modifier = modifier
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun PrimaryButton(modifier: Modifier, sheetState: SheetState) =
        PeraPrimaryButton(
            onClick = { popUndoRekeyNavigationUp() },
            modifier = modifier,
            text = stringResource(id = R.string.done)
        )

    private fun popUndoRekeyNavigationUp() {
        nav(UndoRekeyVerifyInfoFragmentDirections.actionRekeyUndoNavigationPop())
    }
}
