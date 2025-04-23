/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.accountcore.ui.accountselection.usecase

import com.algorand.android.modules.accountcore.ui.mapper.AccountItemConfigurationMapper
import com.algorand.android.modules.accountcore.ui.model.BaseItemConfiguration
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.modules.accounts.lite.domain.model.AccountLite
import javax.inject.Inject

internal class CreateNotLoadedAccountConfigurationUseCase @Inject constructor(
    private val accountItemConfigurationMapper: AccountItemConfigurationMapper,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview
) : CreateNotLoadedAccountConfiguration {

    override suspend fun invoke(accountLite: AccountLite): BaseItemConfiguration.AccountItemConfiguration {
        return accountItemConfigurationMapper(
            accountAddress = accountLite.address,
            accountDisplayName = getAccountDisplayName(accountLite),
            accountIconDrawablePreview = getAccountIconDrawablePreview(accountLite),
            showWarningIcon = true,
            accountType = accountLite.cachedInfo?.type
        )
    }
}
