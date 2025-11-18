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

package com.algorand.android.modules.accountcore.ui.model

import com.algorand.android.models.ButtonConfiguration
import com.algorand.android.models.GovernorIconResource
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.wallet.account.detail.domain.model.AccountType
import java.math.BigDecimal

sealed class BaseItemConfiguration {

    abstract val primaryValueText: String?
    abstract val secondaryValueText: String?

    abstract val primaryValue: BigDecimal?
    abstract val secondaryValue: BigDecimal?

    abstract val actionButtonConfiguration: ButtonConfiguration?
    abstract val checkButtonConfiguration: ButtonConfiguration?
    abstract val dragButtonConfiguration: ButtonConfiguration?

    data class AccountItemConfiguration(
        override val primaryValueText: String? = null,
        override val secondaryValueText: String? = null,
        override val actionButtonConfiguration: ButtonConfiguration? = null,
        override val checkButtonConfiguration: ButtonConfiguration? = null,
        override val dragButtonConfiguration: ButtonConfiguration? = null,
        override val primaryValue: BigDecimal? = null,
        override val secondaryValue: BigDecimal? = null,
        val showWarning: Boolean? = null,
        val accountAddress: String,
        val accountIconDrawablePreview: AccountIconDrawablePreview? = null,
        val governorIconResource: GovernorIconResource? = null,
        val accountDisplayName: AccountDisplayName? = null,
        val accountType: AccountType? = null,
        val accountAssetCount: Int? = null,
        val startSmallIconResource: Int? = null
    ) : BaseItemConfiguration()
}
