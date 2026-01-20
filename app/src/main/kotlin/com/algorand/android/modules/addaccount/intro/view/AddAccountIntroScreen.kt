/*
 *  Copyright 2022-2025 Pera Wallet, LDA
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.algorand.android.modules.addaccount.intro.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.modules.addaccount.intro.viewmodel.AddAccountIntroViewModel
import com.algorand.android.modules.addaccount.intro.viewmodel.AddAccountIntroViewModel.ViewState.Content
import com.algorand.android.modules.addaccount.intro.viewmodel.AddAccountIntroViewModel.ViewState.Idle
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.GroupChoiceWidget
import com.algorand.android.ui.compose.widget.icon.PeraIcon
import com.algorand.android.utils.browser.PRIVACY_POLICY_URL
import com.algorand.android.utils.browser.TERMS_AND_SERVICES_URL
import com.algorand.android.utils.browser.openPrivacyPolicyUrl
import com.algorand.android.utils.browser.openTermsAndServicesUrl

@Composable
fun AddAccountIntroScreen(
    modifier: Modifier = Modifier,
    listener: AddAccountIntroScreenListener,
    viewModel: AddAccountIntroViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showOtherOptions by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Header(onCloseClick = listener::onCloseClick)

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (state) {
                is Idle -> Unit
                is Content -> {
                    val preview = (state as Content).preview
                    if (preview.hasHdWallet) {
                        AddAccountWidget(listener::onAddAccountClick)
                    } else {
                        CreateUniversalWalletWidget(listener::onCreateUniversalWalletClick)
                    }
                    if (viewModel.isJointAccountFeatureEnabled()) {
                        AddJointAccountWidget(listener::onAddJointAccountClick)
                    }
                    ImportAccountWidget(listener::onImportAccountClick)

                    if (!showOtherOptions) {
                        SeeOtherOptionsButton(
                            onClick = { showOtherOptions = true }
                        )
                    } else {
                        WatchAddressWidget(listener::onWatchAddressClick)
                        if (preview.hasHdWallet) {
                            CreateUniversalWalletWidget(listener::onCreateUniversalWalletClick)
                        }
                        CreateAlgo25AccountWidget(listener::onCreateAlgo25AccountClick)
                    }

                    TermsAndPrivacy(
                        modifier = Modifier.padding(top = 48.dp, bottom = 24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun Header(onCloseClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.padding(start = 20.dp, top = 10.dp)) {
            IconButton(onClick = onCloseClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = stringResource(id = R.string.close),
                    modifier = Modifier.size(24.dp),
                    tint = PeraTheme.colors.text.main
                )
            }

            Text(
                modifier = Modifier.padding(start = 10.dp, top = 12.dp),
                style = PeraTheme.typography.title.regular.sansMedium,
                color = PeraTheme.colors.text.main,
                text = stringResource(R.string.add_an_account_title)
            )
        }

        PeraIcon(
            painter = painterResource(R.drawable.pera_icon_3d),
            contentDescription = stringResource(id = R.string.add_an_account_title),
            contentScale = ContentScale.FillWidth
        )
    }
}

@Composable
private fun AddAccountWidget(onClick: () -> Unit) {
    GroupChoiceWidget(
        title = stringResource(id = R.string.add_account),
        description = stringResource(id = R.string.add_account_desc),
        icon = ImageVector.vectorResource(R.drawable.ic_wallet_add),
        iconContentDescription = stringResource(id = R.string.add_account),
        onClick = onClick
    )
}

@Composable
private fun AddJointAccountWidget(onClick: () -> Unit) {
    GroupChoiceWidget(
        title = stringResource(id = R.string.add_joint_account),
        description = stringResource(id = R.string.add_joint_account_desc),
        icon = ImageVector.vectorResource(R.drawable.ic_joint),
        iconContentDescription = stringResource(id = R.string.add_joint_account),
        showNewBadge = true,
        onClick = onClick
    )
}

@Composable
private fun ImportAccountWidget(onClick: () -> Unit) {
    GroupChoiceWidget(
        title = stringResource(id = R.string.import_account),
        description = stringResource(id = R.string.import_an_existing),
        iconContentDescription = stringResource(id = R.string.import_account),
        icon = ImageVector.vectorResource(R.drawable.ic_import_account),
        onClick = onClick
    )
}

@Composable
private fun WatchAddressWidget(onClick: () -> Unit) {
    GroupChoiceWidget(
        title = stringResource(id = R.string.watch_an_address),
        description = stringResource(id = R.string.monitor_an_algorand_address),
        iconContentDescription = stringResource(id = R.string.monitor_an_algorand_address),
        icon = ImageVector.vectorResource(R.drawable.ic_eye),
        onClick = onClick
    )
}

@Composable
private fun CreateUniversalWalletWidget(onClick: () -> Unit) {
    GroupChoiceWidget(
        title = stringResource(id = R.string.create_universal_wallet),
        description = stringResource(id = R.string.create_universal_wallet_desc),
        icon = ImageVector.vectorResource(R.drawable.ic_hd_wallet),
        iconContentDescription = stringResource(id = R.string.create_universal_wallet),
        onClick = onClick
    )
}

@Composable
private fun CreateAlgo25AccountWidget(onClick: () -> Unit) {
    GroupChoiceWidget(
        title = stringResource(id = R.string.create_algo25_account),
        description = stringResource(id = R.string.create_algo25_account_desc),
        icon = ImageVector.vectorResource(R.drawable.ic_wallet),
        iconContentDescription = stringResource(id = R.string.create_algo25_account),
        onClick = onClick
    )
}

@Composable
private fun SeeOtherOptionsButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = PeraTheme.colors.layer.grayLighter,
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(R.drawable.ic_arrow_down),
                contentDescription = stringResource(id = R.string.see_other_options),
                tint = PeraTheme.colors.text.main
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = stringResource(id = R.string.see_other_options),
            style = PeraTheme.typography.body.regular.sansMedium,
            color = PeraTheme.colors.text.main
        )
    }
}

@Composable
private fun TermsAndPrivacy(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val layoutResult = remember {
        mutableStateOf<TextLayoutResult?>(null)
    }
    val annotatedString = createAnnotatedString()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            style = PeraTheme.typography.footnote.sans,
            color = PeraTheme.colors.text.gray,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures { pos ->
                        layoutResult.value?.let { layoutResult ->
                            val offset = layoutResult.getOffsetForPosition(pos)
                            annotatedString.getStringAnnotations(
                                tag = "TERMS_AND_CONDITIONS",
                                start = offset,
                                end = offset
                            ).firstOrNull()?.let {
                                context.openTermsAndServicesUrl()
                            }
                            annotatedString.getStringAnnotations(
                                tag = "PRIVACY_POLICY",
                                start = offset,
                                end = offset
                            ).firstOrNull()?.let {
                                context.openPrivacyPolicyUrl()
                            }
                        }
                    }
                },
            text = annotatedString,
            onTextLayout = {
                layoutResult.value = it
            }
        )
    }
}

@Composable
private fun createAnnotatedString() = buildAnnotatedString {
    val fullText = stringResource(R.string.by_adding_a_wallet)
    val termsAndConditionsText = stringResource(id = R.string.terms_and_conditions)
    val privacyPolicyText = stringResource(id = R.string.privacy_policy)

    val termsAndConditionsStartIndex = fullText.indexOf(termsAndConditionsText)
    val termsAndConditionsEndIndex = termsAndConditionsStartIndex + termsAndConditionsText.length
    val privacyPolicyStartIndex = fullText.indexOf(privacyPolicyText)
    val privacyPolicyEndIndex = privacyPolicyStartIndex + privacyPolicyText.length

    append(fullText)

    if (termsAndConditionsStartIndex >= 0 && termsAndConditionsEndIndex > termsAndConditionsStartIndex) {
        addStyle(
            style = SpanStyle(
                color = PeraTheme.colors.link.primary
            ),
            start = termsAndConditionsStartIndex,
            end = termsAndConditionsEndIndex
        )
        addStringAnnotation(
            tag = "TERMS_AND_CONDITIONS",
            annotation = TERMS_AND_SERVICES_URL,
            start = termsAndConditionsStartIndex,
            end = termsAndConditionsEndIndex
        )
    }

    if (privacyPolicyStartIndex >= 0 && privacyPolicyEndIndex > privacyPolicyStartIndex) {
        addStyle(
            style = SpanStyle(
                color = PeraTheme.colors.link.primary
            ),
            start = privacyPolicyStartIndex,
            end = privacyPolicyEndIndex
        )
        addStringAnnotation(
            tag = "PRIVACY_POLICY",
            annotation = PRIVACY_POLICY_URL,
            start = privacyPolicyStartIndex,
            end = privacyPolicyEndIndex
        )
    }
}

interface AddAccountIntroScreenListener {
    fun onAddAccountClick()
    fun onAddJointAccountClick()
    fun onImportAccountClick()
    fun onWatchAddressClick()
    fun onCreateUniversalWalletClick()
    fun onCreateAlgo25AccountClick()
    fun onCloseClick()
}
