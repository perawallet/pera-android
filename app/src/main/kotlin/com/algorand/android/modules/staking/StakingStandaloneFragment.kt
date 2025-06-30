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

package com.algorand.android.modules.staking

import android.os.Bundle
import android.view.View
import com.algorand.android.discover.utils.getCustomUrl
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.utils.delegation.bottomnavfragment.BottomNavBarFragmentDelegation
import com.algorand.android.utils.delegation.bottomnavfragment.BottomNavBarFragmentDelegationImpl
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class StakingStandaloneFragment : BaseStakingFragment(),
    BottomNavBarFragmentDelegation by BottomNavBarFragmentDelegationImpl() {

    override val fragmentConfiguration: FragmentConfiguration = FragmentConfiguration(
        isBottomBarNeeded = true
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerBottomNavBarFragmentDelegation(this)
    }

    override fun getStakingUrl(): String {
        return getCustomUrl(
            url = stakingViewModel.getStakingBaseUrl(),
            themePreference = webViewThemeHelper.getWebViewThemeFromThemePreference(binding.root.context),
            currency = stakingViewModel.getPrimaryCurrencyId(),
            locale = Locale.getDefault().language,
            version = STAKING_STANDALONE_VERSION
        )
    }

    private companion object {
        const val STAKING_STANDALONE_VERSION = "5"
    }
}
