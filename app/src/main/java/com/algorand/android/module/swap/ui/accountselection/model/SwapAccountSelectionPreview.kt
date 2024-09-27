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

package com.algorand.android.module.swap.ui.accountselection.model

import com.algorand.android.module.account.core.ui.accountselection.model.BaseAccountSelectionListItem
import com.algorand.android.module.asset.utils.AssetAdditionPayload
import com.algorand.android.module.drawable.AnnotatedString
import com.algorand.android.module.foundation.Event

data class SwapAccountSelectionPreview(
    val accountListItems: List<BaseAccountSelectionListItem.BaseAccountItem>,
    val isLoading: Boolean,
    val navToSwapNavigationEvent: Event<SwapAccountSelectionNavDirection>?,
    val isEmptyStateVisible: Boolean,
    val errorEvent: Event<AnnotatedString>?,
    val optInToAssetEvent: Event<AssetAdditionPayload>?
)
