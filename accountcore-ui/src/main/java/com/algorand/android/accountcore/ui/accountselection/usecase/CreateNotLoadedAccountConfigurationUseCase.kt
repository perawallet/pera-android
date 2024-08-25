/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.accountcore.ui.accountselection.usecase

import com.algorand.android.accountcore.ui.mapper.AccountItemConfigurationMapper
import com.algorand.android.accountcore.ui.model.BaseItemConfiguration
import com.algorand.android.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.accountcore.ui.usecase.GetAccountIconDrawablePreview
import javax.inject.Inject

internal class CreateNotLoadedAccountConfigurationUseCase @Inject constructor(
    private val accountItemConfigurationMapper: AccountItemConfigurationMapper,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview
) : CreateNotLoadedAccountConfiguration {

    override suspend fun invoke(address: String): BaseItemConfiguration.AccountItemConfiguration {
        return accountItemConfigurationMapper(
            accountAddress = address,
            accountDisplayName = getAccountDisplayName(address),
            accountIconDrawablePreview = getAccountIconDrawablePreview(address),
            showWarningIcon = true,
            accountType = null
        )
    }
}
