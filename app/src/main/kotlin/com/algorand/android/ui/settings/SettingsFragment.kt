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

package com.algorand.android.ui.settings

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.State
import com.algorand.android.BuildConfig
import com.algorand.android.MainNavigationDirections
import com.algorand.android.R
import com.algorand.android.core.DaggerBaseFragment
import com.algorand.android.databinding.FragmentSettingsBinding
import com.algorand.android.models.AnnotatedString
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.models.WarningConfirmation
import com.algorand.android.ui.common.warningconfirmation.WarningConfirmationBottomSheet.Companion.WARNING_CONFIRMATION_KEY
import com.algorand.android.ui.settings.SettingsViewModel.ViewEvent
import com.algorand.android.ui.settings.SettingsViewModel.ViewEvent.NavigateToPasskeys
import com.algorand.android.ui.settings.SettingsViewModel.ViewEvent.ShowDataClearedBottomSheet
import com.algorand.android.ui.settings.SettingsViewModel.ViewEvent.ShowDevOptionsAlreadyEnabled
import com.algorand.android.ui.settings.SettingsViewModel.ViewEvent.ShowDevOptionsEnabled
import com.algorand.android.ui.settings.SettingsViewModel.ViewEvent.ShowRemainingClicksToDevOptions
import com.algorand.android.utils.browser.openPrivacyPolicyUrl
import com.algorand.android.utils.browser.openSupportCenterUrl
import com.algorand.android.utils.browser.openTermsAndServicesUrl
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.extensions.hide
import com.algorand.android.utils.extensions.show
import com.algorand.android.utils.startSavedStateListener
import com.algorand.android.utils.useSavedStateValue
import com.algorand.android.utils.viewbinding.viewBinding
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class SettingsFragment : DaggerBaseFragment(R.layout.fragment_settings) {

    @Inject
    lateinit var gson: Gson

    private val settingsViewModel: SettingsViewModel by viewModels()

    private val binding by viewBinding(FragmentSettingsBinding::bind)

    private var devOptionsToast: Toast? = null

    private val toolbarConfiguration = ToolbarConfiguration(
        titleResId = R.string.settings,
        startIconResId = R.drawable.ic_left_arrow,
        startIconClick = ::navBack
    )

    override val fragmentConfiguration: FragmentConfiguration = FragmentConfiguration(
        isBottomBarNeeded = true,
        toolbarConfiguration = toolbarConfiguration
    )

    private val firebaseInstanceIdCollector: suspend (String?) -> Unit = { firebaseId ->
        binding.firebaseIdTextView.text = getString(
            R.string.firebase_id_format,
            firebaseId ?: R.string.not_available
        )
    }

    private val viewEventCollector: suspend (ViewEvent) -> Unit = { event ->
        when (event) {
            ShowDataClearedBottomSheet -> navigateToDataClearedBottomSheet()
            ShowDevOptionsAlreadyEnabled -> showToast(getString(R.string.developer_options_is_already_enabled))
            ShowDevOptionsEnabled -> showAlertSuccess(title = getString(R.string.developer_options_enabled))
            is ShowRemainingClicksToDevOptions -> {
                showToast(getString(R.string.more_click_to_enable_developer, event.remainingClicks))
            }
            NavigateToPasskeys -> nav(SettingsFragmentDirections.actionSettingsFragmentToPasskeysFragment())
        }
    }

    private val cloudBackupEnabledCollector: suspend (Boolean) -> Unit = { enabled ->
        binding.cloudBackupListItem
            .getEndComponentViewStub<LinearLayout>()
            ?.findViewById<TextView>(R.id.statusTextView)
            ?.setText(if (enabled) R.string.on else R.string.off)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDialogSavedStateListener()
        initObservers()
        initUi()
        settingsViewModel.initSettingsPreviewFlow()
    }

    private fun initUi() {
        with(binding) {
            securityListItem.setOnClickListener { onSecurityClick() }
            contactsListItem.setOnClickListener { onContactsClick() }
            notificationListItem.setOnClickListener { onNotificationClick() }
            walletConnectListItem.setOnClickListener { onWalletConnectSessionsClick() }
            currencyListItem.setOnClickListener { onCurrencyClick() }
            themeListItem.setOnClickListener { onThemeClick() }
            supportCenterListItem.setOnClickListener { onSupportCenterClick() }
            rateListItem.setOnClickListener { onRateClick() }
            termsAndServicesListItem.setOnClickListener { onTermsAndServicesClick() }
            privacyPolicyListItem.setOnClickListener { onPrivacyPolicyClick() }
            developerListItem.setOnClickListener { onDeveloperSettingsClick() }
            logoutButton.setOnClickListener { onLogoutClick() }
            versionCodeTextView.apply {
                text = getVersionText()
                setOnClickListener { settingsViewModel.enableDeveloperOptions() }
            }
            passkeysListItem.apply {
                if (settingsViewModel.isPasskeysFeatureEnabled()) show() else hide()
                setOnClickListener { settingsViewModel.onPasskeysClick() }
            }
            cloudBackupListItem.apply {
                if (settingsViewModel.isBackupFeatureEnabled()) show() else hide()
                setOnClickListener { nav(SettingsFragmentDirections.actionSettingsFragmentToBackupFragment()) }
            }
        }
    }

    private fun getVersionText(): String {
        val versionName = getString(R.string.version_format, BuildConfig.VERSION_NAME)

        val versionText = if (BuildConfig.DEBUG) {
            versionName + " (${BuildConfig.FLAVOR.uppercase()} - DEBUG)"
        } else if (BuildConfig.FLAVOR.uppercase() != "PROD") {
            versionName + " (${BuildConfig.FLAVOR.uppercase()})"
        } else {
            versionName
        }

        return versionText + "\n (${BuildConfig.VERSION_CODE} - ${BuildConfig.GitHash})"
    }

    private fun initObservers() {
        with(settingsViewModel.settingsPreviewFlow) {
            collectLatestOnLifecycle(
                flow = map { it?.firebaseInstanceId },
                collection = firebaseInstanceIdCollector
            )
            collectLatestOnLifecycle(
                flow = settingsViewModel.viewEvent,
                collection = viewEventCollector,
                state = State.CREATED
            )
        }
        collectLatestOnLifecycle(
            flow = settingsViewModel.isCloudBackupEnabledFlow,
            collection = cloudBackupEnabledCollector
        )
    }

    private fun onContactsClick() {
        nav(SettingsFragmentDirections.actionSettingsFragmentToContactsFragment())
    }

    private fun onSupportCenterClick() {
        context?.openSupportCenterUrl()
    }

    private fun onSecurityClick() {
        nav(SettingsFragmentDirections.actionSettingsFragmentToSecurityNavigation())
    }

    private fun showToast(message: String) {
        devOptionsToast?.cancel()
        devOptionsToast = Toast.makeText(context, message, Toast.LENGTH_SHORT).also {
            it.show()
        }
    }

    private fun initDialogSavedStateListener() {
        startSavedStateListener(R.id.settingsFragment) {
            useSavedStateValue<Boolean>(WARNING_CONFIRMATION_KEY) { isConfirmed ->
                if (isConfirmed) {
                    settingsViewModel.deleteAllData()
                }
            }
        }
    }

    private fun navigateToDataClearedBottomSheet() {
        nav(
            MainNavigationDirections.actionGlobalSingleButtonBottomSheet(
                titleAnnotatedString = AnnotatedString(R.string.your_data_has_been),
                drawableResId = R.drawable.ic_check_72dp
            )
        )
    }

    private fun onDeveloperSettingsClick() {
        nav(SettingsFragmentDirections.actionSettingsFragmentToDeveloperSettingsFragment())
    }

    private fun onCurrencyClick() {
        nav(SettingsFragmentDirections.actionSettingsFragmentToCurrencySelectionFragment())
    }

    private fun onThemeClick() {
        nav(SettingsFragmentDirections.actionSettingsFragmentToThemeSelectionFragment())
    }

    private fun onNotificationClick() {
        nav(SettingsFragmentDirections.actionSettingsFragmentToNotificationSettingsFragment(showDoneButton = false))
    }

    private fun onRateClick() {
        nav(SettingsFragmentDirections.actionSettingsFragmentToRateExperienceBottomSheet())
    }

    private fun onWalletConnectSessionsClick() {
        nav(SettingsFragmentDirections.actionSettingsFragmentToWalletConnectSessionsFragment())
    }

    private fun onLogoutClick() {
        val warningConfirmation = WarningConfirmation(
            titleRes = R.string.delete_all_data,
            descriptionRes = R.string.you_are_about_to_delete,
            drawableRes = R.drawable.ic_trash,
            positiveButtonTextRes = R.string.yes_remove_all_accounts,
            negativeButtonTextRes = R.string.keep_it
        )
        nav(SettingsFragmentDirections.actionSettingsFragmentToWarningConfirmationNavigation(warningConfirmation))
    }

    private fun onTermsAndServicesClick() {
        context?.openTermsAndServicesUrl()
    }

    private fun onPrivacyPolicyClick() {
        context?.openPrivacyPolicyUrl()
    }
}
