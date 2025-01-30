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

package com.algorand.android.ui.register.watch

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.viewModels
import com.algorand.android.R
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.customviews.toolbar.buttoncontainer.model.IconButton
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.ui.common.BaseInfoFragment
import com.algorand.android.ui.compose.widget.PeraDescriptionText
import com.algorand.android.ui.compose.widget.PeraPrimaryButton
import com.algorand.android.ui.compose.widget.PeraTitleText
import com.algorand.android.utils.browser.openWatchAccountSupportUrl
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WatchAccountInfoFragment : BaseInfoFragment() {

    private val toolbarConfiguration = ToolbarConfiguration(
        startIconResId = R.drawable.ic_left_arrow,
        startIconClick = ::navBack
    )

    private val watchAccountInfoViewModel: WatchAccountInfoViewModel by viewModels()

    override val fragmentConfiguration =
        FragmentConfiguration(toolbarConfiguration = toolbarConfiguration)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureToolbar()
    }

    @Composable
    override fun Icon(modifier: Modifier) =
        Image(
            painter = painterResource(id = R.drawable.ic_eye),
            colorFilter = ColorFilter.tint(color = colorResource(R.color.info_image_color)),
            contentDescription = "eye",
            modifier = modifier
        )

    @Composable
    override fun Title(modifier: Modifier) =
        PeraTitleText(
            modifier = modifier,
            text = stringResource(id = R.string.watch_account)
        )

    @Composable
    override fun Description(modifier: Modifier) =
        PeraDescriptionText(
            text = stringResource(id = R.string.monitor_activity_of),
            modifier = modifier
        )

    @Composable
    override fun Warning(modifier: Modifier) =
        Text(
            text = stringResource(id = R.string.if_you_do_not),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            modifier = modifier
        )

    @Composable
    override fun PrimaryButton(modifier: Modifier) =
        PeraPrimaryButton(
            onClick = { navigateToRegisterWatchAccountFragment() },
            modifier = modifier,
            text = stringResource(id = R.string.create_a_watch)
        )

    private fun navigateToRegisterWatchAccountFragment() {
        watchAccountInfoViewModel.logOnboardingCreateWatchAccountClickEvent()
        nav(WatchAccountInfoFragmentDirections.actionWatchAccountInfoFragmentToRegisterWatchAccountNavigation())
    }

    private fun configureToolbar() {
        getAppToolbar()?.setEndButton(
            button = IconButton(
                R.drawable.ic_info,
                onClick = ::onInfoClick
            )
        )
    }

    private fun onInfoClick() {
        context?.openWatchAccountSupportUrl()
    }
}
