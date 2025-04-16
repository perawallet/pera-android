package com.algorand.android.ui.register.registerintro

import ItemChoiceWidget
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.algorand.android.models.OnboardingAccountType
import com.algorand.android.models.RegisterIntroPreview
import com.algorand.android.models.StatusBarConfiguration
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.modules.tracking.core.PeraClickEvent
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.PeraBodyText
import com.algorand.android.ui.compose.widget.PeraCard
import com.algorand.android.ui.compose.widget.PeraHeadlineText
import com.algorand.android.ui.compose.widget.PeraTitleText
import com.algorand.android.utils.browser.PRIVACY_POLICY_URL
import com.algorand.android.utils.browser.TERMS_AND_SERVICES_URL
import com.algorand.android.utils.browser.openPrivacyPolicyUrl
import com.algorand.android.utils.browser.openTermsAndServicesUrl
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

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

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                PeraTheme {
                    val sheetState = rememberModalBottomSheetState(
                        skipPartiallyExpanded = true
                    )
                    val showBottomSheet = rememberSaveable { mutableStateOf(false) }

                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        RegisterTypeSelectionScreen(showBottomSheet)
                        if (showBottomSheet.value) {
                            androidx.compose.material3.ModalBottomSheet(
                                onDismissRequest = {
                                    showBottomSheet.value = false
                                },
                                sheetState = sheetState,
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ) {
                                BottomSheetContent(
                                    sheetState = sheetState,
                                    onDismiss = { showBottomSheet.value = false }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Suppress("LongMethod")
    @Composable
    fun RegisterTypeSelectionScreen(showBottomSheet: MutableState<Boolean>) {
        val coroutineScope = rememberCoroutineScope()
        val registerIntroPreview by registerIntroViewModel.registerIntroPreviewFlow.collectAsState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            PeraHeadlineText(
                text = stringResource(id = R.string.welcome_to_pera),
                modifier = Modifier
                    .padding(start = 24.dp)
                    .align(alignment = Alignment.Start)
            )
            Spacer(modifier = Modifier.weight(1f))
            // uncomment when we get add HD address screen designed
//            if (registerIntroViewModel.isHdWalletToggleEnabled() &&
//                (registerIntroPreview?.hasHdWallet ?: false)
//            ) {
//                ItemChoiceWidget(
//                    modifier = Modifier,
//                    title = stringResource(id = R.string.create_a_new_account),
//                    description = stringResource(id = R.string.create_a_new_account_desc),
//                    icon = ImageVector.vectorResource(R.drawable.ic_wallet),
//                    iconContentDescription = stringResource(id = R.string.create_a_new_account_desc),
//                    onClick = {
//                        coroutineScope.launch {
//                            showBottomSheet.value = true
//                        }
//                    }
//                )
//                Spacer(modifier = Modifier.height(40.dp))
//            }
            ItemChoiceWidget(
                modifier = Modifier,
                title = stringResource(id = R.string.create_a_new_wallet),
                description = stringResource(id = R.string.create_a_new_algorand_account_with),
                icon = ImageVector.vectorResource(R.drawable.ic_wallet),
                iconContentDescription = stringResource(id = R.string.create_a_new_algorand_account_with),
                onClick = {
                    coroutineScope.launch {
                        if (registerIntroViewModel.isHdWalletToggleEnabled()) {
                            showBottomSheet.value = true
                        } else {
                            navToBackupPassphraseInfoNavigation(
                                OnboardingAccountType.Algo25
                            )
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.height(40.dp))
            ItemChoiceWidget(
                modifier = Modifier,
                title = stringResource(id = R.string.import_an_account),
                description = stringResource(id = R.string.import_an_existing),
                iconContentDescription = stringResource(id = R.string.import_an_existing),
                icon = ImageVector.vectorResource(R.drawable.ic_key),
                onClick = ::navToAccountRecoveryTypeSelectionFragment
            )
            Spacer(modifier = Modifier.height(40.dp))
            ItemChoiceWidget(
                modifier = Modifier,
                title = stringResource(id = R.string.watch_an_account),
                description = stringResource(id = R.string.monitor_an_algorand_account),
                iconContentDescription = stringResource(id = R.string.monitor_an_algorand_account),
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun BottomSheetContent(
        sheetState: SheetState,
        onDismiss: () -> Unit
    ) {
        val coroutineScope = rememberCoroutineScope()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            BottomSheetHeader(sheetState, onDismiss)

            PeraCard(
                title = stringResource(R.string.mnemonic_type_universal_title),
                description = stringResource(R.string.mnemonic_type_universal_description),
                footer = stringResource(R.string.mnemonic_type_universal_footer),
                highlighted = stringResource(R.string.recommended),
                onClick = {
                    navToBackupPassphraseInfoNavigation(
                        OnboardingAccountType.HdKey
                    )
                    coroutineScope.launch {
                        sheetState.hide()
                    }
                }
            )

            PeraCard(
                title = stringResource(R.string.mnemonic_type_algo25_title),
                description = stringResource(R.string.mnemonic_type_algo25_description),
                footer = stringResource(R.string.mnemonic_type_algo25_footer),
                onClick = {
                    navToBackupPassphraseInfoNavigation(
                        OnboardingAccountType.Algo25
                    )
                    coroutineScope.launch {
                        sheetState.hide()
                    }
                }
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Suppress("MagicNumber")
    @Composable
    fun BottomSheetHeader(
        sheetState: SheetState,
        onDismiss: () -> Unit
    ) {
        val coroutineScope = rememberCoroutineScope()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 10.dp,
                    end = 40.dp,
                    bottom = 24.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        sheetState.hide()
                        onDismiss()
                    }
                }) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = stringResource(id = R.string.close)
                )
            }
            Spacer(Modifier.weight(0.1f))

            PeraTitleText(
                text = stringResource(id = R.string.bottom_sheet_mnemonic_type_title)
            )
            Spacer(Modifier.weight(1f))
        }
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

    private fun navToBackupPassphraseInfoNavigation(onboardingAccountType: OnboardingAccountType) {
        registerIntroViewModel.logOnboardingWelcomeAccountCreateClickEvent()
        nav(
            RegisterIntroFragmentDirections.actionRegisterIntroFragmentToBackupPassphraseInfoNavigation(
                accountsToBackup = emptyArray(),
                onboardingAccountType = onboardingAccountType
            )
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
