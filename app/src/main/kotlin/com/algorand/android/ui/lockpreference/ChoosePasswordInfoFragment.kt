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

package com.algorand.android.ui.lockpreference

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.algorand.android.R
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.customviews.toolbar.buttoncontainer.model.TextButton
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.ui.common.BaseInfoFragment
import com.algorand.android.ui.compose.widget.PeraDescriptionText
import com.algorand.android.ui.compose.widget.PeraPrimaryButton
import com.algorand.android.ui.compose.widget.PeraSecondaryButton
import com.algorand.android.ui.compose.widget.PeraTitleText
import com.algorand.android.utils.preference.setLockDontAskAgain
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChoosePasswordInfoFragment : BaseInfoFragment() {

    @Inject
    lateinit var sharedPref: SharedPreferences

    private val toolbarConfiguration =
        ToolbarConfiguration(backgroundColor = R.color.tertiary_background)

    override val fragmentConfiguration =
        FragmentConfiguration(toolbarConfiguration = toolbarConfiguration)

    private val args by navArgs<ChoosePasswordInfoFragmentArgs>()

    private val choosePasswordInfoViewModel: ChoosePasswordInfoViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        navigateToHomeNavigationOnBackPressed()
    }

    private fun setupToolbar() {
        getAppToolbar()?.setEndButton(
            button = TextButton(
                R.string.do_not_ask_again,
                onClick = ::onDontAskAgainClick
            )
        )
    }

    @Composable
    override fun Icon(modifier: Modifier) =
        Image(
            painter = painterResource(id = R.drawable.ic_locked),
            colorFilter = ColorFilter.tint(color = colorResource(R.color.info_image_color)),
            contentDescription = "check large",
            modifier = modifier
        )

    @Composable
    override fun Title(modifier: Modifier) =
        PeraTitleText(
            modifier = modifier,
            text = stringResource(id = R.string.increase_your_security)
        )

    @Composable
    override fun Description(modifier: Modifier) =
        PeraDescriptionText(
            text = stringResource(id = R.string.this_6_digit_pin),
            modifier = modifier
        )

    @Composable
    override fun PrimaryButton(modifier: Modifier) =
        PeraPrimaryButton(
            onClick = { navigateToChoosePasswordFragment() },
            modifier = modifier,
            text = stringResource(id = R.string.set_pin_code)
        )

    @Composable
    override fun SecondaryButton(modifier: Modifier) =
        PeraSecondaryButton(
            onClick = { onCancelClick() },
            modifier = modifier,
            text = stringResource(id = R.string.not_now)
        )

    private fun onDontAskAgainClick() {
        sharedPref.setLockDontAskAgain()
        onCancelClick()
    }

    private fun navigateToChoosePasswordFragment() {
        choosePasswordInfoViewModel.logOnboardingSetPinCodeClickEvent()
        nav(
            ChoosePasswordInfoFragmentDirections.actionChoosePasswordInfoFragmentToChoosePasswordFragment(
                args.shouldNavigateHome
            )
        )
    }

    private fun onCancelClick() {
        if (args.shouldNavigateHome) {
            nav(ChoosePasswordInfoFragmentDirections.actionChoosePasswordInfoFragmentToHomeNavigation())
        } else {
            navBack()
        }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (args.shouldNavigateHome) {
                nav(ChoosePasswordInfoFragmentDirections.actionChoosePasswordInfoFragmentToHomeNavigation())
            }
        }
    }

    private fun navigateToHomeNavigationOnBackPressed() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, onBackPressedCallback)
    }
}
