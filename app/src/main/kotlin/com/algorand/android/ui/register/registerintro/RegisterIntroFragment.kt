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

package com.algorand.android.ui.register.registerintro

import GroupChoiceWidget
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import com.algorand.android.LoginNavigationDirections
import com.algorand.android.MainActivity
import com.algorand.android.R
import com.algorand.android.core.DaggerBaseFragment
import com.algorand.android.customviews.toolbar.buttoncontainer.model.TextButton
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.RegisterIntroPreview
import com.algorand.android.models.StatusBarConfiguration
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.modules.tracking.core.PeraClickEvent
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.theme.PeraTheme.typography
import com.algorand.android.ui.compose.widget.icon.PeraIcon
import com.algorand.android.utils.browser.PRIVACY_POLICY_URL
import com.algorand.android.utils.browser.TERMS_AND_SERVICES_URL
import com.algorand.android.utils.browser.openPrivacyPolicyUrl
import com.algorand.android.utils.browser.openTermsAndServicesUrl
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterNotNull

@Suppress("MagicNumber")
@AndroidEntryPoint
class RegisterIntroFragment : DaggerBaseFragment(0) {

    private val registerIntroViewModel: RegisterIntroViewModel by viewModels()

    private val statusBarConfiguration =
        StatusBarConfiguration(backgroundColor = R.color.tertiary_background)

    private val toolbarConfiguration =
        ToolbarConfiguration(backgroundColor = R.color.primary_background)

    override val fragmentConfiguration = FragmentConfiguration(
        toolbarConfiguration = toolbarConfiguration,
        statusBarConfiguration = statusBarConfiguration
    )

    private val registerIntroPreviewCollector: suspend (RegisterIntroPreview) -> Unit = {
        configureToolbar(it.isCloseButtonVisible, it.isSkipButtonVisible)
        (activity as MainActivity).hideProgress()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                PeraTheme {
                    RegisterTypeSelectionScreen()
                }
            }
        }
    }

    @Suppress("LongMethod")
    @Composable
    fun RegisterTypeSelectionScreen() {
        val registerIntroPreview by registerIntroViewModel.registerIntroPreviewFlow.collectAsState()
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    style = typography.title.regular.sansMedium,
                    color = PeraTheme.colors.text.main,
                    text = stringResource(
                        if (registerIntroViewModel.isHdWalletToggleEnabled()) {
                            R.string.add_a_wallet_or_account
                        } else {
                            R.string.welcome_to_pera
                        }
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
                PeraIcon(
                    painter = painterResource(R.drawable.pera_icon_3d),
                    contentDescription = stringResource(id = R.string.add_a_wallet_or_account)
                )
            }
            Spacer(modifier = Modifier.weight(1f))

            if (registerIntroViewModel.isHdWalletToggleEnabled() && registerIntroPreview?.hasHdWallet == true) {
                CreateNewAccountCard(
                    onClick = {
                        navToHdWalletSelectionFragment()
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            if (registerIntroViewModel.isHdWalletToggleEnabled()) {
                CreateWalletHdWidget()
                ImportHdWalletWidget()
            } else {
                CreateAlgo25AccountWidget()
                ImportAlgo25AccountWidget()
            }

            WatchAddressWidget()
            Spacer(modifier = Modifier.weight(1f))

            TermsAndPrivacy()
        }
    }

    @Suppress("LongMethod")
    @Composable
    fun CreateNewAccountCard(
        modifier: Modifier = Modifier,
        onClick: () -> Unit = {}
    ) {
        val icon = ImageVector.vectorResource(id = R.drawable.ic_hd_wallet)
        val outlineColor = PeraTheme.colors.wallet.governor.wallet3Icon
        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

        Box(
            modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .pointerInput(Unit) { detectTapGestures(onTap = { onClick() }) }
                .drawBehind {
                    drawRoundRect(
                        color = outlineColor,
                        size = Size(size.width, size.height),
                        cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx()),
                        style = Stroke(width = 2.dp.toPx(), pathEffect = dashEffect)
                    )
                }
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PeraTheme.colors.layer.grayLighter)
                        .padding(8.dp),
                    imageVector = icon,
                    contentDescription = stringResource(id = R.string.add_a_new_account_desc),
                    tint = PeraTheme.colors.text.main
                )

                Spacer(Modifier.width(24.dp))
                Column {
                    Text(
                        style = typography.body.regular.sansMedium,
                        color = PeraTheme.colors.text.main,
                        text = stringResource(id = R.string.add_a_new_account)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        style = typography.footnote.sans,
                        color = PeraTheme.colors.text.gray,
                        text = stringResource(id = R.string.add_a_new_account_desc)
                    )
                }
            }

            Row(
                Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-12).dp)
                    .background(PeraTheme.colors.background.primary)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PeraIcon(
                    painter = painterResource(id = R.drawable.ic_info),
                    contentDescription = stringResource(id = R.string.warning),
                    modifier = Modifier.size(20.dp),
                    tintColor = PeraTheme.colors.wallet.governor.wallet3Icon
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = stringResource(id = R.string.because_you_have_already),
                    style = typography.footnote.sansMedium,
                    color = PeraTheme.colors.wallet.governor.wallet3Icon
                )
            }
        }
    }

    @Composable
    private fun CreateWalletHdWidget() {
        GroupChoiceWidget(
            title = stringResource(id = R.string.create_a_new_wallet),
            description = stringResource(id = R.string.create_a_new_wallet_desc),
            icon = ImageVector.vectorResource(R.drawable.ic_wallet),
            iconContentDescription = stringResource(id = R.string.create_a_new_algorand_account_with),
            onClick = ::navToCreateWalletNameRegistrationFragment
        )
    }

    @Composable
    private fun CreateAlgo25AccountWidget() {
        GroupChoiceWidget(
            title = stringResource(id = R.string.create_a_new_account),
            description = stringResource(id = R.string.create_a_new_algorand_account_with),
            icon = ImageVector.vectorResource(R.drawable.ic_wallet),
            iconContentDescription = stringResource(id = R.string.create_a_new_algorand_account_with),
            onClick = ::navToCreateAccountNameRegistrationFragment
        )
    }

    @Composable
    private fun ImportHdWalletWidget() {
        GroupChoiceWidget(
            title = stringResource(id = R.string.import_a_wallet),
            description = stringResource(id = R.string.import_an_existing),
            iconContentDescription = stringResource(id = R.string.import_an_existing),
            icon = ImageVector.vectorResource(R.drawable.ic_key),
            onClick = ::navToAccountRecoveryTypeSelectionFragment
        )
    }

    @Composable
    private fun ImportAlgo25AccountWidget() {
        GroupChoiceWidget(
            title = stringResource(id = R.string.import_an_account),
            description = stringResource(id = R.string.import_an_existing),
            iconContentDescription = stringResource(id = R.string.import_an_existing),
            icon = ImageVector.vectorResource(R.drawable.ic_key),
            onClick = ::navToAccountRecoveryTypeSelectionFragment
        )
    }

    @Composable
    private fun WatchAddressWidget() {
        GroupChoiceWidget(
            title = stringResource(id = R.string.watch_an_address),
            description = stringResource(id = R.string.monitor_an_algorand_address),
            iconContentDescription = stringResource(id = R.string.monitor_an_algorand_address),
            icon = ImageVector.vectorResource(R.drawable.ic_eye),
            onClick = ::navToWatchAccountInfoFragment
        )
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
        val fullText = stringResource(
            id = if (registerIntroViewModel.isHdWalletToggleEnabled()) {
                R.string.by_adding_a_wallet
            } else {
                R.string.by_creating_account
            }
        )
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
    }

    fun initObservers() {
        viewLifecycleOwner.collectLatestOnLifecycle(
            registerIntroViewModel.registerIntroPreviewFlow.filterNotNull(),
            registerIntroPreviewCollector
        )
    }

    private fun navToHdWalletSelectionFragment() {
        registerIntroViewModel.logOnboardingWelcomeAccountCreateClickEvent()
        nav(
            RegisterIntroFragmentDirections.actionRegisterIntroFragmentToHdWalletSelectionFragment()
        )
    }

    private fun navToAccountRecoveryTypeSelectionFragment() {
        registerIntroViewModel.logOnboardingWelcomeAccountRecoverClickEvent()
        nav(RegisterIntroFragmentDirections.actionRegisterIntroFragmentToAccountRecoveryTypeSelectionFragment())
    }

    private fun navToWatchAccountInfoFragment() {
        registerIntroViewModel.logEvent(PeraClickEvent.TAP_ONBOARDING_WELCOME_WATCH)
        nav(RegisterIntroFragmentDirections.actionRegisterIntroFragmentToWatchAccountInfoFragment())
    }

    private fun navToCreateWalletNameRegistrationFragment() {
        registerIntroViewModel.logEvent(PeraClickEvent.TAP_ONBOARDING_CREATE_WALLET)
        nav(
            RegisterIntroFragmentDirections.actionRegisterIntroFragmentToCreateWalletNameRegistrationNavigation(
                registerIntroViewModel.createHdKeyAccount()
            )
        )
    }

    private fun navToCreateAccountNameRegistrationFragment() {
        registerIntroViewModel.logEvent(PeraClickEvent.TAP_ONBOARDING_CREATE_ACCOUNT)
        nav(
            RegisterIntroFragmentDirections.actionRegisterIntroFragmentToCreateAccountNameRegistrationNavigation(
                registerIntroViewModel.createAlgo25Account()
            )
        )
    }

    private fun configureToolbar(isCloseButtonVisible: Boolean, isSkipButtonVisible: Boolean) {
        getAppToolbar()?.let { toolbar ->
            if (isCloseButtonVisible) {
                toolbar.configureStartButton(R.drawable.ic_close, ::navBack)
            }
            if (isSkipButtonVisible) {
                toolbar.setEndButton(button = TextButton(R.string.skip, onClick = ::onSkipClick))
            }
        }
    }

    private fun onSkipClick() {
        registerIntroViewModel.logEvent(PeraClickEvent.TAP_ONBOARDING_WELCOME_SKIP)
        registerIntroViewModel.setRegisterSkip()
        nav(LoginNavigationDirections.actionGlobalToHomeNavigation())
    }
}
