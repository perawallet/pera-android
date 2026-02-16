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

package com.algorand.android.ui.settings.developeroptions.featureflags.view

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.PeraSwitch
import com.algorand.android.ui.compose.widget.PeraToolbar
import com.algorand.android.ui.compose.widget.PeraToolbarIcon
import com.algorand.android.ui.compose.widget.button.PeraSecondaryButton
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple
import com.algorand.android.ui.settings.developeroptions.featureflags.viewmodel.OverrideFeatureFlagsViewModel
import com.algorand.android.ui.settings.developeroptions.featureflags.viewmodel.OverrideFeatureFlagsViewModel.ViewState.Content
import com.algorand.android.ui.settings.developeroptions.featureflags.viewmodel.OverrideFeatureFlagsViewModel.ViewState.Idle
import com.algorand.wallet.devoptions.domain.model.DeveloperOptionFeatureFlag

@Composable
fun OverrideFeatureFlagsScreen(
    onNavBack: () -> Unit,
    viewModel: OverrideFeatureFlagsViewModel = hiltViewModel()
) {
    Column(modifier = Modifier.fillMaxSize()) {
        PeraToolbar(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(R.string.override_feature_flags),
            startContainer = {
                PeraToolbarIcon(
                    iconResId = R.drawable.ic_left_arrow,
                    modifier = Modifier.clickableNoRipple(onClick = onNavBack)
                )
            }
        )

        when (val viewState = viewModel.state.collectAsStateWithLifecycle().value) {
            Idle -> Unit
            is Content -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PeraSecondaryButton(
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.enableAllFeatureFlags() },
                        text = stringResource(R.string.enable_all)
                    )
                    PeraSecondaryButton(
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.disableAllFeatureFlags() },
                        text = stringResource(R.string.disable_all)
                    )
                }
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(viewState.featureFlags) { featureFlag ->
                        FeatureFlagItem(featureFlag, viewModel)
                    }
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        viewModel.initViewState()
    }
}

@Composable
private fun FeatureFlagItem(flag: DeveloperOptionFeatureFlag, viewModel: OverrideFeatureFlagsViewModel) {
    Box {
        val density = LocalDensity.current
        var rowSize by remember { mutableStateOf<IntSize?>(null) }
        val overrideContainerTopPadding by remember(rowSize, flag.status) {
            if (rowSize != null && flag.status is DeveloperOptionFeatureFlag.Status.Overridden) {
                val padding = with(density) { (rowSize?.height ?: 0).toDp() + 2.toDp() }
                mutableStateOf(padding)
            } else {
                mutableStateOf(0.dp)
            }
        }
        val animatedPadding = animateDpAsState(overrideContainerTopPadding)
        Row(
            modifier = Modifier
                .background(color = PeraTheme.colors.layer.grayLightest, shape = RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .zIndex(1f)
                .onSizeChanged { rowSize = it },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = flag.featureToggle.description,
                    style = PeraTheme.typography.body.regular.sansMedium,
                    color = PeraTheme.colors.text.main
                )
                Text(
                    text = "${getStateValue(flag.remoteValue)} : ${flag.featureToggle.key}",
                    style = PeraTheme.typography.footnote.sans,
                    color = PeraTheme.colors.text.gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            PeraSwitch(flag.status is DeveloperOptionFeatureFlag.Status.Overridden) {
                viewModel.toggleOverriddenStatus(flag.featureToggle)
            }
        }

        Row(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, top = animatedPadding.value)
                .background(color = PeraTheme.colors.layer.grayLighter, shape = RoundedCornerShape(16.dp))
                .padding(start = 16.dp, end = 16.dp, top = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val isEnabled = (flag.status as? DeveloperOptionFeatureFlag.Status.Overridden)?.isEnabled == true
            Text(
                modifier = Modifier.weight(1f),
                text = "${stringResource(R.string.overridden)} : ${getStateValue(isEnabled)}",
                style = PeraTheme.typography.footnote.sans,
                color = PeraTheme.colors.text.gray
            )
            PeraSwitch(isEnabled) {
                viewModel.overrideFeatureFlag(flag.featureToggle, it)
            }
        }
    }
}

@Composable
private fun getStateValue(isEnabled: Boolean): String {
    return if (isEnabled) {
        stringResource(R.string.enabled)
    } else {
        stringResource(R.string.disabled)
    }
}
