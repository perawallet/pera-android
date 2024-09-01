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

package com.algorand.android.swapui

import androidx.annotation.StringRes
import com.algorand.android.designsystem.AnnotatedString
import com.algorand.android.swapui.assetswap.model.SwapError

interface GetSwapError {

    operator fun invoke(@StringRes errorResId: Int, @StringRes title: Int? = null): SwapError

    operator fun invoke(description: AnnotatedString, title: AnnotatedString? = null): SwapError

    operator fun invoke(message: String): SwapError
}