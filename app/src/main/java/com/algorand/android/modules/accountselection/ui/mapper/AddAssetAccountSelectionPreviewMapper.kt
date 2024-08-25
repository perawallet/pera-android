/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.accountselection.ui.mapper

import com.algorand.android.accountcore.ui.accountselection.model.BaseAccountSelectionListItem
import com.algorand.android.modules.accountselection.ui.model.AddAssetAccountSelectionPreview
import javax.inject.Inject

class AddAssetAccountSelectionPreviewMapper @Inject constructor() {

    fun mapToAddAssetSelectionPreview(
        accountSelectionListItems: List<BaseAccountSelectionListItem>
    ): AddAssetAccountSelectionPreview {
        return AddAssetAccountSelectionPreview(accountSelectionListItems)
    }
}
