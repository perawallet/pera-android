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

import androidx.navigation.fragment.navArgs
import com.algorand.android.BuildConfig.STAKING_URL
import com.algorand.android.discover.utils.getCustomUrl
import com.algorand.android.models.FragmentConfiguration
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class StakingFragment : BaseStakingFragment() {

    override val fragmentConfiguration: FragmentConfiguration = FragmentConfiguration()

    private val args: StakingFragmentArgs by navArgs()

    override fun getStakingUrl(): String {
        val stakingUrl = "$STAKING_URL/${args.path.orEmpty()}"
        return getCustomUrl(
            url = stakingUrl,
            themePreference = webViewThemeHelper.getWebViewThemeFromThemePreference(binding.root.context),
            currency = stakingViewModel.getPrimaryCurrencyId(),
            locale = Locale.getDefault().language
        )
    }
}
