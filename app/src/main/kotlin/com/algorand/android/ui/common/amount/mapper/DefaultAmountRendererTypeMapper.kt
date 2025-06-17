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

package com.algorand.android.ui.common.amount.mapper

import com.algorand.android.ui.common.amount.AmountRenderer
import com.algorand.wallet.privacy.domain.model.PrivacyMode
import javax.inject.Inject

internal class DefaultAmountRendererTypeMapper @Inject constructor() : AmountRendererTypeMapper {

    override fun invoke(privacyMode: PrivacyMode): AmountRenderer.RenderType {
        return when (privacyMode) {
            PrivacyMode.Enabled -> AmountRenderer.RenderType.Hidden()
            PrivacyMode.Disabled -> AmountRenderer.RenderType.Plain
        }
    }
}
