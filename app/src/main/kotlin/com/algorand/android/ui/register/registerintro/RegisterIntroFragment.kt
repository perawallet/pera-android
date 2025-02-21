package com.algorand.android.ui.register.registerintro

import ItemChoiceWidget
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
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
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.PeraBodyText
import com.algorand.android.ui.compose.widget.PeraHeadelineText
import com.algorand.android.utils.browser.PRIVACY_POLICY_URL
import com.algorand.android.utils.browser.TERMS_AND_SERVICES_URL
import com.algorand.android.utils.browser.openPrivacyPolicyUrl
import com.algorand.android.utils.browser.openTermsAndServicesUrl
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterNotNull

// TODO: 16.02.2022 login_navigation graph should be separated into multiple graphs
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
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                PeraTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        RegisterTypeSelectionScreen()
                    }
                }
            }
        }
    }

    @Composable
    fun RegisterTypeSelectionScreen() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            PeraHeadelineText(
                text = stringResource(id = R.string.welcome_to_pera),
                modifier = Modifier
                    .padding(start = 24.dp)
                    .align(alignment = Alignment.Start)
            )
            Spacer(modifier = Modifier.weight(1f))
            ItemChoiceWidget(
                modifier = Modifier.padding(bottom = 40.dp),
                title = stringResource(id = R.string.create_a_new_account),
                description = stringResource(id = R.string.create_a_new_algorand_account_with),
                icon = ImageVector.vectorResource(R.drawable.ic_wallet),
                onClick = ::navToBackupPassphraseInfoNavigation
            )
            ItemChoiceWidget(
                modifier = Modifier.padding(bottom = 40.dp),
                title = stringResource(id = R.string.import_an_account),
                description = stringResource(id = R.string.import_an_existing),
                icon = ImageVector.vectorResource(R.drawable.ic_key),
                onClick = ::navToAccountRecoveryTypeSelectionFragment
            )
            ItemChoiceWidget(
                title = stringResource(id = R.string.watch_an_account),
                description = stringResource(id = R.string.monitor_an_algorand_account),
                icon = ImageVector.vectorResource(R.drawable.ic_eye),
                onClick = ::navToWatchAccountInfoFragment
            )
            Spacer(modifier = Modifier.weight(1f))
            TermsAndPrivacy()
        }
    }

    @Composable
    fun TermsAndPrivacy(modifier: Modifier = Modifier) {
        val context = LocalContext.current
        val layoutResult = remember {
            mutableStateOf<TextLayoutResult?>(null)
        }
        val annotatedString = createAnnotatedString()

        PeraBodyText(
            modifier = modifier
                .pointerInput(Unit) {
                    detectTapGestures { pos ->
                        layoutResult.value?.let { layoutResult ->
                            val offset = layoutResult.getOffsetForPosition(pos)
                            annotatedString.getStringAnnotations(
                                tag = "TERMS_AND_CONDITIONS",
                                start = offset,
                                end = offset
                            ).firstOrNull()?.let { annotation ->
                                context.openTermsAndServicesUrl()
                            }
                            annotatedString.getStringAnnotations(
                                tag = "PRIVACY_POLICY",
                                start = offset,
                                end = offset
                            ).firstOrNull()?.let { annotation ->
                                context.openPrivacyPolicyUrl()
                            }
                        }
                    }
                }
                .padding(start = 43.dp, end = 43.dp, bottom = 24.dp),
            text = annotatedString,
            onTextLayout = {
                layoutResult.value = it
            }
        )
    }

    @Composable
    private fun createAnnotatedString() = buildAnnotatedString {
        val fullText = stringResource(id = R.string.by_creating_account)
        val termsAndConditionsText = stringResource(id = R.string.terms_and_conditions)
        val privacyPolicyText = stringResource(id = R.string.privacy_policy)

        val termsAndConditionsStartIndex = fullText.indexOf(termsAndConditionsText)
        val termsAndConditionsEndIndex =
            termsAndConditionsStartIndex + termsAndConditionsText.length
        val privacyPolicyStartIndex = fullText.indexOf(privacyPolicyText)
        val privacyPolicyEndIndex = privacyPolicyStartIndex + privacyPolicyText.length

        append(fullText)

        addStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.outline
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
                color = MaterialTheme.colorScheme.outline
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

    private fun navToBackupPassphraseInfoNavigation() {
        registerIntroViewModel.logOnboardingWelcomeAccountCreateClickEvent()
        nav(
            RegisterIntroFragmentDirections.actionRegisterIntroFragmentToBackupPassphraseInfoNavigation(
                publicKeysOfAccountsToBackup = emptyArray()
            )
        )
    }

    private fun navToAccountRecoveryTypeSelectionFragment() {
        registerIntroViewModel.logOnboardingWelcomeAccountRecoverClickEvent()
        nav(RegisterIntroFragmentDirections.actionRegisterIntroFragmentToAccountRecoveryTypeSelectionFragment())
    }

    private fun navToWatchAccountInfoFragment() {
        registerIntroViewModel.logOnboardingCreateWatchAccountClickEvent()
        nav(RegisterIntroFragmentDirections.actionRegisterIntroFragmentToWatchAccountInfoFragment())
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
        registerIntroViewModel.logOnboardingCreateAccountSkipClickEvent()
        registerIntroViewModel.setRegisterSkip()
        nav(LoginNavigationDirections.actionGlobalToHomeNavigation())
    }
}
