@file:Suppress("EmptyFunctionBlock", "Unused", "LongMethod")
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

package com.algorand.android.modules.addaccount.intro.preview

import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import com.algorand.android.R
import com.algorand.android.models.AccountCreation
import com.algorand.android.models.Result
import com.algorand.android.modules.addaccount.intro.domain.model.AddAccountIntroPreview
import com.algorand.android.modules.addaccount.intro.domain.usecase.CreateAlgo25Account
import com.algorand.android.modules.addaccount.intro.domain.usecase.CreateHdKeyAccount
import com.algorand.android.modules.addaccount.intro.domain.usecase.GetAddAccountIntroPreviewUseCase
import com.algorand.android.modules.addaccount.intro.mapper.AddAccountIntroPreviewDecider
import com.algorand.android.modules.addaccount.intro.mapper.AddAccountIntroPreviewMapper
import com.algorand.android.modules.addaccount.intro.view.AddAccountIntroScreen
import com.algorand.android.modules.addaccount.intro.view.AddAccountIntroScreenListener
import com.algorand.android.modules.addaccount.intro.viewmodel.AddAccountIntroViewModel
import com.algorand.android.modules.addaccount.intro.viewmodel.AddAccountIntroViewModel.ViewState
import com.algorand.android.modules.tracking.onboarding.register.registerintro.OnboardingCreateNewAccountEventTracker
import com.algorand.android.modules.tracking.onboarding.register.registerintro.OnboardingWelcomeAccountRecoverEventTracker
import com.algorand.android.modules.tracking.onboarding.register.registerintro.RegisterIntroFragmentEventTracker
import com.algorand.android.repository.RegistrationRepository
import com.algorand.android.sharedpref.RegistrationSkipLocalSource
import com.algorand.android.ui.compose.preview.PeraPreviewLightDark
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.usecase.RegistrationUseCase
import com.algorand.android.utils.analytics.CreationType
import com.algorand.wallet.account.local.domain.usecase.GetHasAnyHdSeedId
import com.algorand.wallet.account.local.domain.usecase.IsThereAnyLocalAccount
import com.algorand.wallet.analytics.domain.service.PeraEventTracker
import com.algorand.wallet.remoteconfig.domain.usecase.IsFeatureToggleEnabled
import com.algorand.wallet.viewmodel.StateDelegate

@PeraPreviewLightDark
@Composable
fun AddAccountIntroScreenPreview() {
    PeraTheme {
        val listener = object : AddAccountIntroScreenListener {
            override fun onAddAccountClick() {}
            override fun onAddJointAccountClick() {}
            override fun onImportAccountClick() {}
            override fun onWatchAddressClick() {}
            override fun onCreateUniversalWalletClick() {}
            override fun onCreateAlgo25AccountClick() {}
            override fun onCloseClick() {}
        }
        AddAccountIntroScreen(
            listener = listener,
            viewModel = getMockViewModel()
        )
    }
}

@Suppress("UNUSED_VARIABLE", "UNUSED_PARAMETER", "UNCHECKED_CAST")
private fun getMockViewModel(): AddAccountIntroViewModel {
    val addAccountIntroPreviewDecider = AddAccountIntroPreviewDecider()
    val addAccountIntroPreviewMapper = AddAccountIntroPreviewMapper(addAccountIntroPreviewDecider)
    val mockSharedPreferences = object : SharedPreferences {
        override fun contains(key: String?): Boolean = false
        override fun edit(): SharedPreferences.Editor = object : SharedPreferences.Editor {
            override fun putString(key: String?, value: String?): SharedPreferences.Editor = this
            override fun putStringSet(key: String?, values: MutableSet<String>?): SharedPreferences.Editor = this
            override fun putInt(key: String?, value: Int): SharedPreferences.Editor = this
            override fun putLong(key: String?, value: Long): SharedPreferences.Editor = this
            override fun putFloat(key: String?, value: Float): SharedPreferences.Editor = this
            override fun putBoolean(key: String?, value: Boolean): SharedPreferences.Editor = this
            override fun remove(key: String?): SharedPreferences.Editor = this
            override fun clear(): SharedPreferences.Editor = this
            override fun commit(): Boolean = true
            override fun apply() = Unit
        }
        override fun getAll(): MutableMap<String, *> = mutableMapOf<String, Any>()
        override fun getBoolean(key: String?, defValue: Boolean): Boolean = defValue
        override fun getFloat(key: String?, defValue: Float): Float = defValue
        override fun getInt(key: String?, defValue: Int): Int = defValue
        override fun getLong(key: String?, defValue: Long): Long = defValue
        override fun getString(key: String?, defValue: String?): String? = defValue
        override fun getStringSet(
            key: String?,
            defValues: MutableSet<String>?
        ): MutableSet<String>? = defValues

        override fun registerOnSharedPreferenceChangeListener(
            listener: SharedPreferences.OnSharedPreferenceChangeListener?
        ) = Unit

        override fun unregisterOnSharedPreferenceChangeListener(
            listener: SharedPreferences.OnSharedPreferenceChangeListener?
        ) = Unit
    }
    val registrationSkipLocalSource = RegistrationSkipLocalSource(mockSharedPreferences)
    val registrationRepository = RegistrationRepository(registrationSkipLocalSource)
    val registrationUseCase = RegistrationUseCase(registrationRepository)
    val peraEventTracker = object : PeraEventTracker {
        override suspend fun logEvent(eventName: String) = Unit
        override suspend fun logEvent(eventName: String, payloadMap: Map<String, Any>) = Unit
    }
    val onboardingCreateNewAccountEventTracker = OnboardingCreateNewAccountEventTracker(
        peraEventTracker,
        registrationUseCase
    )
    val onboardingWelcomeAccountRecoverEventTracker = OnboardingWelcomeAccountRecoverEventTracker(
        peraEventTracker,
        registrationUseCase
    )
    val registerIntroFragmentEventTracker = RegisterIntroFragmentEventTracker(
        onboardingCreateNewAccountEventTracker,
        onboardingWelcomeAccountRecoverEventTracker
    )
    val hasAnyHdSeedId = GetHasAnyHdSeedId { true }
    val isThereAnyLocalAccount = IsThereAnyLocalAccount { false }
    val getAddAccountIntroPreview = GetAddAccountIntroPreviewUseCase(
        addAccountIntroPreviewMapper = addAccountIntroPreviewMapper,
        hasAnyHdSeedId = hasAnyHdSeedId,
        isThereAnyLocalAccount = isThereAnyLocalAccount
    )
    val createHdKeyAccount = CreateHdKeyAccount {
        Result.Success(
            AccountCreation(
                address = "PREVIEW_HD_ADDRESS",
                customName = null,
                isBackedUp = false,
                type = AccountCreation.Type.HdKey(
                    byteArrayOf(),
                    byteArrayOf(),
                    byteArrayOf(),
                    0,
                    0,
                    0,
                    0,
                    null
                ),
                creationType = CreationType.CREATE
            )
        )
    }
    val createAlgo25Account = object : CreateAlgo25Account {
        override suspend fun invoke(): Result<AccountCreation> {
            return Result.Success(
                AccountCreation(
                    address = "PREVIEW_ALGO25_ADDRESS",
                    customName = null,
                    isBackedUp = false,
                    type = AccountCreation.Type.Algo25(byteArrayOf()),
                    creationType = CreationType.CREATE
                )
            )
        }
    }
    val isFeatureToggleEnabled = IsFeatureToggleEnabled { true }
    val savedStateHandle = SavedStateHandle()

    val stateDelegate = StateDelegate<ViewState>().apply {
        setDefaultState(
            ViewState.Content(
                AddAccountIntroPreview(
                    titleRes = R.string.add_an_account,
                    isSkipButtonVisible = true,
                    isCloseButtonVisible = false,
                    hasHdWallet = true
                )
            )
        )
    }

    return AddAccountIntroViewModel(
        stateDelegate = stateDelegate,
        getAddAccountIntroPreview = getAddAccountIntroPreview,
        registrationUseCase = registrationUseCase,
        createHdKeyAccount = createHdKeyAccount,
        createAlgo25Account = createAlgo25Account,
        isFeatureToggleEnabled = isFeatureToggleEnabled,
        peraEventTracker = peraEventTracker,
        registerIntroFragmentEventTracker = registerIntroFragmentEventTracker,
        savedStateHandle = savedStateHandle
    )
}
