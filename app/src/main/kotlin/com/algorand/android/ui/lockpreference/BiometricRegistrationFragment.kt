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

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.viewModels
import com.algorand.android.R
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.ui.common.BaseInfoFragment
import com.algorand.android.ui.compose.widget.PeraDescriptionText
import com.algorand.android.ui.compose.widget.PeraPrimaryButton
import com.algorand.android.ui.compose.widget.PeraSecondaryButton
import com.algorand.android.ui.compose.widget.PeraTitleText
import com.algorand.android.utils.alertDialog
import com.algorand.android.utils.showBiometricAuthentication
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BiometricRegistrationFragment : BaseInfoFragment() {

    private val toolbarConfiguration = ToolbarConfiguration(
        startIconResId = R.drawable.ic_left_arrow,
        startIconClick = ::navBack
    )

    override val fragmentConfiguration =
        FragmentConfiguration(toolbarConfiguration = toolbarConfiguration)

    private val biometricRegistrationViewModel: BiometricRegistrationViewModel by viewModels()

    @Composable
    override fun Icon(modifier: Modifier) =
        Image(
            painter = painterResource(id = R.drawable.ic_faceid),
            colorFilter = ColorFilter.tint(color = colorResource(R.color.info_image_color)),
            contentDescription = "face id",
            modifier = modifier
        )

    @Composable
    override fun Title(modifier: Modifier) =
        PeraTitleText(
            modifier = modifier,
            text = stringResource(id = R.string.enable_biometric_authentication)
        )

    @Composable
    override fun Description(modifier: Modifier) =
        PeraDescriptionText(
            modifier = modifier,
            text = stringResource(id = R.string.your_faceid_or_fingerprintid)
        )

    @Composable
    override fun PrimaryButton(modifier: Modifier) =
        PeraPrimaryButton(
            onClick = { checkBiometricAuthentication() },
            modifier = modifier,
            text = stringResource(id = R.string.enable_biometric_authentication)
        )

    @Composable
    override fun SecondaryButton(modifier: Modifier) =
        PeraSecondaryButton(
            onClick = { navigateToHomeNavigation() },
            modifier = modifier,
            text = stringResource(id = R.string.do_not_use)
        )

    private fun checkBiometricAuthentication() {
        activity?.showBiometricAuthentication(
            getString(R.string.app_name),
            getString(R.string.please_use_biometric),
            getString(R.string.cancel),
            successCallback = {
                biometricRegistrationViewModel.setBiometricRegistrationPreference(true)
                handleNextNavigation()
            },
            failCallBack = null,
            hardwareErrorCallback = ::showUnsuccessfulBiometricRegistrationDialog
        )
    }

    private fun showUnsuccessfulBiometricRegistrationDialog() {
        context?.alertDialog {
            setTitle(getString(R.string.warning))
            setMessage(getString(R.string.there_s_no_biometric_registration))
            setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
                handleNextNavigation()
            }
        }?.show()
    }

    private fun handleNextNavigation() {
        nav(
            BiometricRegistrationFragmentDirections
                .actionBiometricRegistrationFragmentToBiometricAuthenticationEnabledFragment()
        )
    }

    private fun navigateToHomeNavigation() {
        nav(BiometricRegistrationFragmentDirections.actionBiometricRegistrationFragmentToHomeNavigation())
    }
}
