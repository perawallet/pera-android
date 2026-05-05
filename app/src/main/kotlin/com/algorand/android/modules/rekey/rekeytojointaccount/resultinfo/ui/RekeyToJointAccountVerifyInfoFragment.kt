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

package com.algorand.android.modules.rekey.rekeytojointaccount.resultinfo.ui

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.RekeyToJointAccountNavigationDirections
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.ui.common.BaseInfoFragment
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.button.PeraPrimaryButton
import com.algorand.android.ui.compose.widget.icon.PeraIcon
import com.algorand.android.ui.compose.widget.text.PeraBodyText
import com.algorand.android.ui.compose.widget.text.PeraHeadlineText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RekeyToJointAccountVerifyInfoFragment : BaseInfoFragment() {

    override val fragmentConfiguration: FragmentConfiguration = FragmentConfiguration()

    private val rekeyToJointAccountVerifyInfoViewModel by viewModels<RekeyToJointAccountVerifyInfoViewModel>()

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            popRekeyToJointAccountNavigationUp()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, onBackPressedCallback)
    }

    @Composable
    override fun Icon(modifier: Modifier): Unit =
        PeraIcon(
            painter = painterResource(id = R.drawable.ic_check),
            contentDescription = stringResource(id = R.string.check),
            modifier = modifier,
            tintColor = PeraTheme.colors.link.icon
        )

    @Composable
    override fun Title(modifier: Modifier): Unit =
        PeraHeadlineText(
            modifier = modifier,
            text = stringResource(id = R.string.account_successfully_rekeyed)
        )

    @Composable
    override fun Description(modifier: Modifier) {
        val state = rekeyToJointAccountVerifyInfoViewModel.state.collectAsStateWithLifecycle().value
        PeraBodyText(
            text = stringResource(
                id = R.string.the_account_name_was_successfully_rekeyed_formatted,
                state.accountDisplayName
            ),
            modifier = modifier
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun PrimaryButton(modifier: Modifier, sheetState: SheetState): Unit =
        PeraPrimaryButton(
            onClick = { popRekeyToJointAccountNavigationUp() },
            modifier = modifier,
            text = stringResource(id = R.string.done)
        )

    private fun popRekeyToJointAccountNavigationUp() {
        nav(RekeyToJointAccountNavigationDirections.actionRekeyToJointAccountNavigationPop())
    }
}
