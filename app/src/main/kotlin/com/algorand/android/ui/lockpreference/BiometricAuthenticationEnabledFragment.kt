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
import com.algorand.android.R
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.ui.common.BaseInfoFragment
import com.algorand.android.ui.compose.widget.PeraDescriptionText
import com.algorand.android.ui.compose.widget.PeraPrimaryButton
import com.algorand.android.ui.compose.widget.PeraTitleText
import com.algorand.android.ui.lockpreference.BiometricAuthenticationEnabledFragmentDirections.Companion.actionBiometricAuthenticationEnabledFragmentToHomeNavigation

class BiometricAuthenticationEnabledFragment : BaseInfoFragment() {

    private val toolbarConfiguration = ToolbarConfiguration(
        startIconResId = R.drawable.ic_left_arrow,
        startIconClick = ::navBack
    )

    override val fragmentConfiguration =
        FragmentConfiguration(toolbarConfiguration = toolbarConfiguration)

    @Composable
    override fun Icon(modifier: Modifier) =
        Image(
            painter = painterResource(id = R.drawable.ic_check),
            colorFilter = ColorFilter.tint(color = colorResource(R.color.info_image_color)),
            contentDescription = "check",
            modifier = modifier
        )

    @Composable
    override fun Title(modifier: Modifier) =
        PeraTitleText(
            modifier = modifier,
            text = stringResource(id = R.string.biometric_authentication_enabled)
        )

    @Composable
    override fun Description(modifier: Modifier) =
        PeraDescriptionText(
            text = stringResource(id = R.string.your_passcode_has_been_verified),
            modifier = modifier
        )

    @Composable
    override fun PrimaryButton(modifier: Modifier) =
        PeraPrimaryButton(
            onClick = { navigateToHomeNavigation() },
            modifier = modifier,
            text = stringResource(id = R.string.go_to_accounts)
        )

    private fun navigateToHomeNavigation() {
        nav(actionBiometricAuthenticationEnabledFragmentToHomeNavigation())
    }
}
