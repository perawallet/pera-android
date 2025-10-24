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

package com.algorand.android.ui.settings.developeroptions.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.PeraToolbar
import com.algorand.android.ui.compose.widget.PeraToolbarIcon
import com.algorand.android.ui.compose.widget.button.PeraSecondaryButton
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple
import com.algorand.android.ui.settings.developeroptions.viewmodel.DeveloperOptionsViewModel

@Composable
fun DeveloperOptionsScreen(
    onNavBack: () -> Unit,
    onOverrideFeatureFlagsClick: () -> Unit,
    viewModel: DeveloperOptionsViewModel = hiltViewModel()
) {
    Column(modifier = Modifier.fillMaxSize()) {
        PeraToolbar(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(R.string.developer_options),
            startContainer = {
                PeraToolbarIcon(
                    iconResId = R.drawable.ic_left_arrow,
                    modifier = Modifier.clickableNoRipple(onClick = onNavBack)
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .clickableNoRipple(onClick = onOverrideFeatureFlagsClick)
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.override_feature_flags),
                style = PeraTheme.typography.body.regular.sansMedium,
                color = PeraTheme.colors.text.main,
            )
            Spacer(Modifier.weight(1f))
            Icon(
                painter = painterResource(R.drawable.ic_right_arrow),
                tint = PeraTheme.colors.text.gray,
                contentDescription = null
            )
        }

        Spacer(Modifier.weight(1f))

        PeraSecondaryButton(
            modifier = Modifier
                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
                .fillMaxWidth(),
            text = stringResource(R.string.disable_developer_options),
            onClick = {
                viewModel.disableDevOps()
                onNavBack()
            }
        )
    }
}
