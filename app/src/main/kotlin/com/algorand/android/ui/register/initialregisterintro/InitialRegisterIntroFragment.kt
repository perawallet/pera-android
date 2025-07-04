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

package com.algorand.android.ui.register.initialregisterintro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import com.algorand.android.R
import com.algorand.android.core.DaggerBaseFragment
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.theme.PeraTheme.typography
import com.algorand.android.ui.compose.widget.button.PeraTertiaryButton
import com.algorand.android.ui.compose.widget.icon.PeraIcon
import com.algorand.android.utils.browser.PRIVACY_POLICY_URL
import com.algorand.android.utils.browser.TERMS_AND_SERVICES_URL
import com.algorand.android.utils.browser.openPrivacyPolicyUrl
import com.algorand.android.utils.browser.openTermsAndServicesUrl
import dagger.hilt.android.AndroidEntryPoint

@Suppress("MagicNumber")
@AndroidEntryPoint
class InitialRegisterIntroFragment : DaggerBaseFragment(0) {

    override val fragmentConfiguration = FragmentConfiguration()

    private val viewModel: InitialRegisterIntroViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                PeraTheme {
                    InitialRegisterIntroScreen()
                }
            }
        }
    }

    @Preview
    @Composable
    fun InitialRegisterIntroScreen() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 50.dp)
                        .fillMaxWidth(),
                    style = typography.title.regular.sansMedium,
                    color = PeraTheme.colors.text.main,
                    text = stringResource(
                        R.string.welcome_to_pera
                    )
                )
                Box(
                    modifier = Modifier
                        .weight(1f, fill = true),
                    contentAlignment = Alignment.TopEnd
                ) {
                    PeraIcon(
                        modifier = Modifier.fillMaxWidth(),
                        painter = painterResource(R.drawable.pera_icon_3d),
                        contentDescription = stringResource(id = R.string.welcome_to_pera),
                        contentScale = ContentScale.FillWidth
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            CreateNewWalletWidget()

            Spacer(modifier = Modifier.height(40.dp))

            ImportAccountWidget()

            Spacer(modifier = Modifier.weight(1f))

            TermsAndPrivacy()
        }
    }

    @Composable
    private fun CreateNewWalletWidget() {
        Column {
            Text(
                modifier = Modifier.padding(horizontal = 24.dp),
                style = typography.body.regular.sans,
                text = stringResource(R.string.new_to_algorand),
                color = PeraTheme.colors.text.gray,
            )

            Spacer(Modifier.height(12.dp))

            PeraTertiaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onClick = ::onCreateNewWalletClicked,
                text = stringResource(id = R.string.create_a_new_wallet),
                leftIcon = {
                    PeraIcon(
                        painter = painterResource(id = R.drawable.ic_hd_wallet),
                        contentDescription = stringResource(id = R.string.hd_wallet),
                    )
                },
                rightIcon = {
                    PeraIcon(
                        painter = painterResource(id = R.drawable.ic_right_arrow),
                        contentDescription = stringResource(id = R.string.right_arrow),
                        tintColor = PeraTheme.colors.text.gray
                    )
                },
                horizontalArrangement = Start
            )
        }
    }

    @Composable
    private fun ImportAccountWidget() {
        Column {
            Text(
                modifier = Modifier.padding(horizontal = 24.dp),
                style = typography.body.regular.sans,
                text = stringResource(R.string.already_have_an_account),
                color = PeraTheme.colors.text.gray,
            )

            Spacer(Modifier.height(12.dp))

            PeraTertiaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onClick = ::onImportAccountClicked,
                text = stringResource(id = R.string.import_an_account),
                leftIcon = {
                    PeraIcon(
                        painter = painterResource(id = R.drawable.ic_key),
                        contentDescription = stringResource(id = R.string.hd_wallet),
                    )
                },
                rightIcon = {
                    PeraIcon(
                        painter = painterResource(id = R.drawable.ic_right_arrow),
                        contentDescription = stringResource(id = R.string.right_arrow),
                        tintColor = PeraTheme.colors.text.gray
                    )
                },
                horizontalArrangement = Start
            )
        }
    }

    @Composable
    fun TermsAndPrivacy(modifier: Modifier = Modifier) {
        val context = LocalContext.current
        val layoutResult = remember {
            mutableStateOf<TextLayoutResult?>(null)
        }
        val annotatedString = createAnnotatedString()

        Text(
            style = typography.footnote.sans,
            color = PeraTheme.colors.text.gray,
            modifier = modifier
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
                }
                .padding(horizontal = 43.dp, vertical = 24.dp),
            text = annotatedString,
            onTextLayout = {
                layoutResult.value = it
            }
        )
    }

    @Composable
    private fun createAnnotatedString() = buildAnnotatedString {
        val fullText = stringResource(R.string.by_creating_a_wallet)
        val termsAndConditionsText = stringResource(id = R.string.terms_and_conditions)
        val privacyPolicyText = stringResource(id = R.string.privacy_policy)

        val termsAndConditionsStartIndex = fullText.indexOf(termsAndConditionsText)
        val termsAndConditionsEndIndex = termsAndConditionsStartIndex + termsAndConditionsText.length
        val privacyPolicyStartIndex = fullText.indexOf(privacyPolicyText)
        val privacyPolicyEndIndex = privacyPolicyStartIndex + privacyPolicyText.length

        append(fullText)

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

    private fun onCreateNewWalletClicked() {
        viewModel.logNewOnboardingImportClickEvent()
        navToCreateWalletNameRegistrationNavigation()
    }

    private fun onImportAccountClicked() {
        viewModel.logNewOnboardingCreateNewAccountClickEvent()
        navToAccountRecoveryTypeSelectionNavigation()
    }

    private fun navToAccountRecoveryTypeSelectionNavigation() {
        nav(
            InitialRegisterIntroFragmentDirections
                .actionInitialRegisterIntroFragmentToRecoveryTypeSelectionNavigation()
        )
    }

    private fun navToCreateWalletNameRegistrationNavigation() {
        nav(
            InitialRegisterIntroFragmentDirections
                .actionInitialRegisterIntroFragmentToCreateWalletNameRegistrationNavigation(
                    viewModel.createHdKeyAccount()
                )
        )
    }
}
